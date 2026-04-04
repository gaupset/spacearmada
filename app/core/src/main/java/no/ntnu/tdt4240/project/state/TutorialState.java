package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;

/**
 * TutorialState displays game instructions and tutorial.
 * Currently shows a placeholder "coming soon" message.
 */
public class TutorialState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;

    private Stage stage;

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

        // Title label
        Label title = new Label("TUTORIAL", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        title.setFontScale(1.4f);
        root.add(title).padBottom(40f).row();

        // Placeholder message
        Label placeholder = new Label("Coming soon...", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.textSecondary));
        placeholder.setFontScale(1.0f);
        root.add(placeholder).padBottom(40f).row();

        // Back button - returns to main menu
        SpaceButton backBtn = new SpaceButton("BACK");
        backBtn.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Navigate back to menu
                sm.set(new MenuState(sm, batch, assets));
            }
        });
        root.add(backBtn)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .row();

        stage.addActor(root);
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
