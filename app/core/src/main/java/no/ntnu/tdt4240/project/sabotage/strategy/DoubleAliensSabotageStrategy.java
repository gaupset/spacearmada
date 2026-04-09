package no.ntnu.tdt4240.project.sabotage.strategy;

import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;

public class DoubleAliensSabotageStrategy implements SabotageStrategy {
    @Override
    public void apply(SabotageEffectsComponent effects, float durationSeconds) {
        effects.alienSpawnBoostRemaining += durationSeconds;
    }
}
