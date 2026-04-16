package no.ntnu.tdt4240.project.service;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import no.ntnu.tdt4240.project.util.AppConfig;
import no.ntnu.tdt4240.project.util.FirebaseJson;

public class FirebaseService {
    private static FirebaseService instance;

    public interface FirebaseCallback {
        void onSuccess(String response);
        void onFailure(String error);
    }

    private final String functionsBaseUrl;
    private final String firestoreBaseUrl;
    private final String databaseBaseUrl;
    private final String projectId;
    private final String dbNsParam;
    private long serverTimeOffset = 0;

    private FirebaseService(AppConfig config) {
        this.functionsBaseUrl = config.firebaseBaseUrl;
        this.firestoreBaseUrl = config.firestoreBaseUrl;
        this.databaseBaseUrl = config.databaseBaseUrl;
        this.projectId = config.projectId;
        // ?ns= is required by the RTDB emulator (HTTP) to select the namespace.
        // Only needs to be included when using emulatrs
        this.dbNsParam = config.databaseBaseUrl.startsWith("http://") ? "?ns=" + config.projectId : "";
        // Disabled: this game does not rely on serverTimeOffset, and this call
        // produces noisy 400 errors against .info/serverTimeOffset via REST.
        //fetchServerTimeOffset();
    }

    public static void init() {
        instance = new FirebaseService(AppConfig.get());
    }

    public static FirebaseService getInstance() {
        return instance;
    }

    public long getServerTime() {
        return System.currentTimeMillis() + serverTimeOffset;
    }

    public void fetchServerTimeOffset() {
        getDbData(".info/serverTimeOffset", new FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    serverTimeOffset = Long.parseLong(response.trim());
                } catch (NumberFormatException e) {
                    Gdx.app.error("FirebaseService", "Failed to parse serverTimeOffset: " + response);
                }
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("FirebaseService", "Failed to fetch serverTimeOffset: " + error);
            }
        });
    }

    // Generic DB methods
    public void getDbData(String path, final FirebaseCallback callback) {
        String url = databaseBaseUrl + "/" + path + ".json" + dbNsParam;
        sendRequest(Net.HttpMethods.GET, url, null, callback);
    }

    public void putDbData(String path, String body, final FirebaseCallback callback) {
        String url = databaseBaseUrl + "/" + path + ".json" + dbNsParam;
        sendRequest(Net.HttpMethods.PUT, url, body, callback);
    }

    public void patchDbData(String path, String body, final FirebaseCallback callback) {
        String url = databaseBaseUrl + "/" + path + ".json" + dbNsParam;
        sendRequest("PATCH", url, body, callback);
    }

    public void postDbData(String path, String body, final FirebaseCallback callback) {
        String url = databaseBaseUrl + "/" + path + ".json" + dbNsParam;
        sendRequest(Net.HttpMethods.POST, url, body, callback);
    }

    public void deleteDbData(String path, final FirebaseCallback callback) {
        String url = databaseBaseUrl + "/" + path + ".json" + dbNsParam;
        sendRequest(Net.HttpMethods.DELETE, url, null, callback);
    }

    public void callGameHandler(String lobbyId, String lobbyUserId, String action, final FirebaseCallback callback) {
        String url = functionsBaseUrl + "/gameHandler";
        String body = "{\"lobbyId\":\"" + lobbyId + "\",\"lobbyUserId\":\"" + lobbyUserId + "\""
            + (action != null ? ",\"action\":\"" + action + "\"" : "")
            + "}";
        sendRequest("POST", url, body, callback);
    }

    public void testConnection(final FirebaseCallback callback) {
        sendRequest(Net.HttpMethods.GET, functionsBaseUrl + "/helloWorld", null, callback);
    }

    public void testFirestore(final FirebaseCallback callback) {
        String docUrl = firestoreBaseUrl
            + "/v1/projects/" + projectId
            + "/databases/(default)/documents/_connectivity/test";

        ConnectivityProbe probe = new ConnectivityProbe();
        String body = FirebaseJson.toFirestoreDocument(probe);

        // Insert
        sendRequest("PATCH", docUrl, body, new FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                // Get
                sendRequest(Net.HttpMethods.GET, docUrl, null, new FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Delete
                        sendRequest(Net.HttpMethods.DELETE, docUrl, null, new FirebaseCallback() {
                            @Override
                            public void onSuccess(String r) {
                                callback.onSuccess("Firestore OK");
                            }

                            @Override
                            public void onFailure(String error) {
                                callback.onSuccess("Firestore OK (cleanup failed: " + error + ")");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure("Firestore read failed: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure("Firestore write failed: " + error);
            }
        });
    }

    public void testDatabase(final FirebaseCallback callback) {
        String refUrl = "_connectivity/test";
        ConnectivityProbe probe = new ConnectivityProbe();
        String body = FirebaseJson.toJson(probe);

        putDbData(refUrl, body, new FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                getDbData(refUrl, new FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        deleteDbData(refUrl, new FirebaseCallback() {
                            @Override
                            public void onSuccess(String r) {
                                callback.onSuccess("Realtime DB OK");
                            }

                            @Override
                            public void onFailure(String error) {
                                callback.onSuccess("Realtime DB OK (cleanup failed: " + error + ")");
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure("RTDB read failed: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure("RTDB write failed: " + error);
            }
        });
    }

    static class ConnectivityProbe {
        long ts = System.currentTimeMillis();
        String source = "connectivity-test";
    }

    private void sendRequest(String method, String url, String body, final FirebaseCallback callback) {
        HttpRequestBuilder builder = new HttpRequestBuilder();
        builder.newRequest().method(method).url(url);

        if (body != null) {
            builder.header("Content-Type", "application/json");
            builder.content(body);
        }

        Net.HttpRequest request = builder.build();

        Gdx.net.sendHttpRequest(request, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final int status = httpResponse.getStatus().getStatusCode();
                final String result = httpResponse.getResultAsString();
                Gdx.app.postRunnable(() -> {
                    if (status >= 200 && status < 300) {
                        callback.onSuccess(result);
                    } else {
                        callback.onFailure("HTTP " + status + ": " + result);
                    }
                });
            }

            @Override
            public void failed(Throwable t) {
                final String msg = t.getMessage();
                Gdx.app.postRunnable(() -> callback.onFailure("Request failed: " + msg));
            }

            @Override
            public void cancelled() {
                Gdx.app.postRunnable(() -> callback.onFailure("Request cancelled"));
            }
        });
    }
}
