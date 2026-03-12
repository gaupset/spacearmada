package com.invaders99.service;

import java.util.HashMap;
import java.util.Map;

public class LobbyModel {
    public Map<String, Boolean> players = new HashMap<>();
    public boolean gamestarted = false;

    public LobbyModel() {}
}
