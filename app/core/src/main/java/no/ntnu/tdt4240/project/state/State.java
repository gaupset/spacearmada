package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public abstract class State {
    protected StateManager sm;

    protected State(StateManager sm) {
        this.sm = sm;
    }

    protected abstract void setup();
    protected abstract void update(float dt);
    protected abstract void render(SpriteBatch batch);
    protected abstract void resize(int width, int height);
    protected abstract void dispose();
}
