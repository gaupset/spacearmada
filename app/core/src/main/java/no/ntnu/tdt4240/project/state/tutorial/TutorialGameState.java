package no.ntnu.tdt4240.project.state.tutorial;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.GameInputProcessor;
import no.ntnu.tdt4240.project.config.Player;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.ScoreComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.engine.system.BounceSystem;
import no.ntnu.tdt4240.project.engine.system.BoundSystem;
import no.ntnu.tdt4240.project.engine.system.CollisionSystem;
import no.ntnu.tdt4240.project.engine.system.EventSystem;
import no.ntnu.tdt4240.project.engine.system.InputSystem;
import no.ntnu.tdt4240.project.engine.system.MovementSystem;
import no.ntnu.tdt4240.project.engine.system.RemovalSystem;
import no.ntnu.tdt4240.project.engine.system.RenderSystem;
import no.ntnu.tdt4240.project.state.State;
import no.ntnu.tdt4240.project.state.StateManager;
import no.ntnu.tdt4240.project.ui.view.GameHud;

public class TutorialGameState extends State {
    private static final float MOVEMENT_STEP_DURATION_SECONDS = 5f;

    private final Engine engine;
    private GameHud hud;
    private InputMultiplexer inputMux;
    private float elapsedSeconds = 0f;
    private boolean canProgress = false;

    public TutorialGameState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
        this.engine = new Engine();
    }

    @Override
    protected void setup() {
        hud = new GameHud(() -> {
            if (canProgress) {
                sm.set(new TutorialCombatIntroState(sm, batch, assets));
            }
        }, null, null);

        GameInputProcessor input = new GameInputProcessor(hud.getStage().getViewport());

        Player player = new Player(assets.player, assets.playerFrames);
        EntityAssembler assembler = new EntityAssembler(engine);
        assembler.createPlayer(player.create());

        engine.addSystem(new InputSystem(input, 0));
        engine.addSystem(new MovementSystem(0));
        engine.addSystem(new BounceSystem(1));
        engine.addSystem(new BoundSystem(1));
        engine.addSystem(new CollisionSystem(assets, 2));
        engine.addSystem(new EventSystem(3));
        engine.addSystem(new RemovalSystem(4));
        engine.addSystem(new RenderSystem(batch, hud.getStage().getViewport(), 5));

        inputMux = new InputMultiplexer();
        inputMux.addProcessor(hud.getStage());
        inputMux.addProcessor(input);
        Gdx.input.setInputProcessor(inputMux);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void show() {
        if (inputMux != null) {
            Gdx.input.setInputProcessor(inputMux);
        }
    }

    @Override
    protected void update(float dt) {
        engine.update(dt);
        if (!canProgress) {
            elapsedSeconds += dt;
            if (elapsedSeconds >= MOVEMENT_STEP_DURATION_SECONDS) {
                canProgress = true;
            }
        }
    }

    @Override
    protected void render() {
        hud.actTutorial(
            Gdx.graphics.getDeltaTime(),
            getPlayerScore(),
            getPlayerHealth(),
            1,
            canProgress ? "Press NEXT to progress to the next step" : "",
            false,
            false,
            true
        );
        hud.draw();
    }

    private int getPlayerScore() {
        ImmutableArray<Entity> playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, ScoreComponent.class).get()
        );
        if (playerEntities.size() == 0) {
            return 0;
        }
        ScoreComponent score = Mapper.score.get(playerEntities.first());
        return score == null ? 0 : score.score;
    }

    private int getPlayerHealth() {
        ImmutableArray<Entity> playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, HealthComponent.class).get()
        );
        if (playerEntities.size() == 0) {
            return 0;
        }
        HealthComponent health = Mapper.health.get(playerEntities.first());
        return health == null ? 0 : health.health;
    }

    @Override
    protected void resize(int width, int height) {
        if (hud != null) {
            hud.resize(width, height);
        }
    }

    @Override
    protected void dispose() {
        if (hud != null) {
            hud.dispose();
        }
    }
}
