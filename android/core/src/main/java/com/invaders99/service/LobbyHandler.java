package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.model.LobbyPlayer;
import com.invaders99.util.FirebaseJson;

import java.util.Random;
import java.util.UUID;

public class LobbyHandler {
    private String lobbyID;
    private String sessionPlayerID; // Unique per lobby session
    private String playerID;        // Persistent player ID

    private static final String SERVER_TIMESTAMP = "{\".sv\": \"timestamp\"}";
    private static final long TIMEOUT_MS = 30000;

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
        sessionPlayerID = UUID.randomUUID().toString();

        LobbyPlayer lp = createInitialLobbyPlayer();
        String lpJson = FirebaseJson.toJson(lp).replace("null", SERVER_TIMESTAMP);

        String lobbyJson = "{" +
                "\"gameStarted\": false," +
                "\"gameEnded\": false," +
                "\"lobbyCreatedAt\": " + SERVER_TIMESTAMP + "," +
                "\"players\": {\"" + sessionPlayerID + "\": " + lpJson + "}" +
                "}";

        FirebaseService.getInstance().putDbData("lobbies/" + newId, lobbyJson, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                lobbyID = newId;
                sendHeartbeat();
                callback.onSuccess(response);
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void joinLobby(final String lobbyId, final LobbyCallback callback) {
        sessionPlayerID = UUID.randomUUID().toString();
        FirebaseService.getInstance().getDbData("lobbies/" + lobbyId, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("null")) {
                    callback.onFailure("Lobby not found");
                    return;
                }
                JsonValue lobbyJson = new JsonReader().parse(response);
                if (lobbyJson != null && !lobbyJson.getBoolean("gameStarted")) {
                    LobbyPlayer lp = createInitialLobbyPlayer();
                    String lpJson = FirebaseJson.toJson(lp).replace("null", SERVER_TIMESTAMP);

                    FirebaseService.getInstance().putDbData("lobbies/" + lobbyId + "/players/" + sessionPlayerID, lpJson, new FirebaseService.FirebaseCallback() {
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
        if (lobbyID == null || sessionPlayerID == null) return;
        String path = "lobbies/" + lobbyID + "/players/" + sessionPlayerID + "/lastTimeOnline";
        FirebaseService.getInstance().putDbData(path, SERVER_TIMESTAMP, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {}
        });
    }

    public void updateScore(int score) {
        if (lobbyID == null || sessionPlayerID == null) return;
        String path = "lobbies/" + lobbyID + "/players/" + sessionPlayerID + "/score";
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
        if (lobbyID == null || sessionPlayerID == null) return;
        String body = "{\"gameOver\": true, \"inGameOverScreen\": true, \"score\": " + finalScore + ", \"lastTimeOnline\": " + SERVER_TIMESTAMP + "}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID + "/players/" + sessionPlayerID, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                checkLobbyState();
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    public void checkLobbyState() {
        if (lobbyID == null) return;
        getAndChangeLobbyStatus(new LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue lobbyData) {
                evaluateLobby(lobbyData);
                sendHeartbeat();
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    private void evaluateLobby(JsonValue lobbyData) {

        JsonValue players = lobbyData.get("players");
        if (players == null) return;

        long now = FirebaseService.getInstance().getServerTime();
        boolean gameEnded = lobbyData.getBoolean("gameEnded", false);

        if (!gameEnded) {
            // check whether gameEnded can be set to true
            boolean allFinished = true;
            for (JsonValue p : players) {
                boolean isGameOver = p.getBoolean("gameOver", false);
                boolean isLeft = p.getBoolean("leftLobby", false);
                long lastOnline = p.getLong("lastTimeOnline", 0);

                // A player is active if they are NOT gameOver AND NOT left AND NOT timed out
                if (!isGameOver && !isLeft && (now - lastOnline < TIMEOUT_MS)) {
                    allFinished = false;
                    break;
                }
            }
            if (allFinished) {
                endLobbyGame();
            }
        } else {
            // Check whether lobby can be deleted.
            boolean allGone = true;
            for (JsonValue p : players) {
                boolean left = p.getBoolean("leftLobby", false);
                long lastOnline = p.getLong("lastTimeOnline", 0);
                if (!left && (now - lastOnline < TIMEOUT_MS)) {
                    allGone = false;
                    break;
                }
            }
            if (allGone) {
                deleteLobby();
            }
        }
    }

    private void endLobbyGame() {
        if (lobbyID == null) return;
        String body = "{\"gameEnded\": true, \"gameEndedAt\": " + SERVER_TIMESTAMP + "}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {}
            @Override
            public void onFailure(String error) {}
        });
    }

    private void deleteLobby() {
        if (lobbyID == null) return;
        final String targetId = lobbyID;
        FirebaseService.getInstance().deleteDbData("lobbies/" + targetId, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (targetId.equals(lobbyID)) lobbyID = null;
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    public void leaveLobby(final LobbyCallback callback) {
        if (lobbyID == null || sessionPlayerID == null) {
            if (callback != null) callback.onSuccess("Already left");
            return;
        }

        String body = "{\"leftLobby\": true, \"gameOver\": true, \"leftAt\": " + SERVER_TIMESTAMP + ", \"inGameOverScreen\": false}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID + "/players/" + sessionPlayerID, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                checkLobbyState();
                lobbyID = null;
                sessionPlayerID = null;
                if (callback != null) callback.onSuccess("Left");
            }
            @Override
            public void onFailure(String error) {
                if (callback != null) callback.onFailure(error);
            }
        });
    }

    public void startGame(final LobbyCallback callback) {
        if (lobbyID == null) return;
        String body = "{\"gameStarted\": true}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID, body, new FirebaseService.FirebaseCallback() {
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

    public void getAndChangeLobbyStatus(final LobbyStatusCallback callback) {
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

    public void setPlayerID(String id) {
        this.playerID = id;
    }
    public String getLobbyID() { return lobbyID; }
}
