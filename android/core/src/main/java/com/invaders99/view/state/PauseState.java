package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.model.Game;
import com.invaders99.model.Sabotage;
import com.invaders99.service.LobbyHandler;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.view.GameStateManager;

public class PauseState extends State {
    private static final float PAUSE_DURATION = 10f;

    private final GameState gameState;
    private Stage stage;
    private Texture overlayTex;
    private float pauseTimeLeft;

    public PauseState(GameStateManager gsm, GameState gameState) {
        super(gsm);
        this.gameState = gameState;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(Game.WORLD_WIDTH, Game.WORLD_HEIGHT));
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
        Skin skin = UiFactory.getInstance().getSkin();

        Table root = new Table();
        root.setFillParent(true);
        root.setBackground(new TextureRegionDrawable(overlayTex));
        root.center();

        Label title = new Label("GAME PAUSED", skin);
        title.setFontScale(1.5f);
        root.add(title).padBottom(40f).row();

        SpaceButton unpauseButton = new SpaceButton("UNPAUSE");
        unpauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.pop();
            }
        });
        root.add(unpauseButton).padBottom(20f).row();

        if (gameState.getLobbyHandler() != null) {
            SpaceButton sabotageButton = new SpaceButton("SABOTAGE");
            sabotageButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    gameState.getLobbyHandler().setSabotage(new Sabotage());
                }
            });
            root.add(sabotageButton).row();
        }

        stage.addActor(root);
    }

    @Override
    public void update(float dt) {
        pauseTimeLeft -= dt;
        if (pauseTimeLeft <= 0f) {
            gsm.pop();
            return;
        }
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        gameState.renderFrozen(batch, Gdx.graphics.getDeltaTime());
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
        if (overlayTex != null) overlayTex.dispose();
    }
}
