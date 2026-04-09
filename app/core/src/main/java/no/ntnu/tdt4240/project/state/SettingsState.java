package no.ntnu.tdt4240.project.state;

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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.service.AudioService;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.Theme;


public class SettingsState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;
    private static final float FONT_SCALE_TITLE = 1.4f;

    private Stage stage;
    private boolean showingFirebase = false;

    public SettingsState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
    }

    private void buildLayout() {
        stage.clear();
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        stage.addActor(root);

        UiFactory ui = UiFactory.getInstance();
        AudioService audio = AudioService.getInstance();

        if (!showingFirebase) {
            // Main settings

            // Title
            Label titleLabel = new Label("SETTINGS", ui.getSkin());
            titleLabel.setFontScale(FONT_SCALE_TITLE);
            root.add(titleLabel).center().padBottom(40f).row();

            // Music
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

            // Sound
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

            // Firebase Tests button
            SpaceButton firebaseBtn = new SpaceButton("FIREBASE TESTS");
            firebaseBtn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showingFirebase = true;
                    buildLayout();
                }
            });
            root.add(firebaseBtn)
                .width(Theme.BUTTON_WIDTH)
                .height(Theme.BUTTON_HEIGHT)
                .padBottom(BUTTON_SPACING)
                .row();

            // Back button to Home
            SpaceButton backButton = new SpaceButton("BACK");
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    sm.set(new MenuState(sm, batch, assets));
                }
            });
            root.add(backButton)
                .width(Theme.BUTTON_WIDTH)
                .height(Theme.BUTTON_HEIGHT)
                .row();

        } else {
            // Firebase Tests

            Label titleLabel = new Label("FIREBASE TESTS", ui.getSkin());
            titleLabel.setFontScale(FONT_SCALE_TITLE);
            root.add(titleLabel)
                .center()
                .padBottom(30f)
                .row();

            // Back button to main settings menu
            SpaceButton backButton = new SpaceButton("BACK");
            backButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    showingFirebase = false;
                    buildLayout();
                }
            });
            root.add(backButton)
                .width(Theme.BUTTON_WIDTH)
                .height(Theme.BUTTON_HEIGHT)
                .padTop(10f)
                .row();
        }
    }

    @Override
    public void update(float dt) {
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
