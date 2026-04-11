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
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.RemoveComponent;

public class BoundSystem extends EntitySystem {
    ImmutableArray<Entity> players; // Only one player
    ImmutableArray<Entity> enemies;
    ImmutableArray<Entity> bullets;

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
     * Decrements the health of each player.
     */
    private void decrementPlayerHealth() {
        for (int i = 0; i < players.size(); i++) {
            Entity p = players.get(i);
            Mapper.health.get(p).health--;
        }
    }
}
