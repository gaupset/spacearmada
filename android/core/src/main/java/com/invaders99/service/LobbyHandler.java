package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.Random;

public class LobbyHandler {
    private static LobbyHandler instance;
    private final LobbyService lobbyService;
    public String playerID;
    public String lobbyID;

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
        lobbyService.getRoot(new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                JsonValue root = (response == null || response.equals("null")) ? null : new JsonReader().parse(response);
                if (root != null && root.has("lobbies") && root.has("players")) {
                    setNewPlayer();
                } else {
                    createDatabase();
                }
                createNewLobby();
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

        lobbyService.setPlayer();
    }

    public void setNewPlayer2(String finalId) {
        PlayerModel newPlayer = new PlayerModel("Player_" + finalId);
        FirebaseService.FirebaseCallback callback = new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                playerID = finalId;
                System.out.println("Created player: " + finalId);
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Player creation failed: " + error);
            }
        };
        lobbyService.writePlayer(finalId, newPlayer, callback);
    }

    public void createNewLobby() {
        if (lobbyID != null) {
            System.out.println("Lobby already exists: " + lobbyID);
            return;
        }
        // get current Lobbygroups and proceed from there!
        lobbyService.getLobbies();
    }

    public void createNewLobby2(String finalId) {
        LobbyModel newLobby = new LobbyModel("Lobby" + finalId);
        lobbyService.writeLobby(finalId, newLobby);

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

    public String getNewID(JsonValue root) {
        // used to create new individual ids both for Lobby AND Player
        String newId;

        Random rand = new Random();
        do {
            newId = String.format("%06d", rand.nextInt(1000000));
        } while (root != null && root.has(newId));

        return newId;
    }

}
