package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.service.FirebaseController;
import no.ntnu.tdt4240.project.service.LobbyService;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.NameGenerator;
import no.ntnu.tdt4240.project.util.Theme;

import java.util.Random;

/**
 * WaitingRoomState is the multiplayer lobby screen.
 * Players can create or join lobbies to play together via Firebase.
 */
public class WaitingRoomState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float UPDATE_INTERVAL = 2.0f;

    private final FirebaseController firebaseController;
    private Stage stage;
    private Table root;
    private Label playerCountLabel;
    private Label errorLabel;
    private SpaceButton startBtn;
    private TextField nameField;

    private float updateTimer = 0;
    private float pingTimer = 0;
    private float pingInterval = 5 + new Random().nextFloat() * 5;

    private boolean inLobby = false;
    private boolean isHost = false;

    public WaitingRoomState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
        this.firebaseController = new FirebaseController();
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        root = new Table();
        root.setFillParent(true);
        stage.addActor(root);

        showMainOptions();
    }

    @Override
    protected void show() {
        if (stage != null) {
            Gdx.input.setInputProcessor(stage);
        }
    }

    private void showMainOptions() {
        root.clear();
        inLobby = false;
        isHost = false;

        Label title = new Label("MULTIPLAYER", UiFactory.getInstance().getSkin());
        title.setFontScale(1.4f);
        root.add(title).padBottom(40f).row();

        String stored = firebaseController.lobbyHandler().getPlayerID();
        String initialName = stored != null ? stored : NameGenerator.random();
        nameField = new TextField(initialName, UiFactory.getInstance().getSkin());
        root.add(new Label("NAME:", UiFactory.getInstance().getSkin())).pad(5).row();
        root.add(nameField).width(220).height(40).padBottom(20f).row();

        SpaceButton createBtn = new SpaceButton("CREATE LOBBY");
        SpaceButton joinBtn = new SpaceButton("JOIN LOBBY");
        SpaceButton backBtn = new SpaceButton("BACK");

        createBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commitPlayerName();
                createLobby();
            }
        });

        joinBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                commitPlayerName();
                showJoinInput();
            }
        });

        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sm.set(new MenuState(sm, batch, assets));
            }
        });

        root.add(createBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(joinBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();
        root.add(backBtn).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).pad(10).row();

        errorLabel = new Label("", UiFactory.getInstance().getSkin());
        errorLabel.setColor(1f, 0.3f, 0.3f, 1f);
        errorLabel.setWrap(true);
        root.add(errorLabel).width(280).padTop(10).row();
    }

    private void showError(String message) {
        Gdx.app.error("Lobby", message);
        if (errorLabel != null) errorLabel.setText(message);
    }

    private void commitPlayerName() {
        String name = nameField != null ? nameField.getText().trim() : "";
        if (name.isEmpty()) name = NameGenerator.random();
        firebaseController.lobbyHandler().setPlayerID(name);
    }

    private void createLobby() {
        firebaseController.createLobby(new LobbyService.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                isHost = true;
                showWaitingRoom(true);
            }

            @Override
            public void onFailure(String error) {
                showError("Create failed: " + error);
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
        firebaseController.joinLobby(code, new LobbyService.LobbyCallback() {
            @Override
            public void onSuccess(String response) {
                isHost = false;
                showWaitingRoom(false);
            }

            @Override
            public void onFailure(String error) {
                showMainOptions();
                showError("Join failed: " + error);
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
            startBtn = new SpaceButton("START GAME");
            startBtn.setDisabled(true);
            startBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (startBtn.isDisabled()) return;
                    firebaseController.startGame(new LobbyService.LobbyCallback() {
                        @Override
                        public void onSuccess(String response) {
                            sm.set(new GameState(sm, batch, new Engine(), assets, firebaseController.lobbyHandler()));
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
                firebaseController.leaveLobby(new LobbyService.LobbyCallback() {
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
        firebaseController.getLobbyStatus(new LobbyService.LobbyStatusCallback() {
            @Override
            public void onUpdate(JsonValue lobbyData) {
                if (inLobby) {
                    if (lobbyData.has("players")) {
                        int count = lobbyData.get("players").size;
                        playerCountLabel.setText("Players: " + count);
                        if (startBtn != null) {
                            startBtn.setDisabled(count < 2);
                        }
                    }
                    if (lobbyData.getBoolean("gameStarted", false)) {
                        inLobby = false;
                        sm.set(new GameState(sm, batch, new Engine(), assets, firebaseController.lobbyHandler()));
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
    protected void update(float dt) {
        if (inLobby) {
            updateTimer += dt;
            if (updateTimer >= UPDATE_INTERVAL) {
                updateTimer = 0;
                firebaseController.lobbyHandler().sendHeartbeat();
                updateLobbyStatus();
            }
            pingTimer += dt;
            if (pingTimer >= pingInterval) {
                pingTimer = 0;
                pingInterval = 5 + new Random().nextFloat() * 5;
                firebaseController.pingGameHandler();
            }
        }
        stage.act(dt);
    }

    @Override
    protected void render() {
        stage.draw();
    }

    @Override
    protected void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    protected void hide() {
        inLobby = false;
    }

    @Override
    public void dispose() {
        if (stage != null) stage.dispose();
    }
}
