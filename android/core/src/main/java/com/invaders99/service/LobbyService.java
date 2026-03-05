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

    public void getLobbies() {
        // get lobbies
        FirebaseService.getInstance().getDbData("lobbies",
            new FirebaseService.FirebaseCallback() {
                @Override
                public void onSuccess(String response) {

                    JsonValue root = (response == null || response.equals("null")) ? null : new JsonReader().parse(response);
                    final String finalId = LobbyHandler.getInstance().getNewID(root);

                    LobbyHandler.getInstance().createNewLobby2(finalId);
                }

                @Override
                public void onFailure(String error) {
                    System.err.println("Fetch players failed: " + error);
                }
            });

    }

    public void setPlayer() {
        // get players
        FirebaseService.getInstance().getDbData("players",
            new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {

                JsonValue root = (response == null || response.equals("null")) ? null : new JsonReader().parse(response);
                final String finalId = LobbyHandler.getInstance().getNewID(root);

                LobbyHandler.getInstance().setNewPlayer2(finalId);
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Fetch players failed: " + error);
            }
        });
    }

    public void writePlayer(String id, PlayerModel player, FirebaseService.FirebaseCallback callback) {
        String body = FirebaseJson.toJson(player);
        FirebaseService.getInstance().putDbData("players/" + id, body, callback);
    }

    public void writeLobby(String id, LobbyModel lobby) {
        // write player into Lobby
        String body = FirebaseJson.toJson(lobby);
        FirebaseService.FirebaseCallback callback = new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                LobbyHandler.getInstance().lobbyID = id;
                System.out.println("Created lobby: " + id);
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Player creation failed: " + error);
            }
        };
        FirebaseService.getInstance().putDbData("lobbies/" + id, body, callback);

        // write Player in Lobby: write to "players/players_id" : lobby = lobbyID
        FirebaseService.FirebaseCallback callback2 = new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("set lobby entry");
            }

            @Override
            public void onFailure(String error) {
                System.err.println("Player creation failed: " + error);
            }
        };
        String body2 = "{\"currentLobby\": \"" + id + "\"}";
        FirebaseService.getInstance().patchDbData("players/" + id, body2, callback2);

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
