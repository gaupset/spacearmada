package com.invaders99.model;

import com.badlogic.gdx.utils.Array;

public class Game {
    public static final float WORLD_WIDTH = 360f;
    public static final float WORLD_HEIGHT = 640f;

    public final Player player;
    public final Array<Bullet> bullets = new Array<>();
    public final Array<Enemy> enemies = new Array<>();
    public int score;
    public int lives = 3;
    public boolean invincible;
    public boolean menuOpen;
    public final Array<Bullet> enemyBullets = new Array<>();

    public Game() {
        player = new Player(WORLD_WIDTH / 2f, 40f);
    }

    public boolean isGameOver() {
        return lives <= 0;
    }
}
