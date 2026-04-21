package no.ntnu.tdt4240.project.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.Random;
import java.util.UUID;
import no.ntnu.tdt4240.project.model.Sabotage;
import no.ntnu.tdt4240.project.data.LobbyPlayer;
import no.ntnu.tdt4240.project.util.FirebaseJson;


public class LobbyService {
    private static final String PREFS_NAME = "spacearmada_prefs";
    private static final String PREF_PLAYER_NAME = "player_name";
    private static final String SERVER_TIMESTAMP = "{\".sv\": \"timestamp\"}";

    private String lobbyID;
    public String lobbyUserID; // Unique per lobby session
    private String playerID;       // Persistent player name
    private final Preferences prefs;

    public LobbyService() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        String stored = prefs.getString(PREF_PLAYER_NAME, null);
        if (stored != null && !stored.isEmpty()) {
            playerID = stored;
        }
    }

    public interface LobbyCallback {
        void onSuccess(String success);
        void onFailure(String error);
    }

    public interface LobbyStatusCallback {
        void onUpdate(JsonValue lobbyData);
        void onFailure(String error);
    }

    public void createLobby(final LobbyCallback callback) {
        final String newId = String.format("%06d", new Random().nextInt(1000000));
        lobbyUserID = UUID.randomUUID().toString();

        String lpJson = buildPlayerJson();

        String lobbyJson = "{" +
            "\"gameStarted\": false," +
            "\"gameEnded\": false," +
            "\"lobbyCreatedAt\": " + SERVER_TIMESTAMP + "," +
            "\"players\": {\"" + lobbyUserID + "\": " + lpJson + "}" +
            "}";

        FirebaseService.getInstance().putDbData("lobbies/" + newId, lobbyJson, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                lobbyID = newId;
                callback.onSuccess(response);
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void joinLobby(final String lobbyId, final LobbyCallback callback) {
        lobbyUserID = UUID.randomUUID().toString();
        FirebaseService.getInstance().getDbData("lobbies/" + lobbyId, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("null")) {
                    callback.onFailure("Lobby not found");
                    return;
                }
                JsonValue lobbyJson = new JsonReader().parse(response);
                if (lobbyJson != null && !lobbyJson.getBoolean("gameStarted", false)) {
                    String lpJson = buildPlayerJson();

                    FirebaseService.getInstance().putDbData("lobbies/" + lobbyId + "/players/" + lobbyUserID, lpJson, new FirebaseService.FirebaseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            lobbyID = lobbyId;
                            sendHeartbeat();
                            callback.onSuccess(response);
                        }
                        @Override
                        public void onFailure(String error) {
                            callback.onFailure(error);
                        }
                    });
                } else {
                    callback.onFailure("Lobby already started");
                }
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    private String buildPlayerJson() {
        LobbyPlayer lp = new LobbyPlayer();
        lp.actualName = playerID;
        lp.personalHighScore = ScoreService.getInstance().getHighScore();
        lp.inGameOverScreen = false;
        lp.gameOver = false;
        lp.leftLobby = false;
        lp.score = 0;
        String base = FirebaseJson.toJson(lp);
        String inner = base.substring(1, base.length() - 1);
        return "{" + inner + ",\"lastTimeOnline\":" + SERVER_TIMESTAMP + "}";
    }

    public void sendHeartbeat() {
        if (lobbyID == null || lobbyUserID == null) return;
        String path = "lobbies/" + lobbyID + "/players/" + lobbyUserID + "/lastTimeOnline";
        FirebaseService.getInstance().putDbData(path, SERVER_TIMESTAMP, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {}
        });
    }

    public void updateScore(int score) {
        if (lobbyID == null || lobbyUserID == null) return;
        String path = "lobbies/" + lobbyID + "/players/" + lobbyUserID + "/score";
        FirebaseService.getInstance().putDbData(path, String.valueOf(score), new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                sendHeartbeat();
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    public void setPlayerGameOver(int finalScore) {
        if (lobbyID == null || lobbyUserID == null) return;
        String body = "{\"gameOver\": true, \"inGameOverScreen\": true, \"score\": " + finalScore + ", \"lastTimeOnline\": " + SERVER_TIMESTAMP + "}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID + "/players/" + lobbyUserID, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {}
        });
    }

    public void leaveLobby(final LobbyCallback callback) {
        if (lobbyID == null || lobbyUserID == null) {
            if (callback != null) callback.onSuccess("Already left");
            return;
        }

        String body = "{\"leftLobby\": true, \"gameOver\": true, \"inGameOverScreen\": false}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID + "/players/" + lobbyUserID, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                lobbyID = null;
                lobbyUserID = null;
                if (callback != null) callback.onSuccess("Left");
            }
            @Override
            public void onFailure(String error) {
                if (callback != null) callback.onFailure(error);
            }
        });
    }

    public void startGame(final LobbyCallback callback) {
        if (lobbyID == null || lobbyUserID == null) return;
        FirebaseService.getInstance().callGameHandler(lobbyID, lobbyUserID, "startGame", new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (callback != null) callback.onSuccess(response);
            }
            @Override
            public void onFailure(String error) {
                if (callback != null) callback.onFailure(error);
            }
        });
    }

    public void setSabotage(Sabotage sabotage) {
        if (lobbyID == null || lobbyUserID == null) return;
        String extras = "\"sabotage\":" + FirebaseJson.toJson(sabotage);
        FirebaseService.getInstance().callGameHandler(lobbyID, lobbyUserID, "sendSabotage", extras, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {
                Gdx.app.error("LobbyHandler", "sendSabotage failed: " + error);
            }
        });
    }

    public void delSabotage(){
        String path = "lobbies/" + lobbyID + "/players/" + lobbyUserID+ "/sabotage";
        FirebaseService.getInstance().deleteDbData(path, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {}
        });
    }

    public void getLobbyStatus(final LobbyStatusCallback callback) {
        if (lobbyID == null) return;
        FirebaseService.getInstance().getDbData("lobbies/" + lobbyID, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("null")) {
                    callback.onFailure("Not found");
                    return;
                }
                callback.onUpdate(new JsonReader().parse(response));
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void pingGameHandler() {
        if (lobbyID == null || lobbyUserID == null) return;
        FirebaseService.getInstance().callGameHandler(lobbyID, lobbyUserID, null, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {}
        });
    }

    public void setPlayerID(String id) {
        this.playerID = id;
        prefs.putString(PREF_PLAYER_NAME, id == null ? "" : id);
        prefs.flush();
    }

    public String getPlayerID() {
        return playerID;
    }

    public String getLobbyID() { return lobbyID; }
}
