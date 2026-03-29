package no.ntnu.tdt4240.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import no.ntnu.tdt4240.project.state.StateManager;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private StateManager sm;
    private SpriteBatch batch;

    @Override
    public void create() {
        sm = new StateManager();
        batch = new SpriteBatch();
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);
        sm.update(Gdx.graphics.getDeltaTime());
        sm.render(batch);
    }

    @Override
    public void resize(int width, int height) {
        sm.resize(width, height);
    }

    @Override
    public void dispose() {
        sm.dispose();
        batch.dispose();
    }
}
