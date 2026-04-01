package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.VelocityComponent;

public class MovementSystem extends IteratingSystem {
    private PositionComponent pos;
    private VelocityComponent vel;

    public MovementSystem(int priority) {
        super(Family.all(
            PositionComponent.class,
            VelocityComponent.class,
            DimensionComponent.class
        ).get(), priority);
    }

    @Override
    public void processEntity(Entity e, float dt) {
        pos = Mapper.position.get(e);
        vel = Mapper.velocity.get(e);
        process(dt);
    }

    private void process(float dt) {
        pos.x += vel.x * dt;
        pos.y += vel.y * dt;
    }
}
