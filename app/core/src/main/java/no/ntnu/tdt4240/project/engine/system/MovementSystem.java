package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.VelocityComponent;

public class MovementSystem extends IteratingSystem {
    private PositionComponent pos;
    private VelocityComponent vel;
    private ImmutableArray<Entity> sabotageEntities;

    public MovementSystem(int priority) {
        super(Family.all(
            PositionComponent.class,
            VelocityComponent.class,
            DimensionComponent.class
        ).get(), priority);
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        super.addedToEngine(engine);
        sabotageEntities = engine.getEntitiesFor(Family.all(SabotageEffectsComponent.class).get());
    }

    private void process(Entity e, float dt) {
        float speedMul = 1f;
        if (Mapper.enemy.has(e) && vel.y < 0f && sabotageEntities != null && sabotageEntities.size() > 0) {
            // Enemy sabotage affects enemies and enemy bullets moving downward.
            SabotageEffectsComponent effects = Mapper.sabotageEffects.get(sabotageEntities.first());
            speedMul = effects.enemySpeedBoostRemaining > 0f ? SabotageEffectsComponent.ENEMY_SPEED_MULTIPLIER : 1f;
        }
        pos.x += vel.x * dt;
        pos.y += vel.y * speedMul * dt;
    }

    @Override
    public void processEntity(Entity e, float dt) {
        pos = Mapper.position.get(e);
        vel = Mapper.velocity.get(e);
        process(e, dt);
    }
}
