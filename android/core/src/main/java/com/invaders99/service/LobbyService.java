package com.invaders99.service;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.util.FirebaseJson;

import java.util.Random;

public class LobbyService {


    public void getRoot(FirebaseService.FirebaseCallback callback) {
        FirebaseService.getInstance().getDbData("", callback);
    }

    public void setupDatabase(String json, FirebaseService.FirebaseCallback callback) {
        FirebaseService.getInstance().putDbData("", json, callback);
    }

    public void createLobby(String id, LobbyModel lobby, FirebaseService.FirebaseCallback callback) {
        String body = FirebaseJson.toJson(lobby);
        FirebaseService.getInstance().putDbData("lobbies/" + id, body, callback);
    }

    public void getPlayers() {
        FirebaseService.getInstance().getDbData("players",
            new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                String newId;
                JsonValue root = (response == null || response.equals("null")) ? null : new JsonReader().parse(response);

                Random rand = new Random();
                do {
                    newId = String.format("%06d", rand.nextInt(1000000));
                } while (root != null && root.has(newId));

                final String finalId = newId;
                PlayerModel newPlayer = new PlayerModel("Player_" + finalId);

                savePlayer(finalId, newPlayer, new FirebaseService.FirebaseCallback() {
                    @Override
                    public void onSuccess(String response) {
                        LobbyHandler.getInstance().playerID = finalId;
                        System.out.println("Created player: " + finalId);
                    }

                    @Override
                    public void onFailure(String error) {
                        System.err.println("Player creation failed: " + error);
                    }
                });
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Fetch players failed: " + error);
            }
        });
    }

    public void savePlayer(String id, PlayerModel player, FirebaseService.FirebaseCallback callback) {
        String body = FirebaseJson.toJson(player);
        FirebaseService.getInstance().putDbData("players/" + id, body, callback);
    }

    public void updatePlayerLobbyReference(String playerId, String lobbyId, FirebaseService.FirebaseCallback callback) {
        String body = "{\"currentLobby\": \"" + lobbyId + "\"}";
        FirebaseService.getInstance().patchDbData("players/" + playerId, body, callback);
    }

    public void addPlayerToLobbyList(String lobbyId, String playerId, FirebaseService.FirebaseCallback callback) {
        String body = "{\"" + playerId + "\": true}";
        FirebaseService.getInstance().patchDbData("lobbies/" + lobbyId + "/players", body, callback);
    }
}
