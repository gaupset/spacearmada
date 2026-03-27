package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.FirebaseController;
import com.invaders99.controller.MainController;
import com.invaders99.controller.WaveController;
import com.invaders99.controller.state.GameController;
import com.invaders99.model.Game;
import com.invaders99.model.Sabotage;
import com.invaders99.service.LobbyHandler;
import com.invaders99.util.Assets;
import com.invaders99.util.FirebaseJson;
import com.invaders99.view.GameHud;
import com.invaders99.view.GameRenderer;
import com.invaders99.view.GameStateManager;

public class GameState extends State {
    private final MainController main;
    private LobbyHandler lobbyHandler;
    private FirebaseController firebaseController;
    private InputMultiplexer inputMux;

    private ExtendViewport viewport;
    private Game model;
    private GameRenderer renderer;
    private GameController controller;
    private GameHud hud;

    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 2.0f;
    private boolean inLobby = false;

    public GameState(GameStateManager gsm, MainController main) {
        super(gsm);
        this.main = main;
    }

    public GameState(GameStateManager gsm, MainController main, FirebaseController firebaseController) {
        super(gsm);
        this.main = main;
        inLobby = true;
        this.firebaseController = firebaseController;
        this.lobbyHandler = firebaseController.lobbyHandler();
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
                () -> gsm.push(new PauseState(gsm, this)),
                () -> gsm.push(new SabotageState(gsm, this, model))
            );

            inputMux = new InputMultiplexer();
            inputMux.addProcessor(hud.getStage());
            inputMux.addProcessor(controller);
        }
        Gdx.input.setInputProcessor(inputMux);
    }

    public LobbyHandler getLobbyHandler() {
        return lobbyHandler;
    }

    /** Offline "DEV GAME" from the menu ({@code !inLobby}); used for dev-only sabotage behavior. */
    boolean isDevGame() {
        return !inLobby;
    }

    /**
     * Dev game only: applies sabotage to this player locally (same payload shape as Firebase), so
     * sabotages can be tested without another player. Lobby multiplayer still uses {@code sabotageTargetId}.
     */
    void triggerDevSelfSabotage(String sabotageType) {
        if (inLobby) {
            return;
        }
        Sabotage s = new Sabotage();
        s.type = sabotageType;
        s.duration = 10;
        JsonValue sabotageJson = new JsonReader().parse(FirebaseJson.toJson(s));
        deploySabotage(sabotageJson);
    }

    /** Lobby: send typed sabotage to assigned target via Firebase. */
    void sendLobbySabotage(Sabotage sabotage) {
        if (lobbyHandler == null) {
            return;
        }
        lobbyHandler.setSabotage(sabotage);
    }

    private void exitGame() {
        if (inLobby) {
            // leaveLobby triggers checkLobbyState() in case of success
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
        if (inLobby) {
            updateTimer += dt;
            if (updateTimer >= UPDATE_INTERVAL) {
                System.out.println("update time!");
                updateTimer = 0;
                lobbyHandler.sendHeartbeat();
                lobbyHandler.updateScore(model.score);
                updateGameStatus();

                if (model.isGameOver()) {
                    triggerGameOver();
                }
            }
        }
    }

    private void deploySabotage(JsonValue sabotageR) {
        float duration = sabotageR.getInt("duration", 10);
        String type = sabotageR.getString("type", Sabotage.TYPE_ENEMY_SPEED);
        if ("example".equals(type)) {
            type = Sabotage.TYPE_ENEMY_SPEED;
        }
        switch (type) {
            case Sabotage.TYPE_ENEMY_SPEED:
                model.applyEnemySpeedSabotage(duration);
                break;
            case Sabotage.TYPE_HALF_PLAYER_BULLETS:
                model.applyPlayerFireRateSabotage(duration);
                break;
            case Sabotage.TYPE_DOUBLE_ALIENS:
                model.applyAlienSpawnSabotage(duration);
                break;
            default:
                model.applyEnemySpeedSabotage(duration);
                break;
        }
    }

    private void updateGameStatus() {
        if (!inLobby) return;
        firebaseController.getLobbyStatus(new LobbyHandler.LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue lobbyData) {
                if (lobbyData.getBoolean("gameEnded", false)) {
                    // go to GameOverScreen
                    System.out.println("update GameStatus: gameEnded is already true, so I end my game");
                    triggerGameOver();
                    return;
                }
                String playerID = firebaseController.lobbyHandler().sessionPlayerID;

                JsonValue players = lobbyData.get("players");
                if (players == null) {
                    System.out.println("No players node found");
                    return;
                }

                JsonValue player = players.get(playerID);
                if (player == null) {
                    System.out.println("Player not found: " + playerID);
                    return;
                }

                JsonValue sabotage = player.get("sabotage");
                if (sabotage != null) {
                    System.out.println("Sabotage found: " + sabotage.prettyPrint(JsonWriter.OutputType.json, 0));
                    delSabotage();
                    deploySabotage(sabotage);
                } else {
                    System.out.println("No sabotage for player " + playerID);
                }
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "Status update failed: " + error);
            }
        });
    }

    private void triggerGameOver() {
        if (inLobby) {
            System.out.println("triggerGameOver executed, inLobby");
            lobbyHandler.setPlayerGameOver(model.score); // checkLobbyState is exc. if success
            gsm.set(new GameOverState(gsm, main, model.score, firebaseController.lobbyHandler()));
        }
        else {
            gsm.set(new GameOverState(gsm, main, model.score));
        }
    }
    private void delSabotage(){
        lobbyHandler.delSabotage();
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
        if (renderer != null) renderer.dispose();
        if (hud != null) hud.dispose();
    }
}
