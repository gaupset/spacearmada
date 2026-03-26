package com.invaders99.controller;

import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.service.LobbyHandler;

public class FirebaseController {
    private final LobbyHandler lobbyHandler;

    public LobbyHandler lobbyHandler() {return lobbyHandler;}

    public FirebaseController() {
        this.lobbyHandler = new LobbyHandler();
        this.lobbyHandler.setPlayerID("player_" + (System.currentTimeMillis() % 10000));
    }

    public void checkDatabaseFormat(LobbyHandler.LobbyCallback callback) {
        lobbyHandler.checkDatabaseFormat(callback);
    }

    public void createLobby(LobbyHandler.LobbyCallback callback) {
        lobbyHandler.createLobby(callback);
    }

    public void joinLobby(String code, LobbyHandler.LobbyCallback callback) {
        lobbyHandler.joinLobby(code, callback);
    }

    public void leaveLobby(boolean isHost, LobbyHandler.LobbyCallback callback) {
        lobbyHandler.leaveLobby(callback);
    }

    public void getLobbyStatus(LobbyHandler.LobbyStatusCallback callback) {
        lobbyHandler.getLobbyStatus(callback);
    }

    public void startGame(LobbyHandler.LobbyCallback callback) {
        lobbyHandler.startGame(callback);
    }

    public String getLobbyID() {
        return lobbyHandler.getLobbyID();
    }

    public void checkLobbyState(JsonValue lobbyData){
        lobbyHandler.evaluateLobby(lobbyData);
    }
}
