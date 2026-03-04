package com.invaders99.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.invaders99.util.AppConfig;
import com.invaders99.util.FirebaseJson;

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

    private FirebaseService(AppConfig config) {
        this.functionsBaseUrl = config.firebaseBaseUrl;
        this.firestoreBaseUrl = config.firestoreBaseUrl;
        this.databaseBaseUrl = config.databaseBaseUrl;
        this.projectId = config.projectId;
    }

    public static void init() {
        instance = new FirebaseService(AppConfig.get());
    }

    public static FirebaseService getInstance() {
        return instance;
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
        String refUrl = databaseBaseUrl + "/_connectivity/test.json?ns=" + projectId;

        ConnectivityProbe probe = new ConnectivityProbe();
        String body = FirebaseJson.toJson(probe);

        // Insert
        sendRequest(Net.HttpMethods.PUT, refUrl, body, new FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                // Select
                sendRequest(Net.HttpMethods.GET, refUrl, null, new FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Delete
                        sendRequest(Net.HttpMethods.DELETE, refUrl, null, new FirebaseCallback() {
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
