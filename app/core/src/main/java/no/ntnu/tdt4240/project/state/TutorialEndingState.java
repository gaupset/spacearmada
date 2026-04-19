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
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.ui.view.GameHud;
import no.ntnu.tdt4240.project.util.Theme;

public class TutorialEndingState extends State {
    private static final float VIEWPORT_MIN_WIDTH = 360f;
    private static final float VIEWPORT_MIN_HEIGHT = 640f;

    private Stage stage;
    private Label messageLineOne;
    private Label messageLineTwo;
    private SpaceButton actionButton;
    private TextButton exitButton;
    private int step = 0;

    public TutorialEndingState(StateManager sm, SpriteBatch batch, Assets assets) {
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
        Table exitBar = new Table();
        exitBar.setFillParent(true);
        exitBar.top().right();
        exitButton = new TextButton("EXIT", UiFactory.getInstance().getSkin());
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

        messageLineOne = new Label("The selected sabotage is now applied to another player", style);
        messageLineOne.setFontScale(0.75f);
        messageLineOne.setWrap(true);
        messageLineOne.setAlignment(Align.center);
        root.add(messageLineOne).width(VIEWPORT_MIN_WIDTH - 48f).padBottom(16f).row();

        messageLineTwo = new Label("", style);
        messageLineTwo.setFontScale(0.72f);
        messageLineTwo.setWrap(true);
        messageLineTwo.setAlignment(Align.center);
        root.add(messageLineTwo).width(VIEWPORT_MIN_WIDTH - 48f).padBottom(24f).row();

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
        stage.addActor(exitBar);
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
            if (exitButton != null) {
                exitButton.setVisible(false);
            }
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
