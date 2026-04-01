package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.MainController;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Theme;
import com.invaders99.view.GameStateManager;

public class TutorialState extends State {
    private final MainController main;
    private Stage stage;

    public TutorialState(GameStateManager gsm, MainController main) {
        super(gsm);
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(360, 640));
        Gdx.input.setInputProcessor(stage);

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label title = new Label("TUTORIAL", UiFactory.getInstance().getSkin());
        title.setFontScale(1.4f);
        root.add(title).padBottom(40f).row();

        root.add(new Label("Coming soon...", UiFactory.getInstance().getSkin())).padBottom(40f).row();

        SpaceButton backBtn = new SpaceButton("BACK");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new MenuState(gsm, main));
            }
        });
        root.add(backBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).row();

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
