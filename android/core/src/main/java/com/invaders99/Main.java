package com.invaders99;

import com.badlogic.gdx.Game;
import com.invaders99.screen.HomeScreen;
import com.invaders99.service.FirebaseService;
import com.invaders99.service.LobbyHandler;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.AppConfig;
import com.invaders99.util.Assets;

/** Application entry point. Manages shared resources and screen navigation. */
public class Main extends Game {
    private final AppConfig config;

    private Assets assets;

    public Main(AppConfig config) {
        this.config = config;
    }

    @Override
    public void create() {
        // Initialize the config
        AppConfig.init(config);

        // Load assets
        assets = new Assets();
        assets.load();

        // Initialize Firebase
        FirebaseService.init();
        UiFactory.init(assets.getDefaultFont());

        // Go to home screen
        setScreen(new HomeScreen(this, assets));

        LobbyHandler.init();
        LobbyHandler.getInstance().start();
    }

    @Override
    public void dispose() {
        super.dispose();
        UiFactory.getInstance().dispose();
        if (assets != null) assets.dispose();
    }
}
