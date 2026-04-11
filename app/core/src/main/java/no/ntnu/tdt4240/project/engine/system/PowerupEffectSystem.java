package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;

public class PowerupEffectSystem extends IteratingSystem {
    public PowerupEffectSystem(int priority) {
        super(Family.all(PowerupEffectsComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PowerupEffectsComponent effects = Mapper.powerupEffects.get(entity);
        effects.shieldRemaining = decToZero(effects.shieldRemaining, deltaTime);
        effects.rapidFireRemaining = decToZero(effects.rapidFireRemaining, deltaTime);
        effects.slowEnemiesRemaining = decToZero(effects.slowEnemiesRemaining, deltaTime);
    }

    private static float decToZero(float value, float dt) {
        if (value <= 0f) {
            return 0f;
        }
        value -= dt;
        return value <= 0f ? 0f : value;
    }
}
