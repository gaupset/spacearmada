package no.ntnu.tdt4240.project.powerup.strategy;

import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;

public class RapidFirePowerupStrategy implements PowerupStrategy {
    @Override
    public void apply(PowerupEffectsComponent effects, float durationSeconds) {
        effects.rapidFireRemaining += durationSeconds;
    }
}
