package com.invaders99.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.Main;
import com.invaders99.ui.SpaceButton;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;

public class HomeScreen implements Screen {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;
    private final Main game;
    private final Assets assets;

    private Stage stage;

    public HomeScreen(Main game, Assets assets) {
        this.game = game;
        this.assets = assets;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        buildLayout();
    }

    private void buildLayout() {
        // Stars background
        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        // Content overlay
        Table root = new Table();
        root.setFillParent(true);
        root.top();

        // Logo in top 1/3
        Image logo = new Image(new TextureRegionDrawable(assets.getLogoCrop()));
        logo.setScaling(Scaling.fit);
        root.add(logo).expandX().fillX().height(VIEWPORT_MIN_HEIGHT / 3f).padTop(20f).row();

        // Buttons bottom 2/3
        Table buttons = new Table();
        String[] buttonLabels = {"DEV GAME", "LOBBY", "LOGIN", "SIGNUP", "SETTINGS"};
        for (String label : buttonLabels) {
            SpaceButton button = new SpaceButton(label);
            if ("LOBBY".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new LobbyScreen(game, assets));
                    }
                });
            } else if ("SETTINGS".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new SettingsScreen(game, assets));
                    }
                });
            } else if ("DEV GAME".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        game.setScreen(new GameScreen(game, assets));
                    }
                });
            }
            buttons.add(button)
                .width(Theme.BUTTON_WIDTH)
                .height(Theme.BUTTON_HEIGHT)
                .padBottom(BUTTON_SPACING)
                .row();
        }
        root.add(buttons).expand().center().row();

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

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
    }
}
