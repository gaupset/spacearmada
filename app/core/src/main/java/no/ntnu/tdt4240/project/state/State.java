package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class State {
    protected StateManager sm;
    protected SpriteBatch batch;

    protected State(StateManager sm, SpriteBatch batch) {
        this.sm = sm;
        this.batch = batch;
    }

    protected abstract void setup();
    protected abstract void update(float dt);
    protected abstract void render();
    protected abstract void resize(int width, int height);
    protected abstract void dispose();
}
