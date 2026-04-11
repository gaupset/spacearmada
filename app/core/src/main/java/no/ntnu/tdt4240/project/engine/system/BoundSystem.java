package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.EnemyComponent;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.RemoveComponent;
import no.ntnu.tdt4240.project.engine.component.WaveComponent;

public class BoundSystem extends EntitySystem {
    ImmutableArray<Entity> players; // Only one player
    ImmutableArray<Entity> enemies;
    ImmutableArray<Entity> bullets;
    ImmutableArray<Entity> waveEntities;

    private PositionComponent pos;
    private DimensionComponent dim;

    public BoundSystem(int priority) {
        super(priority);
    }

    @Override
    public void addedToEngine(Engine engine) {
        players = engine.getEntitiesFor(Family
            .all(PlayerComponent.class)
            .exclude(BulletComponent.class)
            .get()
        );
        enemies = engine.getEntitiesFor(Family
            .all(EnemyComponent.class)
            .exclude(BulletComponent.class)
            .get()
        );
        bullets = engine.getEntitiesFor(Family
            .all(BulletComponent.class)
            .get()
        );
        waveEntities = engine.getEntitiesFor(Family.all(WaveComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        for (Entity e : bullets) {
            setup(e);
            bulletOutOfBounds(e);
        }
        for (Entity e : enemies) {
            setup(e);
            enemyOutOfBounds(e);
        }
    }

    /**
     * Sets up attributes for the specified active entity.
     *
     * @param e The specified active entity
     */
    private void setup(Entity e) {
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);
    }

    /**
     * Out of bounds operations for bullets. Removes the specified entity if it is out of bounds.
     *
     * @param e The specified entity
     */
    private void bulletOutOfBounds(Entity e) {
        if (isOutOfBounds()) {
            e.add(new RemoveComponent());
        }
    }

    /**
     * Out of bounds operations for enemies. Decreases player health and removes the specified
     * entity if it is out of the bottom bound.
     *
     * @param e The specified entity
     */
    private void enemyOutOfBounds(Entity e) {
        if (isOutOfBottomBound()) {
            decrementPlayerHealth();
            if (waveEntities != null && waveEntities.size() > 0) {
                WaveComponent wave = Mapper.wave.get(waveEntities.first());
                wave.enemiesAlive = Math.max(0, wave.enemiesAlive - 1);
            }
            e.add(new RemoveComponent());
        }
    }

    /**
     * Checks whether the entity is out of bounds. An entity is out of bounds if it is out of the
     * top or bottom bound.
     *
     * @return True if entity is out of bounds or false otherwise
     */
    private boolean isOutOfBounds() {
        return isOutOfBottomBound() || isOutOfTopBound();
    }

    /**
     * Checks whether the entity is out of the top bound.
     *
     * @return True if entity is out of top bound or false otherwise
     */
    private boolean isOutOfTopBound() {
        return pos.y > AppProperties.HEIGHT;
    }

    /**
     * Checks whether the entity is out of the bottom bound.
     *
     * @return True if entity is out of bottom bound or false otherwise
     */
    private boolean isOutOfBottomBound() {
        return pos.y + dim.height < 0.0f;
    }

    /**
     * Decrements the health of each player, unless they are currently invincible.
     * Triggers invincibility on hit to prevent multiple simultaneous losses.
     */
    private void decrementPlayerHealth() {
        for (int i = 0; i < players.size(); i++) {
            Entity p = players.get(i);
            HealthComponent h = Mapper.health.get(p);
            if (h.isInvincible()) return;
            h.health--;
            h.invincibilityRemaining = HealthComponent.INVINCIBILITY_DURATION;
        }
    }
}
