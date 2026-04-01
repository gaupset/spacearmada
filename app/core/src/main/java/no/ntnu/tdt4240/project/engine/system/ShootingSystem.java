package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.util.Assets;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.engine.entity.config.Movable;

public class ShootingSystem extends IntervalIteratingSystem {
    private static final float BULLET_WIDTH = 4f;
    private static final float BULLET_HEIGHT = 12f;
    private static final float PLAYER_BULLET_VEL = 500f;
    private static final float ENEMY_BULLET_VEL = 300f;

    private PositionComponent position;
    private DimensionComponent dimension;
    private Assets assets;
    private boolean isPlayer;

    public ShootingSystem(Assets assets, float interval, int priority) {
        super(Family.all(
            ShooterComponent.class
        ).get(), interval, priority);
        this.assets = assets;
    }

    @Override
    protected void processEntity(Entity e) {
        position = Mapper.position.get(e);
        dimension = Mapper.dimension.get(e);
        isPlayer = Mapper.player.has(e);
        process();
    }

    private void process() {
        EntityAssembler assembler = new EntityAssembler(getEngine());
        Movable config = createConfig();
        if (isPlayer) {
            assembler.createPlayerBullet(config);
        }
        else {
            assembler.createEnemyBullet(config);
        }
    }

    /**
     * Creates a new Moveable configuration. The different properties of the entity using this
     * configuration is set here.
     *
     * @return Movable configuration
     * @see Movable
     */
    private Movable createConfig() {
        Texture tex = createTexture();
        Vector2 dim = new Vector2(BULLET_WIDTH, BULLET_HEIGHT);
        Vector2 vel = createVelocity();
        Vector2 pos = createPosition(dim.x);
        return new Movable(pos, vel, dim, tex);
    }

    /**
     * Fetches an asset to be used in either the player or enemy bullet texture.
     *
     * @return Texture for player or enemy bullet
     * @see Texture
     */
    private Texture createTexture() {
        Texture tex;
        if (isPlayer) {
            tex = assets.playerBullet;
        }
        else {
            tex = assets.enemyBullet;
        }
        return tex;
    }

    /**
     * Creates a velocity vector for either the player or enemy bullet.
     *
     * @return Velocity vector for player or enemy bullet
     * @see Vector2
     */
    private Vector2 createVelocity() {
        Vector2 vel = new Vector2();
        if (isPlayer) {
            vel.set(0f, PLAYER_BULLET_VEL);
        }
        else {
            vel.set(0f, -ENEMY_BULLET_VEL);
        }
        return vel;
    }

    /**
     * Creates a position vector for either the player or enemy bullet. The method calculates the
     * center position of the origin entity using the <code>entityCenter</code> variable.
     * Furthermore, it adjusts the calculation using the specified width in the
     * <code>bulletCenter</code> variable.
     *
     * @param width The specified width
     * @return Position vector for player of enemy bullet
     * @see Vector2
     */
    private Vector2 createPosition(float width) {
        Vector2 pos = new Vector2();
        // Center positions
        float entityCenter = position.x;
        float bulletCenter = width * 0.5f;
        if (isPlayer) {
            pos.set(entityCenter - bulletCenter, position.y + dimension.height);
        }
        else {
            pos.set(entityCenter - bulletCenter, position.y);
        }
        return pos;
    }
}
