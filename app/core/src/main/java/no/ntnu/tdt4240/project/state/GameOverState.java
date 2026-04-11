package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.service.LobbyService;
import no.ntnu.tdt4240.project.service.ScoreService;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;

public class GameOverState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;
    private static final float POLL_INTERVAL = 2f;

    private final int finalScore;
    private final Engine engine;
    private final LobbyService lobbyService;
    private Stage stage;
    private boolean isNewHighScore;

    // Online mode UI
    private Label titleLabel;
    private Table scoreboardTable;
    private float pollTimer = 0f;
    private boolean gameEndedDetected = false;
    private boolean winRecorded = false;

    /** Singleplayer constructor. */
    public GameOverState(StateManager sm, SpriteBatch batch, Assets assets, Engine engine, int finalScore) {
        this(sm, batch, assets, engine, finalScore, null);
    }

    /** Online constructor — pass lobbyService for live scoreboard. */
    public GameOverState(StateManager sm, SpriteBatch batch, Assets assets, Engine engine, int finalScore, LobbyService lobbyService) {
        super(sm, batch, assets);
        this.engine = engine;
        this.finalScore = finalScore;
        this.lobbyService = lobbyService;
        this.isNewHighScore = (lobbyService == null) && ScoreService.getInstance().updateHighScore(finalScore);
    }

    private boolean isOnline() {
        return lobbyService != null;
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);

        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        if (isOnline()) {
            buildOnlineLayout();
        } else {
            buildSingleplayerLayout();
        }
    }

    private void buildSingleplayerLayout() {
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

        SpaceButton home = new SpaceButton("HOME");
        home.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sm.set(new MenuState(sm, batch, assets));
            }
        });
        root.add(home).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).padBottom(BUTTON_SPACING).row();

        SpaceButton playAgain = new SpaceButton("PLAY AGAIN");
        playAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Engine newEngine = new Engine();
                sm.set(new GameState(sm, batch, newEngine, assets));
            }
        });
        root.add(playAgain).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).padBottom(BUTTON_SPACING).row();

        stage.addActor(root);
    }

    private void buildOnlineLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.top();

        titleLabel = new Label("GAME OVER", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        titleLabel.setFontScale(1.5f);
        root.add(titleLabel).padTop(40f).padBottom(10f).row();

        Label scoreLabel = new Label("YOUR SCORE: " + finalScore, new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE));
        scoreLabel.setFontScale(0.8f);
        root.add(scoreLabel).padBottom(20f).row();

        Label boardTitle = new Label("SCOREBOARD", new Label.LabelStyle(assets.getDefaultFont(), Color.GOLD));
        boardTitle.setFontScale(0.7f);
        root.add(boardTitle).padBottom(8f).row();

        scoreboardTable = new Table();
        root.add(scoreboardTable).expandX().fillX().padLeft(20f).padRight(20f).row();

        // Spacer to push button to bottom
        root.add().expand().row();

        SpaceButton home = new SpaceButton("HOME");
        home.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                lobbyService.leaveLobby(new LobbyService.LobbyCallback() {
                    @Override
                    public void onSuccess(String success) {
                        sm.set(new MenuState(sm, batch, assets));
                    }
                    @Override
                    public void onFailure(String error) {
                        sm.set(new MenuState(sm, batch, assets));
                    }
                });
            }
        });
        root.add(home).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).padBottom(30f).row();

        stage.addActor(root);
    }

    private void updateScoreboard(JsonValue lobbyData) {
        JsonValue players = lobbyData.get("players");
        if (players == null) return;

        boolean gameEnded = lobbyData.getBoolean("gameEnded", false);

        // Collect player entries
        ArrayList<JsonValue> playerList = new ArrayList<>();
        for (JsonValue player = players.child; player != null; player = player.next) {
            playerList.add(player);
        }

        // Sort by score descending
        Collections.sort(playerList, new Comparator<JsonValue>() {
            @Override
            public int compare(JsonValue a, JsonValue b) {
                return Integer.compare(b.getInt("score", 0), a.getInt("score", 0));
            }
        });

        scoreboardTable.clear();
        Label.LabelStyle white = new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE);
        Label.LabelStyle dim = new Label.LabelStyle(assets.getDefaultFont(), new Color(0.6f, 0.6f, 0.6f, 1f));
        Label.LabelStyle green = new Label.LabelStyle(assets.getDefaultFont(), Color.GREEN);

        for (JsonValue player : playerList) {
            String name = player.getString("actualName", player.name);
            int score = player.getInt("score", 0);
            boolean isGameOver = player.getBoolean("gameOver", false);
            boolean leftLobby = player.getBoolean("leftLobby", false);

            String status;
            Label.LabelStyle statusStyle;
            if (leftLobby) {
                status = "LEFT";
                statusStyle = dim;
            } else if (isGameOver) {
                status = "GAME OVER";
                statusStyle = dim;
            } else {
                status = "PLAYING";
                statusStyle = green;
            }

            Label nameLabel = new Label(name, white);
            nameLabel.setFontScale(0.5f);
            Label scoreLabel = new Label(String.valueOf(score), white);
            scoreLabel.setFontScale(0.5f);
            Label statusLabel = new Label(status, statusStyle);
            statusLabel.setFontScale(0.4f);

            scoreboardTable.add(nameLabel).expandX().left().padBottom(4f);
            scoreboardTable.add(scoreLabel).padLeft(10f).padBottom(4f);
            scoreboardTable.add(statusLabel).padLeft(10f).padBottom(4f).row();
        }

        // Handle game ended — determine winner
        if (gameEnded && !gameEndedDetected) {
            gameEndedDetected = true;
            // Winner is either the last active player or highest score
            String myId = lobbyService.lobbyUserID;
            boolean iWon = false;

            // Check if I'm the last active player
            JsonValue me = players.get(myId);
            if (me != null && !me.getBoolean("gameOver", false) && !me.getBoolean("leftLobby", false)) {
                iWon = true;
            }

            // Or if everyone is done, check if I have the highest score
            if (!iWon && !playerList.isEmpty()) {
                JsonValue top = playerList.get(0);
                if (top.name != null && top.name.equals(myId)) {
                    iWon = true;
                }
            }

            if (iWon && !winRecorded) {
                winRecorded = true;
                titleLabel.setText("VICTORY!");
                titleLabel.setStyle(new Label.LabelStyle(assets.getDefaultFont(), Color.GOLD));
                ScoreService.getInstance().incrementOnlineWins();
            }
        }
    }

    @Override
    public void update(float dt) {
        if (isOnline()) {
            pollTimer += dt;
            if (pollTimer >= POLL_INTERVAL) {
                pollTimer = 0f;
                lobbyService.sendHeartbeat();
                lobbyService.getLobbyStatus(new LobbyService.LobbyStatusCallback() {
                    @Override
                    public void onUpdate(JsonValue lobbyData) {
                        updateScoreboard(lobbyData);
                    }
                    @Override
                    public void onFailure(String error) {}
                });
            }
        }
        stage.act(dt);
    }

    @Override
    protected void render() {
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
