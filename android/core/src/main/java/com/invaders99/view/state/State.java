package com.invaders99.view.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.invaders99.view.GameStateManager;

public abstract class State {
    protected final GameStateManager gsm;

    protected State(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update(float dt);
    public abstract void render(SpriteBatch batch);
    public abstract void dispose();

    public void resize(int width, int height) {}
    public void show() {}
    public void hide() {}
}
