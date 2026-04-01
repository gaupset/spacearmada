package com.invaders99.model;

import com.badlogic.gdx.math.Rectangle;

public class Player {
    public static final float WIDTH = 80f;
    public static final float HEIGHT = 80f;
    public float x;
    public float y;

    private final Rectangle bounds = new Rectangle();

    public Player(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Rectangle getBounds() {
        bounds.set(x - WIDTH / 2f, y - HEIGHT / 2f, WIDTH, HEIGHT);
        return bounds;
    }
}
