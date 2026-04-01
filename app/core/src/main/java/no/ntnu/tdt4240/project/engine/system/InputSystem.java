package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.GameInputProcessor;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;

public class InputSystem extends IteratingSystem {
    private PositionComponent pos;
    private GameInputProcessor input;

    public InputSystem(GameInputProcessor input, int priority) {
        super(Family
            .all(PlayerComponent.class)
            .exclude(BulletComponent.class)
            .get(),
            priority
        );
        this.input = input;
    }
    @Override
    protected void processEntity(Entity e, float dt) {
        pos = Mapper.position.get(e);
        process();
    }

    private void process() {
        if (input.isTouched()) {
            pos.x = input.getX();
        }
    }
}
