package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
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
import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.ScoreComponent;
import no.ntnu.tdt4240.project.engine.component.TutorialScenarioComponent;
import no.ntnu.tdt4240.project.engine.component.WaveComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.engine.system.BounceSystem;
import no.ntnu.tdt4240.project.engine.system.BoundSystem;
import no.ntnu.tdt4240.project.engine.system.CollisionSystem;
import no.ntnu.tdt4240.project.engine.system.EventSystem;
import no.ntnu.tdt4240.project.engine.system.InputSystem;
import no.ntnu.tdt4240.project.engine.system.MovementSystem;
import no.ntnu.tdt4240.project.engine.system.PowerupEffectSystem;
import no.ntnu.tdt4240.project.engine.system.RemovalSystem;
import no.ntnu.tdt4240.project.engine.system.RenderSystem;
import no.ntnu.tdt4240.project.engine.system.SpawnSystem;
import no.ntnu.tdt4240.project.engine.system.TutorialPlayerShootingSystem;
import no.ntnu.tdt4240.project.engine.system.TutorialScenarioSystem;
import no.ntnu.tdt4240.project.engine.system.WaveSystem;
import no.ntnu.tdt4240.project.layout.GameLayout;
import no.ntnu.tdt4240.project.layout.Layout;
import no.ntnu.tdt4240.project.model.Powerup;
import no.ntnu.tdt4240.project.powerup.strategy.PowerupStrategy;
import no.ntnu.tdt4240.project.powerup.strategy.RapidFirePowerupStrategy;
import no.ntnu.tdt4240.project.powerup.strategy.ShieldPowerupStrategy;
import no.ntnu.tdt4240.project.powerup.strategy.SlowEnemiesPowerupStrategy;
import no.ntnu.tdt4240.project.ui.view.GameHud;

import java.util.HashMap;
import java.util.Map;

public class TutorialCombatState extends State {
    private static final int POWERUP_EFFECT_DURATION_SEC = 10;

    private final Engine engine;
    private final Layout layout;
    private final Map<String, PowerupStrategy> powerupStrategies = new HashMap<>();
    private GameHud hud;
    private InputMultiplexer inputMux;

    public TutorialCombatState(StateManager sm, SpriteBatch batch, Assets assets) {
        super(sm, batch, assets);
        this.engine = new Engine();
        this.layout = new GameLayout();
        powerupStrategies.put(Powerup.TYPE_SHIELD, new ShieldPowerupStrategy());
        powerupStrategies.put(Powerup.TYPE_RAPID_FIRE, new RapidFirePowerupStrategy());
        powerupStrategies.put(Powerup.TYPE_SLOW_ENEMIES, new SlowEnemiesPowerupStrategy());
    }

    @Override
    protected void setup() {
        GameInputProcessor input = new GameInputProcessor(layout.get().getViewport());
        EntityAssembler assembler = new EntityAssembler(engine);
        Player player = new Player(assets.player, assets.playerFrames);
        assembler.createPlayer(player.create());

        Entity sabotageEffectsEntity = new Entity();
        sabotageEffectsEntity.add(new SabotageEffectsComponent());
        engine.addEntity(sabotageEffectsEntity);
        Entity powerupEffectsEntity = new Entity();
        powerupEffectsEntity.add(new PowerupEffectsComponent());
        engine.addEntity(powerupEffectsEntity);
        Entity waveEntity = new Entity();
        waveEntity.add(new WaveComponent(System.currentTimeMillis()));
        engine.addEntity(waveEntity);
        Entity tutorialEntity = new Entity();
        TutorialScenarioComponent tutorial = new TutorialScenarioComponent();
        tutorial.mode = TutorialScenarioComponent.MODE_POWERUP;
        engine.addEntity(tutorialEntity.add(tutorial));

        engine.addSystem(new InputSystem(input, 0));
        engine.addSystem(new MovementSystem(0));
        engine.addSystem(new BounceSystem(1));
        engine.addSystem(new BoundSystem(1));
        engine.addSystem(new CollisionSystem(assets, 2));
        engine.addSystem(new EventSystem(3));
        engine.addSystem(new WaveSystem(3));
        engine.addSystem(new SpawnSystem(assets, 3, 4));
        engine.addSystem(new TutorialPlayerShootingSystem(assets, 4));
        engine.addSystem(new TutorialScenarioSystem(4));
        engine.addSystem(new PowerupEffectSystem(4));
        engine.addSystem(new RemovalSystem(5));
        engine.addSystem(new RenderSystem(batch, layout.get().getViewport(), 6));

        hud = new GameHud(
            () -> {
                if (isNextPromptVisible()) {
                    sm.set(new TutorialSabotageIntroState(sm, batch, assets));
                }
            },
            () -> {
                if (canOpenPowerup()) {
                    sm.push(new TutorialPowerupState(sm, batch, assets, this));
                }
            },
            null
        );

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
        if (shouldPauseTutorial()) {
            setTutorialSystemsPaused(true);
        }
        engine.update(dt);
    }

