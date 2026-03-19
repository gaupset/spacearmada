package com.invaders99.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.Main;
import com.invaders99.game.model.GameModel;
import com.invaders99.ui.SpaceButton;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;

public class PauseScreen implements Screen {
    private static final float PAUSE_DURATION = 10f;

    private final Main game;
    private final Assets assets;
    private final GameScreen gameScreen;

    private Stage stage;
    private Texture overlayTex;
    private float pauseTimeLeft;

    public PauseScreen(Main game, Assets assets, GameScreen gameScreen) {
        this.game = game;
        this.assets = assets;
        this.gameScreen = gameScreen;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(GameModel.WORLD_WIDTH, GameModel.WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        pauseTimeLeft = PAUSE_DURATION;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.6f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        buildLayout();
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(overlayTex));
        root.center();

        Label title = new Label("GAME PAUSED", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        title.setFontScale(1.5f);
        root.add(title).padBottom(40f).row();

        SpaceButton unpauseButton = new SpaceButton("UNPAUSE");
        unpauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(gameScreen);
            }
        });
        root.add(unpauseButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .row();

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        gameScreen.renderFrozen(delta);

        pauseTimeLeft -= delta;
        if (pauseTimeLeft <= 0f) {
            game.setScreen(gameScreen);
            return;
        }

        stage.getViewport().apply();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (overlayTex != null) overlayTex.dispose();
    }
}
