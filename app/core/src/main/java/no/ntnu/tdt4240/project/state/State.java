package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

public abstract class State {
    protected Stage stage;
    protected Engine engine;
    protected StateManager sm;

    protected State(Engine engine, StateManager sm) {
        this.stage = new Stage();
        this.engine = engine;
        this.sm = sm;
    }

    protected abstract void update(float dt);
    protected abstract void render(SpriteBatch batch);
    protected abstract void resize(int width, int height);
    protected abstract void dispose();
}
