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
import no.ntnu.tdt4240.project.engine.component.VelocityComponent;

public class BounceSystem extends EntitySystem {
    private ImmutableArray<Entity> players; // Only one player
    private ImmutableArray<Entity> enemies;
    private PositionComponent pos;
    private VelocityComponent vel;
    private DimensionComponent dim;

    public BounceSystem(int priority) {
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
    }

    @Override
    public void update(float deltaTime) {
        for (Entity e : players) {
            playerBounce(e);
        }
        for (Entity e : enemies) {
            enemyBounce(e);
        }
    }

    /**
     * Bounces the specified player entity off of either horizontal bound.
     *
     * @param e Specified player entity
     */
    private void playerBounce(Entity e) {
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);
        bounce(() -> {});
    }

    /**
     * Bounces the specified enemy entity off of either horizontal bound.
     *
     * @param e Specified enemy entity
     */
    private void enemyBounce(Entity e) {
        pos = Mapper.position.get(e);
        vel = Mapper.velocity.get(e);
        dim = Mapper.dimension.get(e);
        bounce(() -> vel.x = -vel.x);
    }

    /**
     * Corrects the current entity if it has passed either horizontal bound and runs the specified
     * operation in each case.
     *
     * @param onBounce Specified operation
     */
    private void bounce(Runnable onBounce) {
        if (isLeftOutOfBounds()) {
            pos.x = dim.width / 2f;
            onBounce.run();
        }
        else if (isRightOutOfBounds()) {
            pos.x = AppProperties.WIDTH - dim.width / 2f;
            onBounce.run();
        }
    }

    /**
     * Checks whether the current entity has passed the left bound.
     *
     * @return True if current entity has passed left bound or false otherwise
     */
    private boolean isLeftOutOfBounds() {
        return pos.x - dim.width / 2f < 0.0f;
    }

    /**
     * Checks whether the current entity has passed the right bound.
     *
     * @return True if current entity has passed right bound or false otherwise
     */
    private boolean isRightOutOfBounds() {
        return pos.x + dim.width / 2f > AppProperties.WIDTH;
    }
}