    @Override
    protected void render() {
        renderFrozen();
    }

    void renderFrozen() {
        boolean powerupVisible = canOpenPowerup();
        String prompt = "";
        if (powerupVisible) {
            prompt = "Click POWERUP";
        } else if (isNextPromptVisible()) {
            prompt = "Press NEXT to continue the tutorial";
        }
        hud.actTutorial(
            Gdx.graphics.getDeltaTime(),
            getPlayerScore(),
            getPlayerHealth(),
            getWaveNumber(),
            prompt,
            powerupVisible,
            false,
            isNextPromptVisible()
        );
        hud.draw();
    }

    private boolean canOpenPowerup() {
        TutorialScenarioComponent tutorial = getTutorialComponent();
        return tutorial != null && tutorial.pauseRequested && !tutorial.powerupChosen;
    }

    private void setTutorialSystemsPaused(boolean paused) {
        for (EntitySystem system : engine.getSystems()) {
            if (system instanceof RenderSystem) {
                continue;
            }
            system.setProcessing(!paused);
        }
    }

    int getAvailableAbilityCount() {
        return canOpenPowerup() ? 1 : 0;
    }

    void applyPowerup(String type) {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(PowerupEffectsComponent.class).get());
        if (entities.size() == 0) {
            return;
        }
        PowerupEffectsComponent effects = Mapper.powerupEffects.get(entities.first());
        PowerupStrategy strategy = powerupStrategies.get(type);
        if (strategy == null) {
            strategy = powerupStrategies.get(Powerup.TYPE_SHIELD);
        }
        strategy.apply(effects, POWERUP_EFFECT_DURATION_SEC);
        TutorialScenarioComponent tutorial = getTutorialComponent();
        if (tutorial != null) {
            tutorial.powerupChosen = true;
            tutorial.pauseRequested = false;
            tutorial.postSelectionElapsedSeconds = 0f;
        }
        setTutorialSystemsPaused(false);
    }

    private boolean shouldPauseTutorial() {
        TutorialScenarioComponent tutorial = getTutorialComponent();
        return tutorial != null && tutorial.pauseRequested;
    }

    private boolean isNextPromptVisible() {
        TutorialScenarioComponent tutorial = getTutorialComponent();
        return tutorial != null && tutorial.nextPromptVisible;
    }

    private void setPauseRequested(boolean pauseRequested) {
        TutorialScenarioComponent tutorial = getTutorialComponent();
        if (tutorial != null) {
            tutorial.pauseRequested = pauseRequested;
        }
    }

    private TutorialScenarioComponent getTutorialComponent() {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(TutorialScenarioComponent.class).get());
        if (entities.size() == 0) {
            return null;
        }
        return Mapper.tutorialScenario.get(entities.first());
    }

    public InputMultiplexer getInputMultiplexer() {
        return inputMux;
    }

    public void restoreDefaultInput() {
        if (inputMux != null) {
            Gdx.input.setInputProcessor(inputMux);
        }
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

    private int getWaveNumber() {
        ImmutableArray<Entity> entities = engine.getEntitiesFor(Family.all(WaveComponent.class).get());
        if (entities.size() == 0) {
            return 1;
        }
        WaveComponent wave = Mapper.wave.get(entities.first());
        return wave == null ? 1 : wave.waveNumber;
    }

    @Override
    protected void resize(int width, int height) {
        layout.resize(width, height);
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
