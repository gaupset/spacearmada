package com.invaders99.service;

public class PlayerModel {
    public String name;
    public int score;
    public String currentLobby;

    public PlayerModel() {}
    public PlayerModel(String name) {
        this.name = name;
        this.score = 0;
    }
}
