package no.ntnu.tdt4240.project.ui.view;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.Theme;

public class GameHud {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final Color PAUSE_BUTTON_DISABLED_LABEL = new Color(0.45f, 0.45f, 0.48f, 1f);

    private final Stage stage;
    private final Table menuPanel;
    private final Texture overlayTex;


    private final Label scoreLabel;
    private final Label healthLabel;
    private final Label enemySpeedLabel;
    private final Label fireRateLabel;
    private final Label alienSpawnLabel;
    private final Label shieldLabel;
    private final Label rapidFireLabel;
    private final Label slowEnemiesLabel;

    public interface MenuToggleListener {
        void onMenuToggle(boolean open);
    }

    public interface QuitListener {
        void onQuit();
    }

    public interface PauseListener {
        void onPause();
    }

    public interface SabotageListener {
        void onSabotage();
    }

    public interface PowerupListener {
        void onPowerup();
    }

    private final TextButton sabotageButton;
    private final TextButton powerupButton;
    private final TextButton pauseButton;
    private final Runnable onMenuResumePressed;

    private boolean isMenuOpen = false;

    public GameHud(
        MenuToggleListener menuToggleListener,
        QuitListener quitListener,
        PauseListener pauseListener,
        SabotageListener sabotageListener,
        PowerupListener powerupListener,
        Runnable onMenuResumePressed
    ) {
        this.onMenuResumePressed = onMenuResumePressed;
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));

        Skin skin = UiFactory.getInstance().getSkin();

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.7f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        pauseButton = new TextButton("PAUSE", skin);
        pauseButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (pauseButton.isDisabled()) {
                    return;
                }
                if (pauseListener != null) {
                    pauseListener.onPause();
                }
            }
        });
        sabotageButton = new TextButton("SABOTAGE", skin);
        sabotageButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        sabotageButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (sabotageListener != null) {
                    sabotageListener.onSabotage();
                }
            }
        });
        powerupButton = new TextButton("POWERUP", skin);
        powerupButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        powerupButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (powerupListener != null) {
                    powerupListener.onPowerup();
                }
            }
        });
        TextButton menuButton = new TextButton("MENU", skin);
        menuButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuToggleListener.onMenuToggle(!isMenuOpen);
            }
        });

        // Top-right: Pause, Menu, Sabotage buttons
        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top().right();
        topBar.add(pauseButton).width(80f).height(36f).pad(8f);
        topBar.add(menuButton).width(80f).height(36f).pad(8f);
        topBar.row();
        topBar.add(sabotageButton).colspan(2).right().padTop(2f).padLeft(8f).padRight(8f).height(36f).width(170f);
        topBar.row();
        topBar.add(powerupButton).colspan(2).right().padTop(2f).padBottom(8f).padLeft(8f).padRight(8f).height(36f).width(170f);
        stage.addActor(topBar);

        // Top-left: Score and Health display
        scoreLabel = new Label("SCORE: 0", skin);
        scoreLabel.setFontScale(0.5f);
        scoreLabel.setColor(Color.WHITE);

        healthLabel = new Label("LIVES: 0", skin);
        healthLabel.setFontScale(0.5f);
        healthLabel.setColor(Color.WHITE);

        // Custom label style with white fontColor so actor color is displayed as-is
        // (default skin uses cyan fontColor which zeroes out the red channel)
        Label.LabelStyle effectStyle = new Label.LabelStyle(skin.get(Label.LabelStyle.class));
        effectStyle.fontColor = new Color(Color.WHITE);

        Color sabotageColor = new Color(1f, 0.3f, 0.3f, 1f);
        Color powerupColor = new Color(0.3f, 1f, 0.3f, 1f);

        enemySpeedLabel = new Label("", effectStyle);
        enemySpeedLabel.setFontScale(0.5f);
        enemySpeedLabel.setColor(sabotageColor);
        enemySpeedLabel.setVisible(false);

        fireRateLabel = new Label("", effectStyle);
        fireRateLabel.setFontScale(0.5f);
        fireRateLabel.setColor(sabotageColor);
        fireRateLabel.setVisible(false);

        alienSpawnLabel = new Label("", effectStyle);
        alienSpawnLabel.setFontScale(0.5f);
        alienSpawnLabel.setColor(sabotageColor);
        alienSpawnLabel.setVisible(false);

        shieldLabel = new Label("", effectStyle);
        shieldLabel.setFontScale(0.5f);
        shieldLabel.setColor(powerupColor);
        shieldLabel.setVisible(false);

        rapidFireLabel = new Label("", effectStyle);
        rapidFireLabel.setFontScale(0.5f);
        rapidFireLabel.setColor(powerupColor);
        rapidFireLabel.setVisible(false);

        slowEnemiesLabel = new Label("", effectStyle);
        slowEnemiesLabel.setFontScale(0.5f);
        slowEnemiesLabel.setColor(powerupColor);
        slowEnemiesLabel.setVisible(false);

        Table statsBar = new Table();
        statsBar.setFillParent(true);
        statsBar.top().left();
        statsBar.add(scoreLabel).padLeft(10f).padTop(10f).row();
        statsBar.add(healthLabel).padLeft(10f).padTop(5f).row();
        statsBar.add(shieldLabel).padLeft(10f).padTop(8f).row();
        statsBar.add(enemySpeedLabel).padLeft(10f).padTop(2f).row();
        statsBar.add(slowEnemiesLabel).padLeft(10f).padTop(2f).row();
        statsBar.add(fireRateLabel).padLeft(10f).padTop(2f).row();
        statsBar.add(rapidFireLabel).padLeft(10f).padTop(2f).row();
        statsBar.add(alienSpawnLabel).padLeft(10f).padTop(2f);
        stage.addActor(statsBar);

        menuPanel = new Table();
        menuPanel.setFillParent(true);
        menuPanel.center();
        menuPanel.setBackground(new TextureRegionDrawable(overlayTex));
        menuPanel.setVisible(false);

        Table content = new Table();
        content.defaults().width(200f).height(44f).padBottom(12f);

        Label title = new Label("MENU", skin);
        title.setFontScale(1.2f);
        content.add(title).padBottom(24f).row();

        TextButton resumeButton = new TextButton("RESUME", skin);
        resumeButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuToggleListener.onMenuToggle(false);
                if (GameHud.this.onMenuResumePressed != null) {
                    GameHud.this.onMenuResumePressed.run();
                }
            }
        });
        content.add(resumeButton).row();

        TextButton quitButton = new TextButton("QUIT", skin);
        quitButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                menuToggleListener.onMenuToggle(false);
                quitListener.onQuit();
            }
        });
        content.add(quitButton).row();

        menuPanel.add(content);
        stage.addActor(menuPanel);
    }

    public void act(float delta, boolean isMenuOpen, boolean isSabotageVisible, boolean isPowerupVisible,
                    boolean isPauseReady, int score, int health,
                    float enemySpeedRemaining, float fireRateRemaining, float alienSpawnRemaining,
                    float shieldRemaining, float rapidFireRemaining, float slowEnemiesRemaining) {
        this.isMenuOpen = isMenuOpen;
        menuPanel.setVisible(isMenuOpen);
        sabotageButton.setVisible(isSabotageVisible);
        powerupButton.setVisible(isPowerupVisible);
        pauseButton.setDisabled(!isPauseReady);
        if (isPauseReady) {
            pauseButton.getLabel().setColor(Color.WHITE);
        } else {
            pauseButton.getLabel().setColor(PAUSE_BUTTON_DISABLED_LABEL);
        }

        scoreLabel.setText("SCORE: " + score);
        healthLabel.setText("LIVES: " + health);

        updateEffectLabel(enemySpeedLabel, "2x ENEMY SPEED", enemySpeedRemaining);
        updateEffectLabel(fireRateLabel, "0.5x FIRE RATE", fireRateRemaining);
        updateEffectLabel(alienSpawnLabel, "2x ALIEN SPAWN", alienSpawnRemaining);
        updateEffectLabel(shieldLabel, "SHIELD", shieldRemaining);
        updateEffectLabel(rapidFireLabel, "2x FIRE RATE", rapidFireRemaining);
        updateEffectLabel(slowEnemiesLabel, "0.5x ENEMY SPEED", slowEnemiesRemaining);

        stage.act(delta);
    }

    private void updateEffectLabel(Label label, String name, float remaining) {
        if (remaining > 0f) {
            label.setText(name + " (" + ((int) remaining + 1) + "s)");
            label.setVisible(true);
        } else {
            label.setVisible(false);
        }
    }

    public void draw() {
        stage.draw();
    }

    public Stage getStage() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void dispose() {
        stage.dispose();
        overlayTex.dispose();
    }
}
