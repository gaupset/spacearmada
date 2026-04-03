package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.tdt4240.project.Assets;

public abstract class State {
    protected StateManager sm;
    protected SpriteBatch batch;
    protected Assets assets;

    protected State(StateManager sm, SpriteBatch batch, Assets assets) {
        this.sm = sm;
        this.batch = batch;
        this.assets = assets;
    }

    protected abstract void setup();
    protected abstract void update(float dt);
    protected abstract void render();
    protected abstract void resize(int width, int height);
    protected abstract void dispose();
}
