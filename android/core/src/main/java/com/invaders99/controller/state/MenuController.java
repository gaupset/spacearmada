package com.invaders99.controller.state;

import com.invaders99.controller.MainController;
import com.invaders99.view.GameStateManager;
import com.invaders99.view.state.GameState;
import com.invaders99.view.state.WaitingRoomState;
import com.invaders99.view.state.SettingsState;

public class MenuController {
    private final GameStateManager gsm;
    private final MainController main;

    public MenuController(GameStateManager gsm, MainController main) {
        this.gsm = gsm;
        this.main = main;
    }

    public void onPlayClicked() {
        gsm.set(new GameState(gsm, main));
    }

    public void onLobbyClicked() {
        try {
            gsm.set(new WaitingRoomState(gsm, main));
        } catch (Exception e) {
            System.err.println("Failed to enter lobby: " + e.getMessage());
            // Optionally: gsm.set(new ErrorState(gsm, main, "No Server Connection"));
        }
    }

    public void onSettingsClicked() {
        gsm.set(new SettingsState(gsm, main));
    }
}
