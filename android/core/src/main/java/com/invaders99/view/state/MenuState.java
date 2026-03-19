package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.MainController;
import com.invaders99.controller.state.MenuController;
import com.invaders99.ui.SpaceButton;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;
import com.invaders99.view.GameStateManager;

public class MenuState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;

    private final MainController main;
    private final MenuController menuController;
    private Stage stage;

    public MenuState(GameStateManager gsm, MainController main) {
        super(gsm);
        this.main = main;
        this.menuController = new MenuController(gsm, main);
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
    }

    private void buildLayout() {
        Assets assets = main.getAssets();

        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        Table root = new Table();
        root.setFillParent(true);
        root.top();

        Image logo = new Image(new TextureRegionDrawable(assets.getLogoCrop()));
        logo.setScaling(Scaling.fit);
        root.add(logo).expandX().fillX().height(VIEWPORT_MIN_HEIGHT / 3f).padTop(20f).row();

        Table buttons = new Table();
        String[] buttonLabels = {"DEV GAME", "LOBBY", "LOGIN", "SIGNUP", "SETTINGS"};
        for (String label : buttonLabels) {
            SpaceButton button = new SpaceButton(label);
            if ("LOBBY".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        menuController.onLobbyClicked();
                    }
                });
            } else if ("SETTINGS".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        menuController.onSettingsClicked();
                    }
                });
            } else if ("DEV GAME".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        menuController.onPlayClicked();
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
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        ScreenUtils.clear(Color.BLACK);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }
}
