package no.ntnu.tdt4240.project.state;

import com.badlogic.ashley.core.Engine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

public class GameState extends State implements EventListener {
    private Engine engine;
    private Layout layout;
    private Assets assets;
    private boolean gameOver;

    public GameState(StateManager sm, SpriteBatch batch, Engine engine) {
        super(sm, batch);
        this.engine = engine;
        this.layout = new GameLayout();
        this.assets = new Assets();
        this.gameOver = false;
    }

    @Override
    public void setup() {
        // Input
        GameInputProcessor input = new GameInputProcessor();
        // Assets
        assets.load();
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
        // TODO Use InputMultiplexer with input system
        Gdx.input.setInputProcessor(input);
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

    @Override
    public void update(float dt) {
        engine.update(dt);
        if (gameOver) {
            engine.removeAllEntities();
            engine.removeAllSystems();

            sm.set(new GameState(sm, batch, engine));
        }
    }

    @Override
    public void render() {
        // layout.render()
    }

    @Override
    public void resize(int width, int height) {
        layout.resize(width, height);
    }

    @Override
    public void dispose() {
        assets.dispose();
    }
}
