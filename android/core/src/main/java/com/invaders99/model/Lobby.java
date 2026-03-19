package com.invaders99.model;

import java.util.HashMap;
import java.util.Map;

public class Lobby {
    public Map<String, Boolean> players = new HashMap<>();
    public boolean gamestarted = false;

    public Lobby() {}
}
