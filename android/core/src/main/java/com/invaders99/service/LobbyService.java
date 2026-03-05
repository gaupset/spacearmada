package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.util.FirebaseJson;

import java.util.Random;

public class LobbyService {
    private String lobbyID;
    public String playerID;


    public void checkDatabaseFormat(final FirebaseService.FirebaseCallback callback) {
        FirebaseService.getInstance().getDbData("", new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                JsonValue root = new JsonReader().parse(response);
                if (root != null && root.has("lobbies") && root.has("players")) {
                    callback.onSuccess(response);
                } else {
                    createDatabase(callback);
                }
            }
            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }


    public void createDatabase(final FirebaseService.FirebaseCallback callback) {
        String initialData = "{\n" +
            "  \"lobbies\": {\n" +
            "    \"lobby1\": {\n" +
            "      \"players\": [\n" +
            "        \"playerID1\",\n" +
            "        \"playerID2\"\n" +
            "      ],\n" +
            "      \"gamestarted\": false\n" +
            "    }\n" +
            "  },\n" +
            "  \"players\": {\n" +
            "    \"player1\": {\n" +
            "      \"name\": \"Player 1\",\n" +
            "      \"score\": 0\n" +
            "    }\n" +
            "  }\n" +
            "}";
        FirebaseService.getInstance().putDbData("", initialData, callback);
    }

    public void createLobby(final String lobbyName, final FirebaseService.FirebaseCallback callback) {
        final String newId = String.format("%06d", new Random().nextInt(1000000));
        LobbyModel lobby = new LobbyModel(lobbyName);
        String body = FirebaseJson.toJson(lobby);

        FirebaseService.getInstance().putDbData("lobbies/" + newId, body, new FirebaseService.FirebaseCallback() {
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

    public void joinLobby(final String lobbyId, final String playerId, final FirebaseService.FirebaseCallback callback) {
        // 1. Get current players in lobby
        FirebaseService.getInstance().getDbData("lobbies/" + lobbyId + "/players", new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                // In RTDB, if we push to a list, it might be an array or object.
                // For simplicity, we'll use a PATCH to add the player to the list if we treat it as an object
                // OR we just overwrite the players list if we have the full state.
                // Let's assume we want to add the player ID to the players list.

                // Update player's currentLobby
                updatePlayerLobby(playerId, lobbyId, new FirebaseService.FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        // Add player to lobby's player list
                        // This is a bit tricky with REST without fetching the whole list.
                        // We can use POST to /players to append, but that creates unique keys.
                        // For a simple implementation, let's just use a specific index or fetch and update.

                        // Simplified: update the lobby players list (this is a placeholder for a more robust list update)
                        String updateLobby = "{\"" + playerId + "\": true}"; // Using player ID as a key in an object is safer
                        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyId + "/players", updateLobby, new FirebaseService.FirebaseCallback() {
                            @Override
                            public void onSuccess(String response) {
                                lobbyID = lobbyId;
                                callback.onSuccess(response);
                            }

                            @Override
                            public void onFailure(String error) {
                                callback.onFailure(error);
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        callback.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                callback.onFailure(error);
            }
        });
    }

    private void updatePlayerLobby(String playerId, String lobbyId, final FirebaseService.FirebaseCallback callback) {
        String body = "{\"currentLobby\": \"" + lobbyId + "\"}";
        FirebaseService.getInstance().patchDbData("players/" + playerId, body, new FirebaseService.FirebaseCallback() {
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

    public void setPlayerInLobby(final String lobbyId, final String playerId, final FirebaseService.FirebaseCallback callback) {
        joinLobby(lobbyId, playerId, callback);
    }



}
