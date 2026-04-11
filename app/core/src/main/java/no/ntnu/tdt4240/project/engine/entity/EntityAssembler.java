package no.ntnu.tdt4240.project.engine.entity;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;

import no.ntnu.tdt4240.project.engine.component.AnimationComponent;
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.EnemyComponent;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.ScoreComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.component.TextureComponent;
import no.ntnu.tdt4240.project.engine.component.VelocityComponent;
import no.ntnu.tdt4240.project.data.Base;
import no.ntnu.tdt4240.project.data.NonPlayable;
import no.ntnu.tdt4240.project.data.Playable;

/**
 * The EntityAssembler class represents a utility for creating entities. Each <code>create()</code>
 * method in this class assembles the components necessary to make up a specific entity. See the
 * documentation provided for each method to know exactly what components are included.
 *
 * <p>When an entity is assembled, it is automatically added to the {@link Engine engine} (remember
 * to remove it when not used anymore).</p>
 */
public class EntityAssembler {
    private static final float PLAYER_SHOOT_BASE_INTERVAL = 1f;
    private static final float ENEMY_SHOOT_BASE_INTERVAL = 1f;
    private static final float ANIMATION_FRAME_DURATION = 0.5f;
    private Engine engine;

    public EntityAssembler(Engine engine) {
        this.engine = engine;
    }

    /**
     * Creates the base entity containing components all entities share. All other
     * <code>create()</code> methods call this method to get the base set of components.
     *
     * <p>Components included:</p>
     *
     * <ul>
     *     <li>{@link PositionComponent}</li>
     *     <li>{@link DimensionComponent}</li>
     *     <li>{@link TextureComponent}</li>
     * </ul>
     *
     * @param data Data object containing all initial entity values
     * @return Entity containing all base components
     */
    public Entity create(Base data) {
        Entity e = new Entity();
        // Components
        e.add(new PositionComponent(data.pos.x, data.pos.y));
        e.add(new DimensionComponent(data.dim.x, data.dim.y));
        e.add(new TextureComponent(data.tex));
        if (data.frames != null && data.frames.length > 1) {
            e.add(new AnimationComponent(data.frames, ANIMATION_FRAME_DURATION));
        }
        return e;
    }

    /**
     * Creates the player entity.
     *
     * <p>Components included:</p>
     *
     * <ul>
     *     <li>{@link PositionComponent}</li>
     *     <li>{@link DimensionComponent}</li>
     *     <li>{@link HealthComponent}</li>
     *     <li>{@link ScoreComponent}</li>
     *     <li>{@link TextureComponent}</li>
     *     <li>{@link PlayerComponent}</li>
     *     <li>{@link ShooterComponent}</li>
     * </ul>
     *
     * @param data Data object containing all initial entity values
     */
    public void createPlayer(Playable data) {
        Entity e = create(data);
        // Components
        e.add(new HealthComponent(data.health));
        e.add(new ScoreComponent());
        e.add(new PlayerComponent());
        e.add(new ShooterComponent(PLAYER_SHOOT_BASE_INTERVAL));
        // Add to engine
        engine.addEntity(e);
    }

    /**
     * Creates the player bullet entity.
     *
     * <p>Components included:</p>
     *
     * <ul>
     *     <li>{@link PositionComponent}</li>
     *     <li>{@link VelocityComponent}</li>
     *     <li>{@link DimensionComponent}</li>
     *     <li>{@link TextureComponent}</li>
     *     <li>{@link PlayerComponent}</li>
     *     <li>{@link BulletComponent}</li>
     * </ul>
     *
     * @param data Data object containing all initial entity values
     */
    public void createPlayerBullet(NonPlayable data) {
        Entity e = create(data);
        // Components
        e.add(new VelocityComponent(data.vel.x, data.vel.y));
        e.add(new PlayerComponent());
        e.add(new BulletComponent());
        // Add to engine
        engine.addEntity(e);
    }

    /**
     * Creates the enemy entity.
     *
     * <p>Components included:</p>
     *
     * <ul>
     *     <li>{@link PositionComponent}</li>
     *     <li>{@link VelocityComponent}</li>
     *     <li>{@link DimensionComponent}</li>
     *     <li>{@link TextureComponent}</li>
     *     <li>{@link EnemyComponent}</li>
     * </ul>
     *
     * @param data Data object containing all initial entity values
     */
    public void createEnemy(NonPlayable data) {
        Entity e = create(data);
        // Components
        e.add(new VelocityComponent(data.vel.x, data.vel.y));
        e.add(new EnemyComponent());
        // Add to engine
        engine.addEntity(e);
    }

    /**
     * Creates the enemy shooter entity.
     *
     * <p>Components included:</p>
     *
     * <ul>
     *     <li>{@link PositionComponent}</li>
     *     <li>{@link VelocityComponent}</li>
     *     <li>{@link DimensionComponent}</li>
     *     <li>{@link TextureComponent}</li>
     *     <li>{@link EnemyComponent}</li>
     *     <li>{@link ShooterComponent}</li>
     * </ul>
     *
     * @param data Data object containing all initial entity values
     */
    public void createEnemyShooter(NonPlayable data) {
        Entity e = create(data);
        // Components
        e.add(new VelocityComponent(data.vel.x, data.vel.y));
        e.add(new EnemyComponent());
        e.add(new ShooterComponent(ENEMY_SHOOT_BASE_INTERVAL));
        // Add to engine
        engine.addEntity(e);
    }

    /**
     * Creates the enemy bullet entity.
     *
     * <p>Components included:</p>
     *
     * <ul>
     *     <li>{@link PositionComponent}</li>
     *     <li>{@link VelocityComponent}</li>
     *     <li>{@link DimensionComponent}</li>
     *     <li>{@link TextureComponent}</li>
     *     <li>{@link EnemyComponent}</li>
     *     <li>{@link BulletComponent}</li>
     * </ul>
     *
     * @param data Data object containing all initial entity values
     */
    public void createEnemyBullet(NonPlayable data) {
        Entity e = create(data);
        // Components
        e.add(new VelocityComponent(data.vel.x, data.vel.y));
        e.add(new EnemyComponent());
        e.add(new BulletComponent());
        // Add to engine
        engine.addEntity(e);
    }
}
