package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.HashMap;
import java.util.Map;

import no.ntnu.tdt4240.project.config.Player;
import no.ntnu.tdt4240.project.model.Sabotage;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.ScoreComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.system.BounceSystem;
import no.ntnu.tdt4240.project.engine.system.BoundSystem;
import no.ntnu.tdt4240.project.engine.system.CollisionSystem;
import no.ntnu.tdt4240.project.engine.system.EventSystem;
import no.ntnu.tdt4240.project.engine.system.InputSystem;
import no.ntnu.tdt4240.project.engine.system.MovementSystem;
import no.ntnu.tdt4240.project.engine.system.RemovalSystem;
import no.ntnu.tdt4240.project.engine.system.RenderSystem;
import no.ntnu.tdt4240.project.engine.system.SabotageEffectSystem;
import no.ntnu.tdt4240.project.engine.system.ShootingSystem;
import no.ntnu.tdt4240.project.engine.system.SpawnSystem;
import no.ntnu.tdt4240.project.event.Event;
import no.ntnu.tdt4240.project.event.EventListener;
import no.ntnu.tdt4240.project.GameInputProcessor;
import no.ntnu.tdt4240.project.layout.GameLayout;
import no.ntnu.tdt4240.project.layout.Layout;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.sabotage.strategy.DoubleAliensSabotageStrategy;
import no.ntnu.tdt4240.project.sabotage.strategy.EnemySpeedSabotageStrategy;
import no.ntnu.tdt4240.project.sabotage.strategy.HalfPlayerBulletsSabotageStrategy;
import no.ntnu.tdt4240.project.sabotage.strategy.SabotageStrategy;
import no.ntnu.tdt4240.project.ui.view.GameHud;

public class GameState extends State implements EventListener {
    // Pause button cooldown duration (prevents continuous pausing to cheat)
    private static final float PAUSE_BUTTON_COOLDOWN_SECONDS = 10f;
    private static final int SABOTAGE_SCORE_THRESHOLD = 10;

    private Engine engine;
    private Layout layout;
    private boolean gameOver;
    private GameHud hud;
    private boolean menuOpen = false;
    private boolean gameplayPaused = false;
    private boolean isSabotageVisible = false;
    private InputMultiplexer inputMux;
    private boolean exitPauseWhenMenuCloses = false;
    private boolean hasBeenSetup = false; // Track if setup has been called to prevent duplication
    private float pauseButtonCooldownRemaining = 0f; // Time remaining until pause button is enabled again
    private int sabotagesUsedCount = 0;
    private final Map<String, SabotageStrategy> sabotageStrategies = new HashMap<>();

    public GameState(StateManager sm, SpriteBatch batch, Engine engine, Assets assets) {
        super(sm, batch, assets);
        this.engine = engine;
        this.layout = new GameLayout();
        this.gameOver = false;
        sabotageStrategies.put(Sabotage.TYPE_ENEMY_SPEED, new EnemySpeedSabotageStrategy());
        sabotageStrategies.put(Sabotage.TYPE_HALF_PLAYER_BULLETS, new HalfPlayerBulletsSabotageStrategy());
        sabotageStrategies.put(Sabotage.TYPE_DOUBLE_ALIENS, new DoubleAliensSabotageStrategy());
    }

