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
import no.ntnu.tdt4240.project.service.AudioService;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.Theme;

public class GameHud {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final Color PAUSE_BUTTON_DISABLED_LABEL = new Color(0.45f, 0.45f, 0.48f, 1f);

    private final Stage stage;
    private final Table menuPanel;
    private final Texture overlayTex;

    private final TextButton soundButton;
    private final TextButton musicButton;

    private final Label scoreLabel;
    private final Label healthLabel;

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

    private final TextButton sabotageButton;
    private final TextButton pauseButton;
    private final Runnable onMenuResumePressed;

    private boolean isMenuOpen = false;

    public GameHud(
        MenuToggleListener menuToggleListener,
        QuitListener quitListener,
        PauseListener pauseListener,
        SabotageListener sabotageListener,
        Runnable onMenuResumePressed
    ) {
        this.onMenuResumePressed = onMenuResumePressed;
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));

        Skin skin = UiFactory.getInstance().getSkin();
        AudioService audio = AudioService.getInstance();

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
        topBar.add(sabotageButton).colspan(2).right().padTop(2f).padBottom(8f).padLeft(8f).padRight(8f).height(36f).width(170f);
        stage.addActor(topBar);

        // Top-left: Score and Health display
        scoreLabel = new Label("SCORE: 0", skin);
        scoreLabel.setFontScale(0.5f);
        scoreLabel.setColor(Color.WHITE);

        healthLabel = new Label("LIVES: 0", skin);
        healthLabel.setFontScale(0.5f);
        healthLabel.setColor(Color.WHITE);

        Table statsBar = new Table();
        statsBar.setFillParent(true);
        statsBar.top().left();
        statsBar.add(scoreLabel).padLeft(10f).padTop(10f).row();
        statsBar.add(healthLabel).padLeft(10f).padTop(5f);
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

        soundButton = new TextButton(getSoundText(audio.isSoundEnabled()), skin);
        soundButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        soundButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = !audio.isSoundEnabled();
                audio.setSoundEnabled(enabled);
                soundButton.setText(getSoundText(enabled));
            }
        });
        content.add(soundButton).row();

        musicButton = new TextButton(getMusicText(audio.isMusicEnabled()), skin);
        musicButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        musicButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                boolean enabled = !audio.isMusicEnabled();
                audio.setMusicEnabled(enabled);
                musicButton.setText(getMusicText(enabled));
            }
        });
        content.add(musicButton).row();

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

    private String getSoundText(boolean enabled) {
        return "SOUND: " + (enabled ? "ON" : "OFF");
    }

    private String getMusicText(boolean enabled) {
        return "MUSIC: " + (enabled ? "ON" : "OFF");
    }

    /**
     * Updates the HUD state and displays.
     * @param delta Time since last frame
     * @param isMenuOpen Whether the menu panel is visible
     * @param isSabotageVisible Whether sabotage button should be shown
     * @param isPauseReady Whether pause button is clickable
     * @param score Current player score
     * @param health Current player health/lives
     */
    public void act(float delta, boolean isMenuOpen, boolean isSabotageVisible, boolean isPauseReady, int score, int health) {
        this.isMenuOpen = isMenuOpen;
        menuPanel.setVisible(isMenuOpen);
        sabotageButton.setVisible(isSabotageVisible);
        pauseButton.setDisabled(!isPauseReady);
        if (isPauseReady) {
            pauseButton.getLabel().setColor(Color.WHITE);
        } else {
            pauseButton.getLabel().setColor(PAUSE_BUTTON_DISABLED_LABEL);
        }

        // Update score and health display
        scoreLabel.setText("SCORE: " + score);
        healthLabel.setText("LIVES: " + health);

        stage.act(delta);
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
