package no.ntnu.tdt4240.project.sabotage.strategy;

import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;

public interface SabotageStrategy {
    void apply(SabotageEffectsComponent effects, float durationSeconds);
}
