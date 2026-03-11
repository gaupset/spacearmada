package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.util.FirebaseJson;

import java.util.Random;

public class LobbyHandler {
    private String lobbyID;
    private String playerID;

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
                    System.out.println("Exception checkDatabase");
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
        System.out.println("create Database");
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
        LobbyModel lobby = new LobbyModel();
        lobby.players.put(playerID, true);
        String body = FirebaseJson.toJson(lobby);
        System.out.println("createLobby: " + body);

        FirebaseService.getInstance().putDbData("lobbies/" + newId, body, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("create Lobby success");
                lobbyID = newId;
                updatePlayerLobby(newId, callback);
            }

            @Override
            public void onFailure(String error) {
                System.out.println("create Lobby failure");
                callback.onFailure(error);
            }
        });
    }

    public void joinLobby(final String lobbyId, final LobbyCallback callback) {
        FirebaseService.getInstance().getDbData("lobbies/" + lobbyId, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("null")) {
                    callback.onFailure("Lobby not found");
                    return;
                }
                JsonValue lobbyJson = new JsonReader().parse(response);
                if (lobbyJson != null && !lobbyJson.getBoolean("gamestarted")) {
                    String patchBody = "{\"" + playerID + "\": true}";
                    FirebaseService.getInstance().patchDbData("lobbies/" + lobbyId + "/players", patchBody, new FirebaseService.FirebaseCallback() {
                        @Override
                        public void onSuccess(String response) {
                            lobbyID = lobbyId;
                            updatePlayerLobby(lobbyId, callback);
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

    public void getLobbyStatus(final LobbyStatusCallback callback) {
        if (lobbyID == null) return;
        FirebaseService.getInstance().getDbData("lobbies/" + lobbyID, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                if (response == null || response.equals("null")) {
                    callback.onFailure("Lobby no longer exists");
                    return;
                }
                JsonValue lobbyJson = new JsonReader().parse(response);
                callback.onUpdate(lobbyJson);
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    public void startGame(final LobbyCallback callback) {
        if (lobbyID == null) return;
        String body = "{\"gamestarted\": true}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyID, body, new FirebaseService.FirebaseCallback() {
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

    private void updatePlayerLobby(String lobbyId, final LobbyCallback callback) {
        String body = "{\"currentLobby\": \"" + lobbyId + "\"}";
        FirebaseService.getInstance().patchDbData("players/" + playerID, body, new FirebaseService.FirebaseCallback() {
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

    public void setPlayerID(String id) {
        this.playerID = id;
    }

    public String getLobbyID() {
        return lobbyID;
    }
}
