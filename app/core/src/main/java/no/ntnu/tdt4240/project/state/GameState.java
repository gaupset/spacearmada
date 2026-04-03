package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.ntnu.tdt4240.project.config.Player;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.engine.system.BounceSystem;
import no.ntnu.tdt4240.project.engine.system.BoundSystem;
import no.ntnu.tdt4240.project.engine.system.CollisionSystem;
import no.ntnu.tdt4240.project.engine.system.EventSystem;
import no.ntnu.tdt4240.project.engine.system.InputSystem;
import no.ntnu.tdt4240.project.engine.system.MovementSystem;
import no.ntnu.tdt4240.project.engine.system.RemovalSystem;
import no.ntnu.tdt4240.project.engine.system.RenderSystem;
import no.ntnu.tdt4240.project.engine.system.ShootingSystem;
import no.ntnu.tdt4240.project.engine.system.SpawnSystem;
import no.ntnu.tdt4240.project.event.Event;
import no.ntnu.tdt4240.project.event.EventListener;
import no.ntnu.tdt4240.project.GameInputProcessor;
import no.ntnu.tdt4240.project.layout.GameLayout;
import no.ntnu.tdt4240.project.layout.Layout;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.ui.view.GameHud;
import no.ntnu.tdt4240.project.ui.UiFactory;

public class GameState extends State implements EventListener {
    private Engine engine;
    private Layout layout;
    private boolean gameOver;
    private GameHud hud;
    private boolean menuOpen = false;
    private boolean gameplayPaused = false;
    private boolean isSabotageVisible = false;
    private InputMultiplexer inputMux;
    private boolean exitPauseWhenMenuCloses = false;

    public GameState(StateManager sm, SpriteBatch batch, Engine engine, Assets assets) {
        super(sm, batch, assets);
        this.engine = engine;
        this.layout = new GameLayout();
        this.gameOver = false;
    }

    @Override
    public void setup() {
        // Input
        GameInputProcessor input = new GameInputProcessor();

        // Player
        Player player = new Player(assets.player);
        EntityAssembler assembler = new EntityAssembler(engine);
        assembler.createPlayer(player.create());
        // Systems
        engine.addSystem(new InputSystem(input, 0));
        engine.addSystem(new MovementSystem(0));
        engine.addSystem(new BounceSystem(1));
        engine.addSystem(new BoundSystem(1));
        engine.addSystem(new CollisionSystem(2));
        engine.addSystem(eventSystem());
        engine.addSystem(new SpawnSystem(assets, 3, 4));
        engine.addSystem(new ShootingSystem(assets, 1, 4));
        engine.addSystem(new RemovalSystem(5));
        engine.addSystem(new RenderSystem(batch, 6));

        hud = new GameHud(
            isOpen -> this.menuOpen = isOpen,
            () -> sm.set(new MenuState(sm, batch, assets)),
            () -> sm.push(new PauseState(sm, batch, assets, this)),
            () -> System.out.println("Sabotage clicked!"),
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

        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

    public void renderFrozen() {
        engine.update(0f);
        hud.act(0f, false, isSabotageVisible, true);
        hud.draw();
    }

    public void resumeInput() {
        Gdx.input.setInputProcessor(inputMux);
    }

    @Override
    public void update(float dt) {
        boolean isRunning = !gameOver;

        for (EntitySystem system : engine.getSystems()) {
            if(!(system instanceof RenderSystem)) {
                system.setProcessing(isRunning);
            }
        }
        engine.update(dt);

        if (gameOver) {
            engine.removeAllEntities();
            engine.removeAllSystems();

            sm.set(new GameState(sm, batch, engine, assets));
        }
    }

    @Override
    public void render() {
        // Background is now drawn in Main.render()
        hud.act(Gdx.graphics.getDeltaTime(), menuOpen, isSabotageVisible, true);
        hud.draw();
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
