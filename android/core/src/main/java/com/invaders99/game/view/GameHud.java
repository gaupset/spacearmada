package com.invaders99.game.view;

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
import com.invaders99.game.model.GameModel;
import com.invaders99.service.AudioService;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Theme;

public class GameHud {
    private final Stage stage;
    private final GameModel model;
    private final Table menuPanel;
    private final Texture overlayTex;

    private final TextButton soundButton;
    private final TextButton musicButton;

    public interface QuitListener {
        void onQuit();
    }

    public GameHud(GameModel model, QuitListener quitListener) {
        this.model = model;
        stage = new Stage(new ExtendViewport(GameModel.WORLD_WIDTH, GameModel.WORLD_HEIGHT));

        Skin skin = UiFactory.getInstance().getSkin();
        AudioService audio = AudioService.getInstance();

        // Overlay background
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.7f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        // Menu button
        TextButton menuButton = new TextButton("MENU", skin);
        menuButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                model.menuOpen = !model.menuOpen;
            }
        });

        Table topBar = new Table();
        topBar.setFillParent(true);
        topBar.top().right();
        topBar.add(menuButton).width(80f).height(36f).pad(8f);
        stage.addActor(topBar);

        // Menu panel
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

        // Sound toggle setup: interacts with AudioService to enable/disable sound effects
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

        // Music toggle setup: interacts with AudioService to enable/disable background music
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

        // Resume
        TextButton resumeButton = new TextButton("RESUME", skin);
        resumeButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                model.menuOpen = false;
            }
        });
        content.add(resumeButton).row();

        // Quit
        TextButton quitButton = new TextButton("QUIT", skin);
        quitButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                model.menuOpen = false;
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

    public void act(float delta) {
        menuPanel.setVisible(model.menuOpen);
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
