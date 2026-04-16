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

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.util.Theme;

public class TutorialEndingState extends State {
    private Stage stage;
    private Label messageLineOne;
    private Label messageLineTwo;
    private SpaceButton actionButton;
    private int step = 0;

    public TutorialEndingState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(AppProperties.WIDTH, AppProperties.HEIGHT));
        Gdx.input.setInputProcessor(stage);
        buildLayout();
    }

    private void buildLayout() {
        Table root = new Table();
        root.setFillParent(true);
        root.center();

        Label.LabelStyle style = new Label.LabelStyle(assets.getDefaultFont(), Color.WHITE);

        messageLineOne = new Label("The selected sabotage is now applied to another player", style);
        messageLineOne.setFontScale(0.75f);
        messageLineOne.setWrap(true);
        messageLineOne.setAlignment(Align.center);
        root.add(messageLineOne).width(AppProperties.WIDTH - 48f).padBottom(16f).row();

        messageLineTwo = new Label("", style);
        messageLineTwo.setFontScale(0.72f);
        messageLineTwo.setWrap(true);
        messageLineTwo.setAlignment(Align.center);
        root.add(messageLineTwo).width(AppProperties.WIDTH - 48f).padBottom(24f).row();

        actionButton = new SpaceButton("CONTINUE");
        actionButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                advanceStep();
            }
        });
        root.add(actionButton)
            .width(Theme.BUTTON_WIDTH)
            .height(Theme.BUTTON_HEIGHT)
            .row();

        stage.addActor(root);
    }

    private void advanceStep() {
        if (step == 0) {
            step = 1;
            messageLineOne.setText("When you are hit by an enemy or enemy bullet, you lose a life");
            messageLineTwo.setText("You also lose a life when an enemy hits the bottom of the screen");
            return;
        }

        if (step == 1) {
            step = 2;
            messageLineOne.setText("Congratulations! You have finished the tutorial");
            messageLineTwo.setText("Click EXIT TUTORIAL to return to the main menu");
            actionButton.setText("EXIT TUTORIAL");
            return;
        }

        sm.set(new MenuState(sm, batch, assets));
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
