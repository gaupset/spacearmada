package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.model.Game;
import com.invaders99.model.Sabotage;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Theme;
import com.invaders99.view.GameStateManager;

import java.util.Locale;

public class SabotageState extends State {
    /** Initial period: gameplay frozen; after this elapses the match runs behind the overlay until RETURN or charges are used. */
    private static final float SABOTAGE_UNPAUSE_TIMER_SEC = 10f;
    private static final int SABOTAGE_EFFECT_DURATION_SEC = 10;
    /** Same height as {@link com.invaders99.view.GameHud} TextButtons (pause / menu / sabotage). */
    private static final float BUTTON_HEIGHT = 36f;
    private static final float BUTTON_GAP = 8f;
    private static final float RETURN_BOTTOM_PAD = 16f;

    private final GameState gameState;
    private final Game model;
    private Stage stage;
    private Texture overlayTex;
    private float unpauseTimeLeft;
    /** After {@link #unpauseTimeLeft} reaches zero: simulation and HUD input run; overlay stays until RETURN or all charges spent. */
    private boolean gameplayRunningBehindOverlay;
    private Label timerLabel;
    private Label chargesLabel;

    public SabotageState(GameStateManager gsm, GameState gameState, Game model) {
        super(gsm);
        this.gameState = gameState;
        this.model = model;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(Game.WORLD_WIDTH, Game.WORLD_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        unpauseTimeLeft = SABOTAGE_UNPAUSE_TIMER_SEC;
        gameplayRunningBehindOverlay = false;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.6f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        buildLayout();
        gameState.setGameplayPaused(true);
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
        root.add(stack).expand().center().width(Game.WORLD_WIDTH).row();

        TextButton returnButton = new TextButton("RETURN", skin);
        returnButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onReturn();
            }
        });
        root.add(returnButton)
            .width(Game.WORLD_WIDTH)
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
            .width(Game.WORLD_WIDTH)
            .height(BUTTON_HEIGHT)
            .padBottom(gapBelow ? BUTTON_GAP : 0f)
            .row();
    }

    private void onReturn() {
        gsm.pop();
    }

    private void onSabotageChosen(String sabotageType) {
        if (model.getAvailableSabotageCount() <= 0) {
            return;
        }
        if (gameState.isDevGame()) {
            gameState.triggerDevSelfSabotage(sabotageType);
        } else {
            Sabotage payload = new Sabotage();
            payload.type = sabotageType;
            payload.duration = SABOTAGE_EFFECT_DURATION_SEC;
            gameState.sendLobbySabotage(payload);
        }
        model.recordSabotageUse();
        if (model.getAvailableSabotageCount() <= 0) {
            gsm.pop();
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
        int n = model.getAvailableSabotageCount();
        return n == 1 ? "1 sabotage available" : n + " sabotages available";
    }

    private static String formatUnpauseTimer(float seconds) {
        return String.format(Locale.US, "Unpause timer: %.1fs", Math.max(0f, seconds));
    }

    private static final String GAME_NOT_PAUSED_LABEL = "Game not paused";

    private void onUnpauseTimerFinished() {
        unpauseTimeLeft = 0f;
        gameplayRunningBehindOverlay = true;
        if (timerLabel != null) {
            timerLabel.setText(GAME_NOT_PAUSED_LABEL);
        }
        gameState.setGameplayPaused(false);
        attachGameplayInputBehindOverlay();
    }

    /** Sabotage UI first, then normal game + HUD input so the player can play while the overlay is open. */
    private void attachGameplayInputBehindOverlay() {
        InputMultiplexer mux = gameState.getInputMultiplexer();
        if (mux == null) {
            Gdx.input.setInputProcessor(stage);
            return;
        }
        InputMultiplexer combined = new InputMultiplexer();
        combined.addProcessor(stage);
        combined.addProcessor(mux);
        Gdx.input.setInputProcessor(combined);
    }

    @Override
    public void update(float dt) {
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
    public void render(SpriteBatch batch) {
        gameState.renderFrozen(batch, Gdx.graphics.getDeltaTime());
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
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
