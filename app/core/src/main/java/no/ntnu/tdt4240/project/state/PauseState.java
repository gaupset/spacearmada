package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import java.util.Locale;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.Theme;

public class PauseState extends State {
    private static final float PAUSE_DURATION = 10f;
    /** Same as {@link SabotageState} RETURN row. */
    private static final float RETURN_BUTTON_HEIGHT = 36f;
    private static final float RETURN_BOTTOM_PAD = 16f;

    private final GameState gameState;
    private Stage stage;
    private Texture overlayTex;
    private float pauseTimeLeft;
    private Label timerLabel;

    public PauseState(StateManager sm, SpriteBatch batch, Assets assets, GameState gameState) {
        super(sm, batch, assets);
        this.gameState = gameState;
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(360, 640));
        pauseTimeLeft = PAUSE_DURATION;

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.6f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        gameState.setExitPauseWhenMenuCloses(true);

        com.badlogic.gdx.InputMultiplexer mux = new com.badlogic.gdx.InputMultiplexer();
        mux.addProcessor(gameState.getHudStage());
        mux.addProcessor(stage);
        Gdx.input.setInputProcessor(stage);

        buildLayout();
    }

    /**
     * HUD stage first (same {@link com.invaders99.view.GameHud} top bar as in play — **MENU** works), then pause overlay.
     * When the menu panel is open, touches still hit HUD first (RESUME, QUIT, sound, …).
     */
    private void syncInputProcessor() {
        Stage hudStage = gameState.getHudStage();
        if (hudStage != null) {
            Gdx.input.setInputProcessor(new InputMultiplexer(hudStage, stage));
        } else {
            Gdx.input.setInputProcessor(stage);
        }
    }

    private void buildLayout() {
        Skin skin = UiFactory.getInstance().getSkin();

        Table root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.childrenOnly);
        root.setBackground(new TextureRegionDrawable(overlayTex));
        root.top();

        Table center = new Table();
        center.center();

        Label title = new Label("Game paused", skin);
        title.setFontScale(1.5f);
        center.add(title).padTop(28f).padBottom(12f).row();

        timerLabel = new Label(formatUnpauseTimer(pauseTimeLeft), skin);
        timerLabel.setFontScale(1.2f);
        center.add(timerLabel).padBottom(20f).row();

        TextButton menuButton = new TextButton("MENU", skin);
        menuButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        menuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                gameState.setMenuOpen(!gameState.isMenuOpen());
            }
        });
        center.add(menuButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(24f)
            .row();

//        if (gameState.getLobbyHandler() != null) {
//            SpaceButton sabotageButton = new SpaceButton("SABOTAGE");
//            sabotageButton.addListener(new ClickListener() {
//                @Override
//                public void clicked(InputEvent event, float x, float y) {
//                    gameState.getLobbyHandler().setSabotage(new Sabotage());
//                }
//            });
//            center.add(sabotageButton).padBottom(20f).row();
//        }

        TextButton returnButton = new TextButton("RETURN", skin);
        returnButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sm.pop();
            }
        });
        center.add(returnButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .padBottom(RETURN_BOTTOM_PAD);

        root.add(center).expand().center();

        stage.addActor(root);
    }

    private static String formatUnpauseTimer(float seconds) {
        return String.format(Locale.US, "Unpause timer: %.1fs", Math.max(0f, seconds));
    }

    @Override
    public void update(float dt) {
        pauseTimeLeft -= dt;
        if (timerLabel != null) {
            timerLabel.setText(formatUnpauseTimer(pauseTimeLeft));
        }
        if (pauseTimeLeft <= 0f) {
            sm.pop();
            return;
        }
        stage.act(dt);
    }

    @Override
    public void render() {
        gameState.renderFrozen();

        if (!gameState.isMenuOpen()) {
            stage.getViewport().apply();
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    public void dispose() {
        gameState.setExitPauseWhenMenuCloses(false);
        if (stage != null) stage.dispose();
        if (overlayTex != null) overlayTex.dispose();
    }
}
