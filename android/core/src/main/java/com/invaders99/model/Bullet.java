package com.invaders99.model;

import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    public static final float WIDTH = 60f;
    public static final float HEIGHT = 60f;
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
        float hitboxWidth = WIDTH * 0.3f;   // narrower
        float hitboxHeight = HEIGHT * 0.1f; // slightly shorter

        bounds.set(
            x - hitboxWidth / 2f,
            y - hitboxHeight / 2f,
            hitboxWidth,
            hitboxHeight
        );

        return bounds;
    }
}
