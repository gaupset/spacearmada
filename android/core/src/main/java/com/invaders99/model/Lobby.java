package com.invaders99.model;

import java.util.HashMap;
import java.util.Map;

public class Lobby {
    public boolean gameStarted = false;
    public boolean gameEnded = false;
    public Object lobbyCreatedAt;
    public Object gameEndedAt;
    public Map<String, LobbyPlayer> players = new HashMap<>();

    public Lobby() {}
}
