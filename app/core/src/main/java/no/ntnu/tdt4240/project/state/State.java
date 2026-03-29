package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.AppConfig;

public abstract class State {
    protected Stage stage;
    protected StateManager sm;

    protected State(StateManager sm) {
        this.stage = new Stage(new ExtendViewport(AppConfig.WIDTH, AppConfig.HEIGHT));
        this.sm = sm;
    }

    protected abstract void setup();
    protected abstract void update(float dt);
    protected abstract void render(SpriteBatch batch);
    protected abstract void resize(int width, int height);
    protected abstract void dispose();
}
