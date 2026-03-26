package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.FirebaseController;
import com.invaders99.controller.MainController;
import com.invaders99.service.LobbyHandler;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Assets;
import com.invaders99.util.Theme;
import com.invaders99.view.GameStateManager;

public class LobbyState extends State {
    private final MainController main;
    private final FirebaseController firebaseController;
    private Stage stage;
    private Table root;
    private Label playerCountLabel;
    private float updateTimer = 0;
    private static final float UPDATE_INTERVAL = 2.0f;
    private boolean inLobby = false;
    private boolean isHost = false;

    public LobbyState(GameStateManager gsm, MainController main) {
        super(gsm);
        this.main = main;
        this.firebaseController = new FirebaseController();
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(360, 640));
        Gdx.input.setInputProcessor(stage);

        Assets assets = main.getAssets();
        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        firebaseController.checkDatabaseFormat(new LobbyHandler.LobbyCallback() {
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
        isHost = false;
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
                gsm.set(new MenuState(gsm, main));
            }
        });

        root.add(createBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(joinBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(backBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
    }

    private void createLobby() {
        firebaseController.createLobby(new LobbyHandler.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                isHost = true;
                showWaitingRoom(true);
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "Create failed: " + error);
            }
        });
    }

    private void showJoinInput() {
        root.clear();
        final TextField codeField = new TextField("", UiFactory.getInstance().getSkin());
        SpaceButton confirmBtn = new SpaceButton("JOIN");
        SpaceButton cancelBtn = new SpaceButton("CANCEL");

        confirmBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                joinLobby(codeField.getText());
            }
        });

        cancelBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                showMainOptions();
            }
        });

        root.add(new Label("ENTER CODE:", UiFactory.getInstance().getSkin())).pad(10).row();
        root.add(codeField).width(200).height(40).pad(10).row();
        root.add(confirmBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(cancelBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
    }

    private void joinLobby(String code) {
        firebaseController.joinLobby(code, new LobbyHandler.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                isHost = false;
                showWaitingRoom(false);
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "Join failed: " + error);
            }
        });
    }

    private void showWaitingRoom(boolean host) {
        root.clear();
        inLobby = true;
        this.isHost = host;
        root.add(new Label("LOBBY: " + firebaseController.getLobbyID(), UiFactory.getInstance().getSkin())).pad(20).row();

        playerCountLabel = new Label("Players: ...", UiFactory.getInstance().getSkin());
        root.add(playerCountLabel).pad(10).row();

        if (isHost) {
            SpaceButton startBtn = new SpaceButton("START GAME");
            startBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    firebaseController.startGame(new LobbyHandler.LobbyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            gsm.set(new GameState(gsm, main, firebaseController));
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
                firebaseController.leaveLobby(isHost, new LobbyHandler.LobbyCallback() {
                    @Override
                    public void onSuccess(String success) {
                        inLobby = false;
                        showMainOptions();
                    }

                    @Override
                    public void onFailure(String error) {
                        Gdx.app.error("Lobby", "Leave failed: " + error);
                        showMainOptions();
                    }
                });
            }
        });
        root.add(leaveBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
    }

    private void updateLobbyStatus() {
        firebaseController.getLobbyStatus(new LobbyHandler.LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue lobbyData) {
                if (inLobby) {
                    if (lobbyData.has("players")) {
                        playerCountLabel.setText("Players: " + lobbyData.get("players").size);
                    }
                    if (lobbyData.getBoolean("gameStarted", false)) {
                        inLobby = false;
                        gsm.set(new GameState(gsm, main, firebaseController));
                    }
                }
            }

            @Override
            public void onFailure(String error) {
                Gdx.app.error("Lobby", "Status update failed: " + error);
                showMainOptions();
            }
        });
    }

    @Override
    public void update(float dt) {
        if (inLobby) {
            updateTimer += dt;
            if (updateTimer >= UPDATE_INTERVAL) {
                updateTimer = 0;
                firebaseController.lobbyHandler().sendHeartbeat();
                updateLobbyStatus();
            }
        }
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
    public void hide() {
        inLobby = false;
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }
}
