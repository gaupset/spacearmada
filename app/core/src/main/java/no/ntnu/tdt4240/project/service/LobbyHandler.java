package no.ntnu.tdt4240.project.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.Gdx;
import java.util.Random;
import java.util.UUID;
import no.ntnu.tdt4240.project.service.FirebaseService;
import no.ntnu.tdt4240.project.data.LobbyPlayer;
import no.ntnu.tdt4240.project.util.FirebaseJson;


public class LobbyHandler {
    private String lobbyID;
    public String lobbyUserID; // Unique per lobby session
    private String playerID;       // Persistent player ID

    private static final String SERVER_TIMESTAMP = "{\".sv\": \"timestamp\"}";

    public interface LobbyCallback {
        void onSuccess(String success);
        void onFailure(String error);
    }

    public interface LobbyStatusCallback {
        void onUpdate(JsonValue lobbyData);
        void onFailure(String error);
    }

    public void checkDatabaseFormat(final LobbyCallback callback) {
        FirebaseService.getInstance().getDbData("", new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                try {
                    JsonValue root = new JsonReader().parse(response);
                    if (root != null && root.has("lobbies") && root.has("players")) {
                        callback.onSuccess(response);
                    } else {
                        createDatabase(callback);
                    }
                } catch (Exception e) {
                    createDatabase(callback);
                }
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void createDatabase(final LobbyCallback callback) {
        String initialData = "{\"lobbies\": {}, \"players\": {}}";
        FirebaseService.getInstance().putDbData("", initialData, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                callback.onSuccess(response);
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void createLobby(final LobbyCallback callback) {
        final String newId = String.format("%06d", new Random().nextInt(1000000));
        lobbyUserID = UUID.randomUUID().toString();

        LobbyPlayer lp = createInitialLobbyPlayer();
        String lpJson = FirebaseJson.toJson(lp).replace("null", SERVER_TIMESTAMP);

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
                    LobbyPlayer lp = createInitialLobbyPlayer();
                    String lpJson = FirebaseJson.toJson(lp).replace("null", SERVER_TIMESTAMP);

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

    private LobbyPlayer createInitialLobbyPlayer() {
        LobbyPlayer lp = new LobbyPlayer();
        lp.actualName = playerID;
        lp.personalHighScore = ScoreService.getInstance().getHighScore();
        lp.inGameOverScreen = false;
        lp.gameOver = false;
        lp.leftLobby = false;
        lp.score = 0;
        lp.lastTimeOnline = SERVER_TIMESTAMP;
        return lp;
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

    public void setSabotage(Sabotage sabotage){
        getLobbyStatus(new LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue response) {
                String otherPlayer = getSabotageTargetPlayerId(response);
                writeSabotage(sabotage, otherPlayer);
            }
            @Override
            public void onFailure(String error) {}
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

    private void writeSabotage(Sabotage sabotage, String otherPlayerID) {
        if (otherPlayerID == null || otherPlayerID.isEmpty()) return;
        String path = "lobbies/" + lobbyID + "/players/" + otherPlayerID + "/sabotage";
        String sabotageJson = FirebaseJson.toJson(sabotage);
        FirebaseService.getInstance().putDbData(path, sabotageJson, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {
                Gdx.app.error("LobbyHandler", "writeSabotage failed: " + error);
            }
        });
    }

    private String getSabotageTargetPlayerId(JsonValue lobbyData) {
        JsonValue players = lobbyData.get("players");
        if (players == null || lobbyUserID == null) return "";
        JsonValue me = players.get(lobbyUserID);
        if (me == null) return "";
        return me.getString("sabotageTargetId", "");
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
    }

    public String getLobbyID() { return lobbyID; }
}
