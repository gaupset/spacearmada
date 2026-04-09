package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;

public class SabotageEffectSystem extends IteratingSystem {
    public SabotageEffectSystem(int priority) {
        super(Family.all(SabotageEffectsComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        SabotageEffectsComponent effects = Mapper.sabotageEffects.get(entity);
        effects.enemySpeedBoostRemaining = decToZero(effects.enemySpeedBoostRemaining, deltaTime);
        effects.playerFireRateSlowRemaining = decToZero(effects.playerFireRateSlowRemaining, deltaTime);
        effects.alienSpawnBoostRemaining = decToZero(effects.alienSpawnBoostRemaining, deltaTime);
    }

    private static float decToZero(float value, float dt) {
        if (value <= 0f) {
            return 0f;
        }
        value -= dt;
        return value <= 0f ? 0f : value;
    }
}
