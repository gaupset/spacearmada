package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.math.MathUtils;

import no.ntnu.tdt4240.project.config.Enemy;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.data.NonPlayable;

public class SpawnSystem extends IntervalSystem {
    private final Enemy enemy;

    public SpawnSystem(Assets assets, float interval, int priority) {
        super(interval, priority);
        this.enemy = new Enemy(assets.enemy);
    }

    @Override
    protected void updateInterval() {
        EntityAssembler assembler = new EntityAssembler(getEngine());
        NonPlayable config = enemy.create();
        // Randomly spawn enemy as shooter
        if (MathUtils.randomBoolean()) {
            assembler.createEnemy(config);
        }
        else {
            assembler.createEnemyShooter(config);
        }
    }
}
