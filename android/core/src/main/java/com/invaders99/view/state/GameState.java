package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.MainController;
import com.invaders99.controller.WaveController;
import com.invaders99.controller.state.GameController;
import com.invaders99.model.Game;
import com.invaders99.service.LobbyHandler;
import com.invaders99.util.Assets;
import com.invaders99.view.GameHud;
import com.invaders99.view.GameRenderer;
import com.invaders99.view.GameStateManager;

public class GameState extends State {
    private final MainController main;
    private final LobbyHandler lobbyHandler;
    private InputMultiplexer inputMux;

    private ExtendViewport viewport;
    private Game model;
    private GameRenderer renderer;
    private GameController controller;
    private GameHud hud;
    private Timer.Task heartbeatTask;
    private Timer.Task lobbyCheckTask;

    public GameState(GameStateManager gsm, MainController main) {
        this(gsm, main, null);
    }

    public GameState(GameStateManager gsm, MainController main, LobbyHandler lobbyHandler) {
        super(gsm);
        this.main = main;
        this.lobbyHandler = lobbyHandler;
    }

    @Override
    public void show() {
        if (model == null) {
            Assets assets = main.getAssets();
            model = new Game();
            viewport = new ExtendViewport(Game.WORLD_WIDTH, Game.WORLD_HEIGHT);
            renderer = new GameRenderer(assets);
            controller = new GameController(model, viewport, assets, new WaveController());
            hud = new GameHud(
                model,
                open -> model.menuOpen = open,
                () -> exitGame(),
                () -> gsm.push(new PauseState(gsm, this))
            );

            inputMux = new InputMultiplexer();
            inputMux.addProcessor(hud.getStage());
            inputMux.addProcessor(controller);
        }
        Gdx.input.setInputProcessor(inputMux);

        if (lobbyHandler != null) {
            startLobbyTasks();
        }
    }

    private void startLobbyTasks() {
        heartbeatTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                lobbyHandler.sendHeartbeat();
                lobbyHandler.updateScore(model.score);
            }
        }, 0, 5f);

        lobbyCheckTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                lobbyHandler.getAndChangeLobbyStatus(new LobbyHandler.LobbyStatusCallback() {
                    @Override
                    public void onUpdate(JsonValue lobbyData) {
                        if (lobbyData.getBoolean("gameEnded", false)) {
                            triggerGameOver();
                        }
                    }
                    @Override
                    public void onFailure(String error) {}
                });
            }
        }, 1f, 2f);
    }

    private void exitGame() {
        if (lobbyHandler != null) {
            lobbyHandler.leaveLobby(new LobbyHandler.LobbyCallback() {
                @Override
                public void onSuccess(String success) {
                    gsm.set(new MenuState(gsm, main));
                }
                @Override
                public void onFailure(String error) {
                    gsm.set(new MenuState(gsm, main));
                }
            });
        } else {
            gsm.set(new MenuState(gsm, main));
        }
    }

    public void renderFrozen(SpriteBatch batch, float delta) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        renderer.render(model, batch, viewport);
        hud.act(delta);
        hud.draw();
    }

    @Override
    public void update(float dt) {
        controller.update(dt);

        if (model.isGameOver()) {
            triggerGameOver();
        }
    }

    private void triggerGameOver() {
        stopLobbyTasks();
        gsm.set(new GameOverState(gsm, main, model.score, lobbyHandler));
    }

    private void stopLobbyTasks() {
        if (heartbeatTask != null) heartbeatTask.cancel();
        if (lobbyCheckTask != null) lobbyCheckTask.cancel();
    }

    @Override
    public void render(SpriteBatch batch) {
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        ScreenUtils.clear(Color.BLACK);
        renderer.render(model, batch, viewport);

        hud.act(Gdx.graphics.getDeltaTime());
        hud.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        hud.resize(width, height);
    }

    @Override
    public void dispose() {
        stopLobbyTasks();
        if (renderer != null) renderer.dispose();
        if (hud != null) hud.dispose();
    }
}
