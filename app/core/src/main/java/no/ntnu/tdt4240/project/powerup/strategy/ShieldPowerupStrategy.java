package no.ntnu.tdt4240.project.powerup.strategy;

import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;

public class ShieldPowerupStrategy implements PowerupStrategy {
    @Override
    public void apply(PowerupEffectsComponent effects, float durationSeconds) {
        effects.shieldRemaining += durationSeconds;
    }
}
