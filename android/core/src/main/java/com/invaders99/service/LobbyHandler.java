package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.util.FirebaseJson;

import java.util.Random;

public class LobbyHandler {
    static LobbyHandler instance;
    static LobbyService lobbyService;
    static CallbackFactory callbackFactory;

    private String playerID;

    public static void init() {
        instance = new LobbyHandler();
        lobbyService = new LobbyService();
    }

    public static LobbyHandler getInstance() {
        return instance;
    }

    public void start() {
        lobbyService.checkDatabaseFormat(new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Database OK");
                setNewPlayer();
            }

            @Override
            public void onFailure(String error) {
                System.out.println("Database error: " + error);
            }
        });
    }

    public void readDatabase() {
        FirebaseService.getInstance().getDbData("", new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println(response);
            }

            @Override
            public void onFailure(String error) {
                System.out.println("Database error: " + error);
            }
        });
    }

    public void setNewPlayer() {
        if (playerID != null) return;

        // 1. Get current players to ensure uniqueness
        FirebaseService.getInstance().getDbData("players", new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                String newId;
                JsonValue root = (response == null || response.equals("null")) ? null : new JsonReader().parse(response);

                // 2. Generate a unique 6-digit ID
                Random rand = new Random();
                do {
                    newId = String.format("%06d", rand.nextInt(1000000));
                } while (root != null && root.has(newId));

                final String finalId = newId;
                PlayerModel newPlayer = new PlayerModel("Player_" + finalId);
                String body = FirebaseJson.toJson(newPlayer);

                // 3. Write the new player to the database
                FirebaseService.getInstance().putDbData("players/" + finalId, body, new FirebaseService.FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        playerID = finalId;
                        lobbyService.playerID = finalId;
                        System.out.println("Created new player: " + playerID);
                    }

                    @Override
                    public void onFailure(String error) {
                        System.err.println("Failed to create player: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Failed to fetch players: " + error);
            }
        });
    }

    public void joinLobbyWithID(String lobbyId) {
        if (playerID == null) {
            System.err.println("Cannot join lobby: Player ID not set.");
            return;
        }

        lobbyService.setPlayerInLobby(lobbyId, playerID, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Joined lobby: " + lobbyId);
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Join failed: " + error);
            }
        });
    }

    public void createLobbyGroup(String name) {
        lobbyService.createLobby(name, new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("Lobby created: " + response);
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Lobby creation failed: " + error);
            }
        });
    }

    public String getPlayerID() {
        return playerID;
    }
}
