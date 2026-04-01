package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.RemoveComponent;

public class BoundSystem extends IteratingSystem {
    private PositionComponent pos;
    private DimensionComponent dim;

    public BoundSystem(int priority) {
        super(Family.all(
            PositionComponent.class,
            DimensionComponent.class
        ).get(), priority);
    }

    @Override
    protected void processEntity(Entity e, float dt) {
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);
        process(e);
    }

    private void process(Entity e) {
        if (isOutOfBounds()) {
            e.add(new RemoveComponent());
        }
    }

    /**
     * Checks whether the entity is out of bounds. An entity is out of bounds if it has passed the
     * top or bottom of the screen.
     *
     * @return True if entity is out of bounds or false otherwise
     */
    private boolean isOutOfBounds() {
        return pos.y + dim.height < 0.0f || pos.y > AppProperties.HEIGHT;
    }
}
