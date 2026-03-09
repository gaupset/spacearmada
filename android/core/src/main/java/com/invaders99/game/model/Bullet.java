package com.invaders99.game.model;

import com.badlogic.gdx.math.Rectangle;

public class Bullet {
    public static final float WIDTH = 4f;
    public static final float HEIGHT = 12f;
    public static final float SPEED = 300f;

    public float x;
    public float y;

    private final Rectangle bounds = new Rectangle();

    public Bullet(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        bounds.set(x - WIDTH / 2f, y - HEIGHT / 2f, WIDTH, HEIGHT);
        return bounds;
    }
}
