package com.invaders99.game.model;

import com.badlogic.gdx.utils.Array;

public class GameModel {
    public static final float WORLD_WIDTH = 360f;
    public static final float WORLD_HEIGHT = 640f;

    public final Player player;
    public final Array<Bullet> bullets = new Array<>();
    public final Array<Enemy> enemies = new Array<>();
    public int score;
    public boolean menuOpen;

    public GameModel() {
        player = new Player(WORLD_WIDTH / 2f, 40f);
    }
}