    @Override
    public void setup() {
        // Only run setup once to prevent player/system duplication when returning from pause
        if (hasBeenSetup) {
            return;
        }
        hasBeenSetup = true;

        // Input
        GameInputProcessor input = new GameInputProcessor();

        // Player
        Player player = new Player(assets.player);
        EntityAssembler assembler = new EntityAssembler(engine);
        assembler.createPlayer(player.create());
        Entity sabotageEffectsEntity = new Entity();
        sabotageEffectsEntity.add(new SabotageEffectsComponent());
        engine.addEntity(sabotageEffectsEntity);
        // Systems
        engine.addSystem(new InputSystem(input, 0));
        engine.addSystem(new MovementSystem(0));
        engine.addSystem(new BounceSystem(1));
        engine.addSystem(new BoundSystem(1));
        engine.addSystem(new CollisionSystem(2));
        engine.addSystem(eventSystem());
        engine.addSystem(new SpawnSystem(assets, 3, 4));
        engine.addSystem(new ShootingSystem(assets, 4));
        engine.addSystem(new SabotageEffectSystem(4));
        engine.addSystem(new RemovalSystem(5));
        engine.addSystem(new RenderSystem(batch, 6));

        hud = new GameHud(
            isOpen -> this.menuOpen = isOpen,
            () -> sm.set(new MenuState(sm, batch, assets)),
            () -> sm.push(new PauseState(sm, batch, assets, this)),
            () -> {
                if (getAvailableSabotageCount() > 0) {
                    sm.push(new SabotageState(sm, batch, assets, this));
                }
            },
            () -> {
                this.menuOpen = false;
                if (exitPauseWhenMenuCloses) {
                    exitPauseWhenMenuCloses = false;
                    sm.pop();
                    resumeInput();
                }
            }
        );

        InputMultiplexer inputMux = new InputMultiplexer();
        inputMux.addProcessor(hud.getStage());
        inputMux.addProcessor(input);
        Gdx.input.setInputProcessor(inputMux);

        // Store inputMux for later restoration
        this.inputMux = inputMux;

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    protected void show() {
        // Restore input processor when returning from pause
        // This ensures the HUD and game input work properly after pausing
        if (inputMux != null) {
            Gdx.input.setInputProcessor(inputMux);
        }
    }

    @Override
    protected void hide() {
        // Called when leaving this state (e.g., going to pause)
        // No cleanup needed here as we want to preserve state
    }

    private EventSystem eventSystem() {
        EventSystem sys = new EventSystem(3);
        sys.events.subscribe(this);
        return sys;
    }

    @Override
    public void receive(Event event) {
        if (event == Event.LOSE) {
            gameOver = true;
        }
    }

    /**
     * Gets the player's current score from the ECS engine
     * Searches for the player entity and retrieves its ScoreComponent
     * @return Current score value, or 0 if player entity not found
     */
    private int getPlayerScore() {
        // Get all entities with PlayerComponent and ScoreComponent
        ImmutableArray<Entity> playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, ScoreComponent.class).get()
        );

        // If player entity exists, return its score
        if (playerEntities.size() > 0) {
            Entity player = playerEntities.first();
            ScoreComponent scoreComp = Mapper.score.get(player);
            return scoreComp != null ? scoreComp.score : 0;
        }

