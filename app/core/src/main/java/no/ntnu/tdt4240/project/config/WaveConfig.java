package no.ntnu.tdt4240.project.config;

public class WaveConfig {
    public final int enemyCount;
    public final float spawnInterval;
    public final float shooterChance;
    public final float speedMultiplier;

    public WaveConfig(int enemyCount, float spawnInterval, float shooterChance, float speedMultiplier) {
        this.enemyCount = enemyCount;
        this.spawnInterval = spawnInterval;
        this.shooterChance = shooterChance;
        this.speedMultiplier = speedMultiplier;
    }

    /**
     * Generates wave config for the given wave number.
     * Difficulty scales progressively with each wave.
     */
    public static WaveConfig forWave(int wave) {
        int enemyCount = 5 + wave * 2;
        float spawnInterval = Math.max(0.6f, 1.6f - wave * 0.2f);
        float shooterChance = Math.min(0.8f, 0.3f + wave * 0.05f);
        float speedMultiplier = 1f + (wave - 1) * 0.1f;
        return new WaveConfig(enemyCount, spawnInterval, shooterChance, speedMultiplier);
    }
}
