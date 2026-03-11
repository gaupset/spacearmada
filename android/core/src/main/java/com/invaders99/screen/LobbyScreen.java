package com.invaders99.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.JsonValue;
import com.invaders99.Main;
import com.invaders99.service.LobbyHandler;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;

public class LobbyScreen implements Screen {
    private final Main game;
    private final Assets assets;
    private final LobbyHandler lobbyHandler;
    private Stage stage;
    private Table root;
    private Label playerCountLabel;
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 2.0f; // Update every 2 seconds
    private boolean inLobby = false;

    public LobbyScreen(Main game, Assets assets) {
        this.game = game;
        this.assets = assets;
        this.lobbyHandler = new LobbyHandler();
        // Use a more stable ID for testing, or better yet, one from Auth
        this.lobbyHandler.setPlayerID("player_" + (System.currentTimeMillis() % 10000));
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(360, 640));
        Gdx.input.setInputProcessor(stage);

        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        // Ensure database format is correct
        lobbyHandler.checkDatabaseFormat(new LobbyHandler.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                showMainOptions();
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "DB check failed: " + error);
            }
        });
    }

    private void showMainOptions() {
        root.clear();
        inLobby = false;
        SpaceButton createBtn = new SpaceButton("CREATE LOBBY");
        SpaceButton joinBtn = new SpaceButton("JOIN LOBBY");
        SpaceButton backBtn = new SpaceButton("BACK");

        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                createLobby();
            }
        });

        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showJoinInput();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new HomeScreen(game, assets));
            }
        });

        root.add(createBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(joinBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(backBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
    }

    private void createLobby() {
        lobbyHandler.createLobby(new LobbyHandler.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("create Lobby:" + response);
                showWaitingRoom(true);
            }

            @Override
            public void onFailure(String error) {
                System.out.println("Lobby create failed: " + error);
                Gdx.app.error("Lobby", "Create failed: " + error);
            }
        });
    }

    private void showJoinInput() {
        System.out.println("print join Input");
        root.clear();
        final TextField codeField = new TextField("", UiFactory.getInstance().getSkin());
        SpaceButton confirmBtn = new SpaceButton("JOIN");
        SpaceButton cancelBtn = new SpaceButton("CANCEL");

        confirmBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("join Lobby: " + codeField.getText());
                joinLobby(codeField.getText());
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("join lobby cancel");
                showMainOptions();
            }
        });

        root.add(new Label("ENTER CODE:", UiFactory.getInstance().getSkin())).pad(10).row();
        root.add(codeField).width(200).height(40).pad(10).row();
        root.add(confirmBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(cancelBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
    }

    private void joinLobby(String code) {
        lobbyHandler.joinLobby(code, new LobbyHandler.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                System.out.println("join Lobby: " + response);
                showWaitingRoom(false);
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "Join failed: " + error);
            }
        });
    }

    private void showWaitingRoom(boolean isHost) {
        root.clear();
        inLobby = true;
        root.add(new Label("LOBBY: " + lobbyHandler.getLobbyID(), UiFactory.getInstance().getSkin())).pad(20).row();

        playerCountLabel = new Label("Players: ...", UiFactory.getInstance().getSkin());
        root.add(playerCountLabel).pad(10).row();

        if (isHost) {
            SpaceButton startBtn = new SpaceButton("START GAME");
            startBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    lobbyHandler.startGame(new LobbyHandler.LobbyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            System.out.println("showWaitingRoom: " + response);
                            game.setScreen(new GameScreen(game, assets));
                        }
                        @Override
                        public void onFailure(String error) {
                            Gdx.app.error("Lobby", "Start failed: " + error);
                        }
                    });
                }
            });
            root.add(startBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        } else {
            root.add(new Label("WAITING FOR HOST...", UiFactory.getInstance().getSkin())).pad(10).row();
        }

        SpaceButton leaveBtn = new SpaceButton("LEAVE");
        leaveBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMainOptions();
            }
        });
        root.add(leaveBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
    }

    private void updateLobbyStatus() {
        lobbyHandler.getLobbyStatus(new LobbyHandler.LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue lobbyData) {
                if (lobbyData.has("players")) {
                    playerCountLabel.setText("Players: " + lobbyData.get("players").size);
                }
                if (lobbyData.getBoolean("gamestarted", false)) {
                    game.setScreen(new GameScreen(game, assets));
                }
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "Status update failed: " + error);
            }
        });
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);

        if (inLobby) {
            updateTimer += delta;
            if (updateTimer >= UPDATE_INTERVAL) {
                updateTimer = 0;
                updateLobbyStatus();
            }
        }

        stage.act(delta);
        stage.draw();
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() { inLobby = false; }
    @Override public void dispose() { if (stage != null) stage.dispose(); }
}
