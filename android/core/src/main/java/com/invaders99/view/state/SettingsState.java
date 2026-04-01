package com.invaders99.view.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.invaders99.controller.MainController;
import com.invaders99.service.AudioService;
import com.invaders99.ui.FirebaseTestWidget;
import com.invaders99.ui.SpaceButton;
import com.invaders99.ui.UiFactory;
import com.invaders99.util.Theme;
import com.invaders99.view.GameStateManager;

public class SettingsState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;
    private static final float FONT_SCALE_TITLE = 1.4f;

    private final MainController main;
    private Stage stage;
    private FirebaseTestWidget functionsWidget;
    private FirebaseTestWidget firestoreWidget;
    private FirebaseTestWidget databaseWidget;

    public SettingsState(GameStateManager gsm, MainController main) {
        super(gsm);
        this.main = main;
    }

    @Override
    public void show() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
        functionsWidget.autoTest();
        firestoreWidget.autoTest();
        databaseWidget.autoTest();
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        UiFactory ui = UiFactory.getInstance();
        AudioService audio = AudioService.getInstance();

        Label titleLabel = new Label("SETTINGS", ui.getSkin());
        titleLabel.setFontScale(FONT_SCALE_TITLE);
        root.add(titleLabel).center().padBottom(40f).row();

        root.add(new Label("MUSIC VOLUME", ui.getSkin(), "secondary")).padBottom(5f).row();
        Slider musicSlider = new Slider(0f, 1f, 0.05f, false, ui.getSkin());
        musicSlider.setValue(audio.getMusicVolume());
        musicSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.setMusicVolume(musicSlider.getValue());
            }
        });
        root.add(musicSlider).width(Theme.BUTTON_WIDTH).padBottom(20f).row();

        root.add(new Label("SOUND VOLUME", ui.getSkin(), "secondary")).padBottom(5f).row();
        Slider soundSlider = new Slider(0f, 1f, 0.05f, false, ui.getSkin());
        soundSlider.setValue(audio.getSoundVolume());
        soundSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                audio.setSoundVolume(soundSlider.getValue());
            }
        });
        root.add(soundSlider).width(Theme.BUTTON_WIDTH).padBottom(40f).row();

        functionsWidget = new FirebaseTestWidget("CLOUD FUNCTIONS", FirebaseTestWidget.SERVICE_FUNCTIONS);
        root.add(functionsWidget).width(Theme.BUTTON_WIDTH).padBottom(BUTTON_SPACING).row();

        firestoreWidget = new FirebaseTestWidget("FIRESTORE", FirebaseTestWidget.SERVICE_FIRESTORE);
        root.add(firestoreWidget).width(Theme.BUTTON_WIDTH).padBottom(BUTTON_SPACING).row();

        databaseWidget = new FirebaseTestWidget("REALTIME DATABASE", FirebaseTestWidget.SERVICE_DATABASE);
        root.add(databaseWidget).width(Theme.BUTTON_WIDTH).padBottom(BUTTON_SPACING).row();

        SpaceButton backButton = new SpaceButton("BACK");
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gsm.set(new MenuState(gsm, main));
            }
        });
        root.add(backButton).width(Theme.BUTTON_WIDTH).height(Theme.BUTTON_HEIGHT).padTop(BUTTON_SPACING).row();

        stage.addActor(root);
    }

    @Override
    public void update(float dt) {
        stage.act(dt);
    }

    @Override
    public void render(SpriteBatch batch) {
        Theme theme = UiFactory.getInstance().getTheme();
        ScreenUtils.clear(theme.background);
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