        return 0;
    }

    /**
     * Gets the player's current health from the ECS engine
     * Searches for the player entity and retrieves its HealthComponent
     * @return Current health value, or 0 if player entity not found
     */
    private int getPlayerHealth() {
        // Get all entities with PlayerComponent and HealthComponent
        ImmutableArray<Entity> playerEntities = engine.getEntitiesFor(
            Family.all(PlayerComponent.class, HealthComponent.class).get()
        );

        // If player entity exists, return its health
        if (playerEntities.size() > 0) {
            Entity player = playerEntities.first();
            HealthComponent healthComp = Mapper.health.get(player);
            return healthComp != null ? healthComp.health : 0;
        }

        return 0;
    }

    Stage getHudStage() {
        return hud != null ? hud.getStage() : null;
    }

    void setExitPauseWhenMenuCloses(boolean exitPauseWhenMenuCloses) {
        this.exitPauseWhenMenuCloses = exitPauseWhenMenuCloses;
    }

    public boolean isMenuOpen() {
        return menuOpen;
    }

    public void setMenuOpen(boolean open) {
        this.menuOpen = open;
    }

    /**
     * Starts the pause button cooldown after returning from pause.
     * This prevents players from continuously pausing to gain an unfair advantage.
     */
    void startPauseButtonCooldown() {
        pauseButtonCooldownRemaining = PAUSE_BUTTON_COOLDOWN_SECONDS;
    }

    /**
     * Checks if the pause button is ready to be clicked (cooldown has expired).
     * @return true if pause button can be clicked, false if still on cooldown
     */
    private boolean isPauseButtonReady() {
        return pauseButtonCooldownRemaining <= 0f;
    }

    public void renderFrozen() {
        engine.update(0f);
        // Get current player stats for frozen render
        int score = getPlayerScore();
        int health = getPlayerHealth();
        isSabotageVisible = getAvailableSabotageCount() > 0;
        boolean pauseReady = isPauseButtonReady();
        hud.act(0f, false, isSabotageVisible, pauseReady, score, health);
        hud.draw();
    }

    public void resumeInput() {
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void update(float dt) {
        updateGameplay(dt);
    }

    public void updateGameplay(float dt) {
        if (gameplayPaused) {
            return;
        }

        boolean isRunning = !gameOver;

        for (EntitySystem system : engine.getSystems()) {
            if(!(system instanceof RenderSystem)) {
                boolean shouldProcess = isRunning;
                if (system instanceof SabotageEffectSystem && menuOpen) {
                    shouldProcess = false;
                }
                system.setProcessing(shouldProcess);
            }
        }
        engine.update(dt);

        // Update pause button cooldown timer while game is running
        if (isRunning && pauseButtonCooldownRemaining > 0f) {
            pauseButtonCooldownRemaining -= dt;
            if (pauseButtonCooldownRemaining <= 0f) {
                pauseButtonCooldownRemaining = 0f;
            }
        }

        // When game is over, transition to GameOverState
        if (gameOver) {
            // Get final score before cleaning up entities
            int finalScore = getPlayerScore();

            // Clean up all entities and systems
            engine.removeAllEntities();
            engine.removeAllSystems();

            // Transition to GameOverState with final score
            sm.set(new GameOverState(sm, batch, assets, engine, finalScore));
        }
    }

    @Override
    public void render() {
        // Background is now drawn in Main.render()
        // Get current player stats for HUD display
        int score = getPlayerScore();
        int health = getPlayerHealth();
        isSabotageVisible = getAvailableSabotageCount() > 0;
        boolean pauseReady = isPauseButtonReady();
        hud.act(Gdx.graphics.getDeltaTime(), menuOpen, isSabotageVisible, pauseReady, score, health);
        hud.draw();
    }

    public int getAvailableSabotageCount() {
        int earned = getPlayerScore() / SABOTAGE_SCORE_THRESHOLD;
        return Math.max(0, earned - sabotagesUsedCount);
    }

    public void recordSabotageUse() {
        sabotagesUsedCount += 1;
    }

    public InputMultiplexer getInputMultiplexer() {
        return inputMux;
    }

    public void setGameplayPaused(boolean gameplayPaused) {
        this.gameplayPaused = gameplayPaused;
    }

    public void restoreDefaultInput() {
        resumeInput();
    }

    public void applySabotage(String type, float durationSeconds) {
        SabotageEffectsComponent effects = getSabotageEffectsComponent();
        if (effects == null) {
            return;
        }
        SabotageStrategy strategy = sabotageStrategies.get(type);
        if (strategy == null) {
            strategy = sabotageStrategies.get(Sabotage.TYPE_ENEMY_SPEED);
        }
        strategy.apply(effects, durationSeconds);
    }

    private SabotageEffectsComponent getSabotageEffectsComponent() {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(SabotageEffectsComponent.class).get());
        if (entities.size() == 0) {
            return null;
        }
        return Mapper.sabotageEffects.get(entities.first());
    }

    @Override
    public void resize(int width, int height) {
        layout.resize(width, height);
    }

    @Override
    public void dispose() {
        // Assets are now managed by Main
    }
}
