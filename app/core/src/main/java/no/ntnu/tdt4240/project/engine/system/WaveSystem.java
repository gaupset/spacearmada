package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.config.WaveConfig;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.WaveComponent;

public class WaveSystem extends IteratingSystem {
    private static final float WAVE_TRANSITION_DELAY = 2f;

    public WaveSystem(int priority) {
        super(Family.all(WaveComponent.class).get(), priority);
    }

    // processEntity is called for each entity with a WaveComponent every frame. 
    // It manages the timing and configuration of enemy waves.
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        WaveComponent wave = Mapper.wave.get(entity);

        if (!wave.waveActive) {
            if (wave.waveTransitionTimer > 0f) {
                wave.waveTransitionTimer -= deltaTime;
                return;
            }
            configureWave(wave);
            wave.waveActive = true;
            return;
        }

        if (wave.enemiesToSpawn <= 0 && wave.enemiesAlive <= 0) {
            wave.waveNumber++;
            wave.waveActive = false;
            wave.waveTransitionTimer = WAVE_TRANSITION_DELAY;
        }
    }

    private void configureWave(WaveComponent wave) {
        WaveConfig config = WaveConfig.forWave(wave.waveNumber);
        wave.enemiesToSpawn = config.enemyCount;
        wave.spawnInterval = config.spawnInterval;
        wave.shooterChance = config.shooterChance;
        wave.enemySpeedMultiplier = config.speedMultiplier;
        wave.enemiesAlive = 0;
    }
}
