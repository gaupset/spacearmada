package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class WaveComponent implements Component {
    public long seed;
    public int waveNumber = 1;
    public int enemiesToSpawn;
    public int enemiesAlive;
    public float spawnInterval;
    public float shooterChance;
    public float enemySpeedMultiplier;
    public boolean waveActive = false;
    public float waveTransitionTimer = 0f;

    public WaveComponent(long seed) {
        this.seed = seed;
    }
}
