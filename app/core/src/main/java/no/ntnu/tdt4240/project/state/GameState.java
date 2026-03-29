package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import no.ntnu.tdt4240.project.layout.GameLayout;
import no.ntnu.tdt4240.project.layout.Layout;

public class GameState extends State {
    private Engine engine;
    private Layout layout;

    public GameState(StateManager sm, Engine engine) {
        super(sm);
        this.engine = engine;
        this.layout = new GameLayout();
    }

    @Override
    public void setup() {
        // TODO Use InputMultiplexer with input system
        Gdx.input.setInputProcessor(layout.get());
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void update(float dt) {
    }

    @Override
    public void render(SpriteBatch batch) {
    }

    @Override
    public void resize(int width, int height) {
        layout.resize(width, height);
    }

    @Override
    public void dispose() {
    }
}
