package com.invaders99.service;

import java.util.ArrayList;
import java.util.List;

public class LobbyModel {
    public String name;
    public List<String> players = new ArrayList<>();
    public boolean gamestarted = false;

    public LobbyModel() {}
    public LobbyModel(String name) {
        this.name = name;
    }
}
