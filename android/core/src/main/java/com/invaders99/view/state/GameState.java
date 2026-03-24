package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.MainController;
import com.invaders99.controller.WaveController;
import com.invaders99.controller.state.GameController;
import com.invaders99.model.Game;
import com.invaders99.util.Assets;
import com.invaders99.view.GameHud;
import com.invaders99.view.GameRenderer;
import com.invaders99.view.GameStateManager;

public class GameState extends State {
    private final MainController main;
    private InputMultiplexer inputMux;

    private ExtendViewport viewport;
    private Game model;
    private GameRenderer renderer;
    private GameController controller;
    private GameHud hud;

    public GameState(GameStateManager gsm, MainController main) {
        super(gsm);
        this.main = main;
    }

    @Override
    public void show() {
        if (model == null) {
            Assets assets = main.getAssets();
            model = new Game();
            viewport = new ExtendViewport(Game.WORLD_WIDTH, Game.WORLD_HEIGHT);
            renderer = new GameRenderer(assets);
            controller = new GameController(model, viewport, assets, new WaveController());
            hud = new GameHud(
                model,
                open -> model.menuOpen = open,
                () -> gsm.set(new MenuState(gsm, main)),
                () -> gsm.push(new PauseState(gsm, this))
            );

            inputMux = new InputMultiplexer();
            inputMux.addProcessor(hud.getStage());
            inputMux.addProcessor(controller);
        }
        Gdx.input.setInputProcessor(inputMux);
    }

    public void renderFrozen(SpriteBatch batch, float delta) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        renderer.render(model, batch, viewport);
        hud.act(delta);
        hud.draw();
    }

    @Override
    public void update(float dt) {
        controller.update(dt);

        if (model.isGameOver()) {
            gsm.set(new GameOverState(gsm, main, model.score));
            return;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        renderer.render(model, batch, viewport);

        hud.act(Gdx.graphics.getDeltaTime());
        hud.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hud.resize(width, height);
    }

    public void dispose() {
        if (renderer != null) renderer.dispose();
        if (hud != null) hud.dispose();
    }
}
