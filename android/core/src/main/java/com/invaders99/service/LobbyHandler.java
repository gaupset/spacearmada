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
        System.out.println("setPlayerGameOver executed");
        if (lobbyID == null || sessionPlayerID == null) return;
        String body = "{\"gameOver\": true, \"inGameOverScreen\": true, \"score\": " + finalScore + ", \"lastTimeOnline\": " + SERVER_TIMESTAMP + "}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID + "/players/" + sessionPlayerID, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("SetPlayerGameOver: checkLobbyState call");
                checkLobbyState();
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    public void checkLobbyState() {
        if (lobbyID == null) return;
        getLobbyStatus(new LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue lobbyData) {
                evaluateLobby(lobbyData);
            }
            @Override
            public void onFailure(String error) {}
        });
    }

    public void evaluateLobby(JsonValue lobbyData) {
        System.out.println("evaluateLobby");

        JsonValue players = lobbyData.get("players");
        if (players == null) return;

        long now = FirebaseService.getInstance().getServerTime();
        boolean gameStarted = lobbyData.getBoolean("gameStarted", false);
        boolean gameEnded = lobbyData.getBoolean("gameEnded", false);
        long lobbyCreatedAt = lobbyData.getLong("lobbyCreatedAt", 0);

        // in lobbyphase do nothing

        // in gamePhase
        if (!gameEnded && gameStarted) {
            if (shouldGameEnd(players, now)){
                endGame();
            };
        }

        // evaluate whether lobby can be deleted
        if (gameStarted && gameEnded) {
            if (shouldLobbyBeDeleted(players, now)) {
                deleteLobby();
            }
        }
    }

    private boolean shouldLobbyBeDeleted(JsonValue players, long now) {
        int activeCount = 0;
        System.out.println("shouldLobbyBeDeleted");


        for (JsonValue p : players) {
            System.out.println("player: " + p);
            boolean leftLobby = p.getBoolean("leftLobby", false);
            long lastOnline = p.getLong("lastTimeOnline", 0);

            boolean offline = lastOnline > 0 && (now - lastOnline >= TIMEOUT_MS);
            boolean active = !leftLobby && !offline;

            if (active) {
                activeCount++;
            }
        }

        if (activeCount < 1) {
            System.out.println("evaluateLobby: LobbyshouldBeDeleted, activeCount=" + activeCount);
            return true;
        }
        return false;
    }

    private boolean shouldGameEnd(JsonValue players, long now) {
        int activeCount = 0;

        for (JsonValue p : players) {
            boolean isGameOver = p.getBoolean("gameOver", false);
            boolean leftLobby = p.getBoolean("leftLobby", false);
            long lastOnline = p.getLong("lastTimeOnline", 0);

            boolean offline = lastOnline > 0 && (now - lastOnline >= TIMEOUT_MS);
            boolean active = !isGameOver && !leftLobby && !offline;

            if (active) {
                activeCount++;
            }
        }

        if (activeCount <= 1) {
            System.out.println("shouldGameEnd: true: game should end, activeCount=" + activeCount);
            return true;
        }
        return false;
    }


    private void endGame() {
        // sets gameEnded to true
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
                System.out.println("Lobby deleted");
                if (targetId.equals(lobbyID)) lobbyID = null;
            }
            @Override
            public void onFailure(String error) {
                System.out.println("Lobby not deleted: Error");
            }
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

    public void setPlayerID(String id) {
        this.playerID = id;
    }
    public String getLobbyID() { return lobbyID; }
}
