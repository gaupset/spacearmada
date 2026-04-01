package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.engine.component.RemoveComponent;

public class RemovalSystem extends IteratingSystem {

    public RemovalSystem(int priority) {
        super(Family.all(
            RemoveComponent.class
        ).get(), priority);
    }

    @Override
    protected void processEntity(Entity e, float dt) {
        getEngine().removeEntity(e);
    }
}
