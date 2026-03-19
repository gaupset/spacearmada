package com.invaders99.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.Main;
import com.invaders99.game.controller.GameController;
import com.invaders99.game.model.GameModel;
import com.invaders99.game.view.GameHud;
import com.invaders99.game.view.GameRenderer;
import com.invaders99.util.Assets;

public class GameScreen implements Screen {
    private final Main game;
    private final Assets assets;

    private ExtendViewport viewport;
    private SpriteBatch batch;
    private GameModel model;
    private GameRenderer renderer;
    private GameController controller;
    private GameHud hud;
    private InputMultiplexer inputMux;
    private boolean goingToPause;

    public GameScreen(Main game, Assets assets) {
        this.game = game;
        this.assets = assets;
    }

    @Override
    public void show() {
        if (model == null) {
            viewport = new ExtendViewport(GameModel.WORLD_WIDTH, GameModel.WORLD_HEIGHT);
            batch = new SpriteBatch();
            model = new GameModel();
            renderer = new GameRenderer(assets);
            controller = new GameController(model, viewport, assets);
            hud = new GameHud(model, () -> game.setScreen(new HomeScreen(game, assets)),
                () -> {
                    goingToPause = true;
                    game.setScreen(new PauseScreen(game, assets, this));
                });

            inputMux = new InputMultiplexer();
            inputMux.addProcessor(hud.getStage());
            inputMux.addProcessor(controller);
        }
        Gdx.input.setInputProcessor(inputMux);
    }

    /** Renders the current game state without updating (for pause overlay). */
    public void renderFrozen(float delta) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        renderer.render(model, batch, viewport);
        hud.act(delta);
        hud.draw();
    }

    @Override
    public void render(float delta) {
        controller.update(delta);

        if (model.isGameOver()) {
            game.setScreen(new GameOverScreen(game, assets, model.score));
            return;
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        renderer.render(model, batch, viewport);

        hud.act(delta);
        hud.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hud.resize(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        if (goingToPause) {
            goingToPause = false;
            return;
        }
        dispose();
    }

    @Override
    public void dispose() {
        if (batch != null) batch.dispose();
        if (renderer != null) renderer.dispose();
        if (hud != null) hud.dispose();
    }
}
