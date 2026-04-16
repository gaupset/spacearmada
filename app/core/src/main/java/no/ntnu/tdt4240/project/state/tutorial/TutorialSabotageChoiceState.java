package no.ntnu.tdt4240.project.state.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.model.Sabotage;
import no.ntnu.tdt4240.project.state.State;
import no.ntnu.tdt4240.project.state.StateManager;
import no.ntnu.tdt4240.project.ui.SpaceButton;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.Theme;

public class TutorialSabotageChoiceState extends State {
    private static final float BUTTON_HEIGHT = 36f;
    private static final float BUTTON_GAP = 8f;

    private final TutorialSabotageState tutorialGameState;
    private Stage stage;
    private Texture overlayTex;
    private Label chargesLabel;

    public TutorialSabotageChoiceState(
        StateManager sm,
        com.badlogic.gdx.graphics.g2d.SpriteBatch batch,
        Assets assets,
        TutorialSabotageState tutorialGameState
    ) {
        super(sm, batch, assets);
        this.tutorialGameState = tutorialGameState;
    }

    @Override
    protected void setup() {
        stage = new Stage(new ExtendViewport(AppProperties.WIDTH, AppProperties.HEIGHT));

        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(new Color(0f, 0f, 0f, 0.6f));
        pixmap.fill();
        overlayTex = new Texture(pixmap);
        pixmap.dispose();

        buildLayout();
    }

    @Override
    protected void show() {
        InputMultiplexer gameMux = tutorialGameState.getInputMultiplexer();
        if (gameMux == null) {
            Gdx.input.setInputProcessor(stage);
            return;
        }
        InputMultiplexer combined = new InputMultiplexer();
        combined.addProcessor(stage);
        combined.addProcessor(gameMux);
        Gdx.input.setInputProcessor(combined);
    }

    private void buildLayout() {
        Skin skin = UiFactory.getInstance().getSkin();

        Table root = new Table();
        root.setFillParent(true);
        root.setTouchable(Touchable.childrenOnly);
        root.setBackground(new TextureRegionDrawable(overlayTex));
        root.top();

        Label titleLabel = new Label("Choose a sabotage", skin);
        titleLabel.setFontScale(1.1f);
        root.add(titleLabel).padTop(28f).row();

        chargesLabel = new Label(formatChargesLine(), skin);
        chargesLabel.setFontScale(0.85f);
        root.add(chargesLabel).padTop(6f).padBottom(8f).row();

        Table stack = new Table();
        addSabotageRow(stack, Sabotage.TYPE_ENEMY_SPEED, "2x enemy speed", true);
        addSabotageRow(stack, Sabotage.TYPE_HALF_PLAYER_BULLETS, "0.5x player bullets", true);
        addSabotageRow(stack, Sabotage.TYPE_DOUBLE_ALIENS, "2x number of aliens", false);
        root.add(stack).expand().center().width(AppProperties.WIDTH).row();

        stage.addActor(root);
    }

    private void addSabotageRow(Table parent, String sabotageType, String label, boolean gapBelow) {
        SpaceButton button = new SpaceButton(label);
        button.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                onSabotageChosen(sabotageType);
            }
        });
        parent.add(button)
            .width(AppProperties.WIDTH)
            .height(BUTTON_HEIGHT)
            .padBottom(gapBelow ? BUTTON_GAP : 0f)
            .row();
    }

    private void onSabotageChosen(String sabotageType) {
        if (tutorialGameState.getAvailableAbilityCount() <= 0) {
            return;
        }
        tutorialGameState.useSabotage();
        sm.set(new TutorialEndingState(sm, batch, assets));
    }

    private String formatChargesLine() {
        int n = tutorialGameState.getAvailableAbilityCount();
        return n == 1 ? "1 sabotage available" : n + " sabotages available";
    }

    @Override
    protected void update(float dt) {
        stage.act(dt);
    }

    @Override
    protected void render() {
        tutorialGameState.renderFrozen();
        stage.getViewport().apply();
        stage.draw();
    }

    @Override
    protected void resize(int width, int height) {
        if (stage != null) {
            stage.getViewport().update(width, height, true);
        }
    }

    @Override
    protected void dispose() {
        tutorialGameState.restoreDefaultInput();
        if (stage != null) {
            stage.dispose();
        }
        if (overlayTex != null) {
            overlayTex.dispose();
        }
    }
}
