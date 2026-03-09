package com.invaders99.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
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

public class GameOverScreen implements Screen {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;

    private final Main game;
    private final Assets assets;
    private final int finalScore;

    private Stage stage;

    public GameOverScreen(Main game, Assets assets, int finalScore) {
        this.game = game;
        this.assets = assets;
        this.finalScore = finalScore;
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
        root.center();

        // "GAME OVER" title
        Label title = new Label("GAME OVER", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        title.setFontScale(1.5f);
        root.add(title).padBottom(30f).row();

        // Score
        Label scoreLabel = new Label("SCORE: " + finalScore, new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE));
        scoreLabel.setFontScale(1.0f);
        root.add(scoreLabel).padBottom(60f).row();

        // Buttons
        SpaceButton playAgain = new SpaceButton("PLAY AGAIN");
        playAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new GameScreen(game, assets));
            }
        });
        root.add(playAgain)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING)
            .row();

        SpaceButton home = new SpaceButton("HOME");
        home.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HomeScreen(game, assets));
            }
        });
        root.add(home)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING)
            .row();

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
