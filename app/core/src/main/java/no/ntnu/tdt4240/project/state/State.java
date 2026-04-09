package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.ntnu.tdt4240.project.Assets;

/**
 * Base class for all game states in the state machine.
 * States control different screens/modes of the game (Menu, Game, Pause, etc.).
 */
public abstract class State {
    protected StateManager sm;
    protected SpriteBatch batch;
    protected Assets assets;

    protected State(StateManager sm, SpriteBatch batch, Assets assets) {
        this.sm = sm;
        this.batch = batch;
        this.assets = assets;
    }

    /**
     * Called once when the state is first created.
     * Use this to initialize resources that should only be created once.
     */
    protected abstract void setup();

    /**
     * Called when the state becomes active (either first time or returning from another state).
     * Use this to restore input processors and resume any paused logic.
     * Default implementation calls setup() for states that don't need special show logic.
     */
    protected void show() {}

    /**
     * Called when the state becomes inactive (pausing or switching to another state).
     * Use this to clean up input processors or pause logic without disposing resources.
     */
    protected void hide() {}

    protected abstract void update(float dt);
    protected abstract void render();
    protected abstract void resize(int width, int height);

    /**
     * Called when the state is completely removed from the state stack.
     * Use this to dispose of all resources (textures, stages, etc.).
     */
    protected abstract void dispose();
}
