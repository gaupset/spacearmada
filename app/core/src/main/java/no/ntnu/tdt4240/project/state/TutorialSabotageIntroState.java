package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;

public class TutorialSabotageIntroState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;

    private Stage stage;

    public TutorialSabotageIntroState(StateManager sm, SpriteBatch batch, Assets assets) {
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
        root.center();

        Label.LabelStyle style = new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE);
        Label lineOne = new Label("Earn 5 points to sabotage another player", style);
        lineOne.setWrap(true);
        lineOne.setAlignment(Align.center);
        lineOne.setFontScale(0.75f);
        root.add(lineOne).width(VIEWPORT_MIN_WIDTH - 48f).padBottom(24f).row();

        SpaceButton continueButton = new SpaceButton("CONTINUE");
        continueButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                sm.set(new TutorialSabotageState(sm, batch, assets));
            }
        });
        root.add(continueButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .row();

        stage.addActor(root);
    }

    @Override
    protected void update(float dt) {
        stage.act(dt);
    }

    @Override
    protected void render() {
        stage.draw();
    }

    @Override
    protected void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    protected void dispose() {
        if (stage != null) {
            stage.dispose();
        }
    }
}
