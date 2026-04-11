package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class PowerupEffectsComponent implements Component {
    public static final float ENEMY_SPEED_SLOW_MULTIPLIER = 0.5f;
    public static final float PLAYER_SHOOT_INTERVAL_MULTIPLIER = 0.5f;

    public float shieldRemaining = 0f;
    public float rapidFireRemaining = 0f;
    public float slowEnemiesRemaining = 0f;
}
