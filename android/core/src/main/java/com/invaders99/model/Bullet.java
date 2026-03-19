package com.invaders99.model;

import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    public static final float WIDTH = 4f;
    public static final float HEIGHT = 12f;
    public static final float SPEED = 300f;
    public static final float ENEMY_SPEED = 150f;

    public float x;
    public float y;
    public final boolean enemyBullet;

    private final Rectangle bounds = new Rectangle();

    public Bullet(float x, float y) {
        this(x, y, false);
    }

    public Bullet(float x, float y, boolean enemyBullet) {
        this.x = x;
        this.y = y;
        this.enemyBullet = enemyBullet;
    }

    public Rectangle getBounds() {
        bounds.set(x - WIDTH / 2f, y - HEIGHT / 2f, WIDTH, HEIGHT);
        return bounds;
    }
}
