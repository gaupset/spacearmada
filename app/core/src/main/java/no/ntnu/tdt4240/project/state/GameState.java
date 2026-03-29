package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.AppConfig;

public class GameState extends State {
    private Engine engine;
    public GameState(StateManager sm, Engine engine) {
        super(sm);
        this.engine = engine;
    }

    @Override
    protected void setup() {
        stage.setViewport(new ExtendViewport(AppConfig.WIDTH, AppConfig.HEIGHT));
        // TODO Use InputMultiplexer with input system
        Gdx.input.setInputProcessor(stage);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void update(float dt) {
    }

    @Override
    protected void render(SpriteBatch batch) {
    }

    @Override
    protected void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    protected void dispose() {
    }
}
