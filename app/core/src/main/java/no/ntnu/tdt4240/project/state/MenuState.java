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
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.service.ScoreService;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;
import no.ntnu.tdt4240.project.ui.UiFactory;

public class MenuState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;
    private static final float BUTTON_SPACING = 16f;

    private Stage stage;

    public MenuState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(VIEWPORT_MIN_WIDTH, VIEWPORT_MIN_HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.top();

        Image logo = new Image(new TextureRegionDrawable(assets.getLogoCrop()));
        logo.setScaling(Scaling.fit);
        root.add(logo).expandX().fillX().height(VIEWPORT_MIN_HEIGHT / 3f).padTop(20f).row();

        // High Score Display
        int highScore = ScoreService.getInstance().getHighScore();
        Label highLabel = new Label("PERSONAL HIGH SCORE: " + highScore,
            new Label.LabelStyle(assets.getDefaultFont(), Color.GOLD));
        highLabel.setFontScale(0.6f);
        root.add(highLabel).padBottom(10f).row();

        Table buttons = new Table();
        String[] buttonLabels = {"DEV GAME", "LOBBY", "LOGIN", "SIGNUP", "SETTINGS"};
        for (String label : buttonLabels) {
            SpaceButton button = new SpaceButton(label);
            if ("LOBBY".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
//                        menuController.onLobbyClicked();
                    }
                });
            } else if ("SETTINGS".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        sm.set(new SettingsState(sm, batch, assets));
                    }
                });
            } else if ("DEV GAME".equals(label)) {
                button.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        Engine engine = new Engine();
                        sm.set(new GameState(sm, batch, engine, assets));
                    }
                });
            }
            buttons.add(button)
                .width(Theme.BUTTON_WIDTH)
                .height(Theme.BUTTON_HEIGHT)
                .padBottom(BUTTON_SPACING)
                .row();
        }
        root.add(buttons).expand().center().row();

        stage.addActor(root);
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
