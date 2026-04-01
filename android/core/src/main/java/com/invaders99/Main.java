package com.invaders99;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.invaders99.controller.MainController;
import com.invaders99.util.AppConfig;

public class Main extends ApplicationAdapter {
    private final AppConfig config;
    private MainController mainController;

    public Main(AppConfig config) {
        this.config = config;
    }

    @Override
    public void create() {
        mainController = new MainController(config);
        mainController.create();
    }

    @Override
    public void render() {
        mainController.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(int width, int height) {
        mainController.resize(width, height);
    }

    @Override
    public void dispose() {
        mainController.dispose();
    }
}
