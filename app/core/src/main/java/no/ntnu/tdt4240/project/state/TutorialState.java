package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.state.tutorial.TutorialGameState;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.ui.view.GameHud;
import no.ntnu.tdt4240.project.util.Theme;

/**
 * TutorialState displays game instructions and tutorial.
 * Currently shows a placeholder "coming soon" message.
 */
public class TutorialState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;

    private Stage stage;
    private Label messageLineOne;
    private Label messageLineTwo;
    private SpaceButton continueButton;
    private int introStep = 0;

    /**
     * Constructor for TutorialState
     * @param sm StateManager for handling state transitions
     * @param batch SpriteBatch for rendering
     * @param assets Assets manager for textures and fonts
     */
    public TutorialState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
    }

    @Override
    protected void setup() {
        // Create stage with viewport for UI rendering
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
    }

    /**
     * Builds the UI layout for the tutorial screen
     */
    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();
        Table exitBar = new Table();
        exitBar.setFillParent(true);
        exitBar.top().right();
        TextButton exitButton = new TextButton("EXIT", UiFactory.getInstance().getSkin());
        exitButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sm.set(new MenuState(sm, batch, assets));
            }
        });
        exitBar.add(exitButton)
            .width(GameHud.TUTORIAL_TOP_EXIT_WIDTH)
            .height(GameHud.TUTORIAL_TOP_EXIT_HEIGHT)
            .pad(GameHud.TUTORIAL_TOP_EXIT_PAD);

        Label.LabelStyle style = new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE);
        messageLineOne = new Label("Welcome to Spacearmada", style);
        messageLineOne.setFontScale(0.85f);
        messageLineOne.setWrap(true);
        messageLineOne.setAlignment(Align.center);
        root.add(messageLineOne).width(VIEWPORT_MIN_WIDTH - 48f).padBottom(16f).row();

        messageLineTwo = new Label("Click CONTINUE to begin the tutorial", style);
        messageLineTwo.setFontScale(0.75f);
        messageLineTwo.setWrap(true);
        messageLineTwo.setAlignment(Align.center);
        root.add(messageLineTwo).width(VIEWPORT_MIN_WIDTH - 48f).padBottom(24f).row();

        continueButton = new SpaceButton("CONTINUE");
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (introStep == 0) {
                    introStep = 1;
                    messageLineOne.setText("Use the touchscreen to move sideways");
                    messageLineTwo.setText("");
                    return;
                }
                sm.set(new TutorialGameState(sm, batch, assets));
            }
        });
        root.add(continueButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .row();

        stage.addActor(root);
        stage.addActor(exitBar);
    }

    @Override
    public void update(float dt) {
        // Update stage animations
        stage.act(dt);
    }

    @Override
    protected void render() {
        // Draw the UI
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Update viewport on screen resize
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        // Cleanup resources
        if (stage != null) stage.dispose();
    }
}
