package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class SabotageEffectsComponent implements Component {
    public static final float ENEMY_SPEED_MULTIPLIER = 2f;
    public static final float PLAYER_SHOOT_INTERVAL_MULTIPLIER = 2f;
    public static final float ENEMY_SPAWN_RATE_MULTIPLIER = 2f;

    public float enemySpeedBoostRemaining = 0f;
    public float playerFireRateSlowRemaining = 0f;
    public float alienSpawnBoostRemaining = 0f;
}
