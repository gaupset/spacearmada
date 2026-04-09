package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.Locale;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.model.Sabotage;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.Theme;

public class SabotageState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float SABOTAGE_UNPAUSE_TIMER_SEC = 10f;
    private static final int SABOTAGE_EFFECT_DURATION_SEC = 10;
    private static final float BUTTON_HEIGHT = 36f;
    private static final float BUTTON_GAP = 8f;
    private static final float RETURN_BOTTOM_PAD = 16f;
    private static final String GAME_NOT_PAUSED_LABEL = "Game not paused";

    private final GameState gameState;
    private Stage stage;
    private Texture overlayTex;
    private float unpauseTimeLeft;
    private boolean gameplayRunningBehindOverlay;
    private Label timerLabel;
    private Label chargesLabel;

    public SabotageState(StateManager sm, Assets assets, GameState gameState) {
        super(sm, null, assets);
        this.gameState = gameState;
    }

    public SabotageState(StateManager sm, com.badlogic.gdx.graphics.g2d.SpriteBatch batch, Assets assets, GameState gameState) {
        super(sm, batch, assets);
        this.gameState = gameState;
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        unpauseTimeLeft = SABOTAGE_UNPAUSE_TIMER_SEC;
        gameplayRunningBehindOverlay = false;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.6f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        buildLayout();
    }

    @Override
    protected void show() {
        gameState.setGameplayPaused(true);
        Gdx.input.setInputProcessor(stage);
    }

    private void buildLayout() {
        Skin skin = UiFactory.getInstance().getSkin();

        Table root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.childrenOnly);
        root.setBackground(new TextureRegionDrawable(overlayTex));
        root.top();

        timerLabel = new Label(formatUnpauseTimer(unpauseTimeLeft), skin);
        timerLabel.setFontScale(1.2f);
        root.add(timerLabel).padTop(28f).row();

        chargesLabel = new Label(formatChargesLine(), skin);
        chargesLabel.setFontScale(0.85f);
        root.add(chargesLabel).padTop(6f).padBottom(8f).row();

        Table stack = new Table();
        addSabotageRow(stack, Sabotage.TYPE_ENEMY_SPEED, "2x enemy speed", true);
        addSabotageRow(stack, Sabotage.TYPE_HALF_PLAYER_BULLETS, "0.5x player bullets", true);
        addSabotageRow(stack, Sabotage.TYPE_DOUBLE_ALIENS, "2x number of aliens", true);
        root.add(stack).expand().center().width(VIEWPORT_MIN_WIDTH).row();

        TextButton returnButton = new TextButton("RETURN", skin);
        returnButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sm.pop();
            }
        });
        root.add(returnButton)
            .width(VIEWPORT_MIN_WIDTH)
            .height(BUTTON_HEIGHT)
            .padBottom(RETURN_BOTTOM_PAD);

        stage.addActor(root);
    }

    private void addSabotageRow(Table parent, String sabotageType, String label, boolean gapBelow) {
        SpaceButton button = new SpaceButton(label);
        button.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSabotageChosen(sabotageType);
            }
        });
        parent.add(button)
            .width(VIEWPORT_MIN_WIDTH)
            .height(BUTTON_HEIGHT)
            .padBottom(gapBelow ? BUTTON_GAP : 0f)
            .row();
    }

    private void onSabotageChosen(String sabotageType) {
        if (gameState.getAvailableSabotageCount() <= 0) {
            return;
        }
        gameState.applySabotage(sabotageType, SABOTAGE_EFFECT_DURATION_SEC);
        gameState.recordSabotageUse();
        if (gameState.getAvailableSabotageCount() <= 0) {
            sm.pop();
        } else {
            refreshChargesLabel();
        }
    }

    private void refreshChargesLabel() {
        if (chargesLabel != null) {
            chargesLabel.setText(formatChargesLine());
        }
    }

    private String formatChargesLine() {
        int n = gameState.getAvailableSabotageCount();
        return n == 1 ? "1 sabotage available" : n + " sabotages available";
    }

    private static String formatUnpauseTimer(float seconds) {
        return String.format(Locale.US, "Unpause timer: %.1fs", Math.max(0f, seconds));
    }

    private void onUnpauseTimerFinished() {
        unpauseTimeLeft = 0f;
        gameplayRunningBehindOverlay = true;
        if (timerLabel != null) {
            timerLabel.setText(GAME_NOT_PAUSED_LABEL);
        }
        gameState.setGameplayPaused(false);
        attachGameplayInputBehindOverlay();
    }

    private void attachGameplayInputBehindOverlay() {
        InputMultiplexer gameMux = gameState.getInputMultiplexer();
        if (gameMux == null) {
            Gdx.input.setInputProcessor(stage);
            return;
        }
        InputMultiplexer combined = new InputMultiplexer();
        combined.addProcessor(stage);
        combined.addProcessor(gameMux);
        Gdx.input.setInputProcessor(combined);
    }

    @Override
    protected void update(float dt) {
        if (!gameplayRunningBehindOverlay) {
            unpauseTimeLeft -= dt;
            if (unpauseTimeLeft <= 0f) {
                onUnpauseTimerFinished();
                gameState.updateGameplay(dt);
            } else if (timerLabel != null) {
                timerLabel.setText(formatUnpauseTimer(unpauseTimeLeft));
            }
        } else {
            gameState.updateGameplay(dt);
        }
        stage.act(dt);
    }

    @Override
    protected void render() {
        gameState.renderFrozen();
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    protected void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    protected void dispose() {
        gameState.setGameplayPaused(false);
        gameState.restoreDefaultInput();
        if (stage != null) {
            stage.dispose();
        }
        if (overlayTex != null) {
            overlayTex.dispose();
        }
    }
}
