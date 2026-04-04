package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
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
import no.ntnu.tdt4240.project.service.ScoreService;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;

/**
 * GameOverState displays when the player loses the game.
 * Shows the final score and indicates if it's a new high score.
 * Provides options to return to menu or play again.
 */
public class GameOverState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;

    private final int finalScore;
    private final Engine engine;
    private Stage stage;
    private boolean isNewHighScore;

    /**
     * Constructor for GameOverState
     * @param sm StateManager for handling state transitions
     * @param batch SpriteBatch for rendering
     * @param assets Assets manager for textures and fonts
     * @param engine Ashley Engine for ECS
     * @param finalScore The player's final score
     */
    public GameOverState(StateManager sm, SpriteBatch batch, Assets assets, Engine engine, int finalScore) {
        super(sm, batch, assets);
        this.engine = engine;
        this.finalScore = finalScore;
        // Update high score and check if this is a new record
        this.isNewHighScore = ScoreService.getInstance().updateHighScore(finalScore);
    }

    @Override
    protected void setup() {
        // Create stage with viewport for UI rendering
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
    }

    /**
     * Builds the UI layout for the game over screen
     */
    private void buildLayout() {
        // Background image
        Image bg = new Image(new TextureRegionDrawable(assets.getStarsBackground()));
        bg.setScaling(Scaling.fill);
        bg.setFillParent(true);
        stage.addActor(bg);

        // Main table for layout
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        // Title label
        Label title = new Label("GAME OVER", new Label.LabelStyle(assets.getDefaultFont(), Theme.CLASSIC.primary));
        title.setFontScale(1.5f);
        root.add(title).padBottom(10f).row();

        // New high score notification if applicable
        if (isNewHighScore) {
            Label newHigh = new Label("NEW HIGH SCORE!", new Label.LabelStyle(assets.getDefaultFont(), Color.GOLD));
            newHigh.setFontScale(0.8f);
            root.add(newHigh).padBottom(10f).row();
        }

        // Display final score
        Label scoreLabel = new Label("YOUR SCORE: " + finalScore, new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE));
        scoreLabel.setFontScale(1.0f);
        root.add(scoreLabel).padBottom(20f).row();

        // Home button - returns to main menu
        SpaceButton home = new SpaceButton("HOME");
        home.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Navigate back to menu
                sm.set(new MenuState(sm, batch, assets));
            }
        });
        root.add(home)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING)
            .row();

        // Play Again button - starts a new game
        SpaceButton playAgain = new SpaceButton("PLAY AGAIN");
        playAgain.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Start a new game with a fresh engine
                Engine newEngine = new Engine();
                sm.set(new GameState(sm, batch, newEngine, assets));
            }
        });
        root.add(playAgain)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(BUTTON_SPACING)
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
