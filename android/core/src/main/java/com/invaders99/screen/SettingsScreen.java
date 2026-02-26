package com.invaders99.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.Main;
import com.invaders99.ui.FirebaseTestWidget;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;

/** Settings screen with Firebase connection status and a back button. */
public class SettingsScreen implements Screen {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;
    private static final float FONT_SCALE_TITLE = 1.4f;

    private final Main game;
    private final Assets assets;

    private Stage stage;
    private FirebaseTestWidget firebaseWidget;

    public SettingsScreen(Main game, Assets assets) {
        this.game = game;
        this.assets = assets;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        buildLayout();
        firebaseWidget.autoTest();
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Title
        Label titleLabel = new Label("SETTINGS", UiFactory.getInstance().getSkin());
        titleLabel.setFontScale(FONT_SCALE_TITLE);
        root.add(titleLabel).center().padBottom(40f).row();

        // Firebase connection widget
        firebaseWidget = new FirebaseTestWidget();
        root.add(firebaseWidget)
            .width(Theme.BUTTON_WIDTH)
            .padBottom(BUTTON_SPACING)
            .row();

        // Back button
        SpaceButton backButton = new SpaceButton("BACK");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HomeScreen(game, assets));
            }
        });
        root.add(backButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padTop(BUTTON_SPACING)
            .row();

        stage.addActor(root);
    }

    @Override
    public void render(float delta) {
        Theme theme = UiFactory.getInstance().getTheme();
        ScreenUtils.clear(theme.background);

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
