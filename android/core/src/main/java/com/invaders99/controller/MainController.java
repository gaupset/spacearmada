package com.invaders99.controller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.invaders99.service.AudioService;
import com.invaders99.service.FirebaseService;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.AppConfig;
import com.invaders99.util.Assets;
import com.invaders99.view.GameStateManager;
import com.invaders99.view.state.MenuState;

public class MainController {
    private final AppConfig config;
    private GameStateManager gsm;
    private SpriteBatch batch;
    private Assets assets;

    public MainController(AppConfig config) {
        this.config = config;
    }

    public void create() {
        AppConfig.init(config);

        assets = new Assets();
        assets.load();

        FirebaseService.init();
        UiFactory.init(assets.getDefaultFont());

        AudioService.getInstance().playMusic("elevator_music.mp3", true);

        batch = new SpriteBatch();
        gsm = new GameStateManager();
        gsm.push(new MenuState(gsm, this));
    }

    public void render(float delta) {
        gsm.update(delta);
        gsm.render(batch);
    }

    public void resize(int width, int height) {
        gsm.resize(width, height);
    }

    public void dispose() {
        gsm.dispose();
        if (batch != null) batch.dispose();
        UiFactory.getInstance().dispose();
        if (assets != null) assets.dispose();
        AudioService.getInstance().stopMusic();
    }

    public GameStateManager getGsm() {
        return gsm;
    }

    public Assets getAssets() {
        return assets;
    }

    public SpriteBatch getBatch() {
        return batch;
    }
}
