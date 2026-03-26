package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.MainController;
import com.invaders99.service.LobbyHandler;
import com.invaders99.service.ScoreService;
import com.invaders99.ui.SpaceButton;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;
import com.invaders99.view.GameStateManager;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class GameOverState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;

    private final MainController main;
    private final int finalScore;
    private LobbyHandler lobbyHandler;
    private Stage stage;
    private boolean isNewHighScore;
    private Table scoresTable;
    private boolean inLobby = false;
    private Timer.Task heartbeatTask;
    private Timer.Task lobbyCheckTask;

    public GameOverState(GameStateManager gsm, MainController main, int finalScore, LobbyHandler lobbyHandler) {
        super(gsm);
        this.main = main;
        this.finalScore = finalScore;
        this.lobbyHandler = lobbyHandler;
        inLobby = true;
        this.isNewHighScore = ScoreService.getInstance().updateHighScore(finalScore);
    }

    private void startLobbyTasks() {
        heartbeatTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                lobbyHandler.sendHeartbeat();
            }
        }, 0, 5f);

        lobbyCheckTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                lobbyHandler.getLobbyStatus(new LobbyHandler.LobbyStatusCallback() {
                    @Override
                    public void onUpdate(JsonValue lobbyData) {
                        if (lobbyData.getBoolean("gameEnded", false)) {
                            // deleteLobby();
                        }
                    }
                    @Override
                    public void onFailure(String error) {}
                });
            }
        }, 0.5f, 1f);
    }

    public GameOverState(GameStateManager gsm, MainController main, int finalScore) {
        super(gsm);
        this.main = main;
        this.finalScore = finalScore;
        this.isNewHighScore = ScoreService.getInstance().updateHighScore(finalScore);
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();

        if (lobbyHandler != null && lobbyHandler.getLobbyID() != null) {
            startLobbyStatusPolling();
        }
    }

    private void startLobbyStatusPolling() {
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (lobbyHandler != null && lobbyHandler.getLobbyID() != null) {
                    lobbyHandler.getLobbyStatus(new LobbyHandler.LobbyStatusCallback() {
                        @Override
                        public void onUpdate(JsonValue lobbyData) {
                            updateScoresTable(lobbyData);
                        }
                        @Override
                        public void onFailure(String error) {}
                    });
                } else {
                    this.cancel();
                }
            }
        }, 0, 2f);
    }

    private void updateScoresTable(JsonValue lobbyData) {
        if (scoresTable == null) return;
        scoresTable.clearChildren();

        JsonValue players = lobbyData.get("players");
        if (players == null) return;

        List<JsonValue> playerList = new ArrayList<>();
        for (JsonValue p : players) {
            playerList.add(p);
        }

        playerList.sort(new Comparator<JsonValue>() {
            @Override
            public int compare(JsonValue p1, JsonValue p2) {
                return Integer.compare(p2.getInt("score", 0), p1.getInt("score", 0));
            }
        });

        Assets assets = main.getAssets();
        Label.LabelStyle style = new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE);

        for (JsonValue p : playerList) {
            String name = p.getString("actualName", "Unknown");
            int score = p.getInt("score", 0);
            boolean isGameOver = p.getBoolean("gameOver", false);
            boolean left = p.getBoolean("leftLobby", false);

            String statusText = left ? "[LEFT]" : (isGameOver ? "[DONE]" : "[PLAYING...]");
            Label pLabel = new Label(name + ": " + score + " " + statusText, style);
            pLabel.setFontScale(0.6f);
            scoresTable.add(pLabel).row();
        }
    }

    private void buildLayout() {
        Assets assets = main.getAssets();
        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label title = new Label("GAME OVER", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        title.setFontScale(1.5f);
        root.add(title).padBottom(10f).row();

        if (isNewHighScore) {
            Label newHigh = new Label("NEW HIGH SCORE!", new Label.LabelStyle(assets.getDefaultFont(), Color.GOLD));
            newHigh.setFontScale(0.8f);
            root.add(newHigh).padBottom(10f).row();
        }

        Label scoreLabel = new Label("YOUR SCORE: " + finalScore, new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE));
        scoreLabel.setFontScale(1.0f);
        root.add(scoreLabel).padBottom(20f).row();

        // Multi-player Scores
        if (lobbyHandler != null && lobbyHandler.getLobbyID() != null) {
            scoresTable = new Table();
            root.add(scoresTable).padBottom(20f).row();
        }

        SpaceButton home = new SpaceButton("HOME");
        home.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
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
        });
        root.add(home)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING)
            .row();

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
