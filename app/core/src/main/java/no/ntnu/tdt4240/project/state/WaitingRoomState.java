package no.ntnu.tdt4240.project.state;

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
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;

/**
 * WaitingRoomState is the multiplayer lobby screen.
 * Players can create or join lobbies to play together.
 * Currently shows a placeholder "coming soon" message until Firebase is integrated.
 */
public class WaitingRoomState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;

    private Stage stage;

    /**
     * Constructor for WaitingRoomState
     * @param sm StateManager for handling state transitions
     * @param batch SpriteBatch for rendering
     * @param assets Assets manager for textures and fonts
     */
    public WaitingRoomState(StateManager sm, SpriteBatch batch, Assets assets) {
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
     * Builds the UI layout for the waiting room / lobby screen
     */
    private void buildLayout() {
        // Background image
        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Title label
        Label title = new Label("LOBBY", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        title.setFontScale(1.4f);
        root.add(title).padBottom(40f).row();

        // Placeholder message
        Label placeholder = new Label("Coming soon...", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.textSecondary));
        placeholder.setFontScale(1.0f);
        root.add(placeholder).padBottom(20f).row();

        // Info message
        Label info = new Label("Firebase integration required for multiplayer", new Label.LabelStyle(assets.getDefaultFont(), Color.GRAY));
        info.setFontScale(0.6f);
        root.add(info).padBottom(40f).row();

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
