package no.ntnu.tdt4240.project.powerup.strategy;

import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;

public interface PowerupStrategy {
    void apply(PowerupEffectsComponent effects, float durationSeconds);
}
