package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.Rectangle;

import java.util.function.BiConsumer;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.EnemyComponent;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.RemoveComponent;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.service.AudioService;

public class CollisionSystem extends EntitySystem {
    private final Assets assets;
    private ImmutableArray<Entity> players; // Only one player
    private ImmutableArray<Entity> playerBullets;
    private ImmutableArray<Entity> enemies;
    private ImmutableArray<Entity> enemyBullets;
    private ImmutableArray<Entity> powerupEntities;

    public CollisionSystem(Assets assets, int priority) {
        super(priority);
        this.assets = assets;
    }

    @Override
    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(Family
            .all(PlayerComponent.class)
            .exclude(BulletComponent.class)
            .get()
        );
        playerBullets = engine.getEntitiesFor(Family
            .all(PlayerComponent.class, BulletComponent.class)
            .get()
        );
        enemies = engine.getEntitiesFor(Family
            .all(EnemyComponent.class)
            .exclude(BulletComponent.class)
            .get()
        );
        enemyBullets = engine.getEntitiesFor(Family
            .all(EnemyComponent.class, BulletComponent.class)
            .get()
        );
        powerupEntities = engine.getEntitiesFor(Family.all(PowerupEffectsComponent.class).get());
    }

    @Override
    public void update(float dt) {
        for (Entity e : players) {
            HealthComponent hp = Mapper.health.get(e);
            if (hp != null && hp.invincibilityRemaining > 0f) {
                hp.invincibilityRemaining = Math.max(0f, hp.invincibilityRemaining - dt);
            }
            playerCollision(e, enemies);
            playerCollision(e, enemyBullets);
        }
        for (Entity e : enemies) {
            enemyCollision(e, playerBullets);
        }
        resetRectangle();
    }

    /**
     * Handles collisions on the specified player entity from the specified entity collection.
     *
     * @param e Specified player entity
     * @param entities Specified entity collection
     */
    private void playerCollision(Entity e, ImmutableArray<Entity> entities) {
        handleCollision(e, entities, (player, other) -> {
            if (powerupEntities != null && powerupEntities.size() > 0) {
                PowerupEffectsComponent pwr = Mapper.powerupEffects.get(powerupEntities.first());
                if (pwr.shieldRemaining > 0f) {
                    other.add(new RemoveComponent());
                    return;
                }
            }
            HealthComponent hp = Mapper.health.get(player);
            if (hp != null && hp.isInvincible()) {
                return;
            }
            for (int i = 0; i < players.size(); i++) {
                Entity p = players.get(i);
                HealthComponent h = Mapper.health.get(p);
                h.health--;
                h.invincibilityRemaining = HealthComponent.INVINCIBILITY_DURATION;
            }
            other.add(new RemoveComponent());
        });
    }

    /**
     * Handles collisions on the specified enemy entity from the specified entity collection.
     *
     * @param e Specified enemy entity
     * @param entities Specified entity collection
     */
    private void enemyCollision(Entity e, ImmutableArray<Entity> entities) {
        handleCollision(e, entities, (enemy, player) -> {
            for (int i = 0; i < players.size(); i++) {
                Entity p = players.get(i);
                Mapper.score.get(p).score++;
            }
            AudioService.getInstance().playSound(assets.getLaserSound());
            enemy.add(new RemoveComponent());
            player.add(new RemoveComponent());
        });
    }

    /**
     * Handles collisions on the specified entity from the specified entity collection. If
     * collision occurs, the specified operation is executed.
     *
     * @param e Specified entity
     * @param entities Specified
     * @param onCollision Specified operation
     */
    private void handleCollision(
        Entity e,
        ImmutableArray<Entity> entities,
        BiConsumer<Entity, Entity> onCollision)
    {
        setupRectangle2(e);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            setupRectangle1(entity);
            if (overlap()) {
                onCollision.accept(e, entity);
            }
        }
    }

    /**
     * Checks whether <code>tmp</code> overlaps <code>tmp2</code>.
     *
     * @return True if <code>tmp</code> overlaps <code>tmp2</code> or false otherwise
     * @see Rectangle#tmp
     * @see Rectangle#tmp2
     */
    private boolean overlap() {
        return Rectangle.tmp.overlaps(Rectangle.tmp2);
    }

    /**
     * Sets up {@link Rectangle#tmp} to use the parameters of the specified entity. This is an
     * alternative to instantiating a new {@link Rectangle}.
     *
     * @param e Specified entity
     */
    private void setupRectangle1(Entity e) {
        setupRectangle(e, Rectangle.tmp);
    }

    /**
     * Sets up {@link Rectangle#tmp2} to use the parameters of the specified entity. This is an
     * alternative to instantiating a new {@link Rectangle}.
     *
     * @param e Specified entity
     */
    private void setupRectangle2(Entity e) {
        setupRectangle(e, Rectangle.tmp2);
    }

    /**
     * Sets up the specified rectangle to use the parameters of the specified entity.
     *
     * @param e Specified entity
     * @param r Specified rectangle
     * @see Rectangle
     */
    private void setupRectangle(Entity e, Rectangle r) {
        PositionComponent pos = Mapper.position.get(e);
        DimensionComponent dim = Mapper.dimension.get(e);
        r.set(pos.x - dim.width / 2f, pos.y - dim.height / 2f, dim.width, dim.height);
    }

    /**
     * Resets all values in {@link Rectangle#tmp} and {@link Rectangle#tmp2} to zero.
     */
    private void resetRectangle() {
        Rectangle.tmp.set(0.0f, 0.0f, 0.0f, 0.0f);
        Rectangle.tmp2.set(0.0f, 0.0f, 0.0f, 0.0f);
    }
}
