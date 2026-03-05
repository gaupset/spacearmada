package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Random;

public class LobbyHandler {
    private static LobbyHandler instance;
    private final LobbyService lobbyService;
    public String playerID;

    private LobbyHandler() {
        this.lobbyService = new LobbyService();
    }

    public static void init() {
        instance = new LobbyHandler();
    }

    public static LobbyHandler getInstance() {
        return instance;
    }

    public void start() {
        checkDatabaseFormat();
    }

    private void checkDatabaseFormat() {
        lobbyService.getRoot(new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                JsonValue root = (response == null || response.equals("null")) ? null : new JsonReader().parse(response);
                if (root != null && root.has("lobbies") && root.has("players")) {
                    setNewPlayer();
                } else {
                    createDatabase();
                }
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Format check failed: " + error);
            }
        });
    }

    private void createDatabase() {
        String initialData = "{\"lobbies\": {\"init\": true}, \"players\": {\"init\": true}}";
        lobbyService.setupDatabase(initialData, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                setNewPlayer();
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Database setup failed: " + error);
            }
        });
    }

    public void setNewPlayer() {
        if (playerID != null) {
            System.out.println("Player already exists: " + playerID);
            return;
        }

        lobbyService.getPlayers();
    }


    public void createLobbyGroup(String name) {
        final String newId = String.format("%06d", new Random().nextInt(1000000));
        LobbyModel lobby = new LobbyModel(name);

        lobbyService.createLobby(newId, lobby, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Lobby created: " + newId);
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Lobby creation failed: " + error);
            }
        });
    }

    public void joinLobbyWithID(final String lobbyId) {
        if (playerID == null) {
            System.err.println("No player ID found. Register first.");
            return;
        }

        // 1. Update Player's currentLobby
        lobbyService.updatePlayerLobbyReference(playerID, lobbyId, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                // 2. Add Player to Lobby's players list
                lobbyService.addPlayerToLobbyList(lobbyId, playerID, new FirebaseService.FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        System.out.println("Joined lobby: " + lobbyId);
                    }

                    @Override
                    public void onFailure(String error) {
                        System.err.println("Failed to add player to lobby list: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to update player lobby ref: " + error);
            }
        });
    }

    public String getPlayerID() {
        return playerID;
    }
}
