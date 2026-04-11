package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.math.MathUtils;

import no.ntnu.tdt4240.project.config.Enemy;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.data.NonPlayable;

public class SpawnSystem extends EntitySystem {
    private final float baseInterval;
    private final Enemy enemy;
    private ImmutableArray<com.badlogic.ashley.core.Entity> sabotageEntities;
    private float spawnTimer;

    public SpawnSystem(Assets assets, float interval, int priority) {
        super(priority);
        this.baseInterval = interval;
        this.enemy = new Enemy(assets.enemy, assets.enemyFrames);
        this.spawnTimer = 0f;
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        super.addedToEngine(engine);
        sabotageEntities = engine.getEntitiesFor(Family.all(SabotageEffectsComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {
        float spawnMultiplier = 1f;
        if (sabotageEntities != null && sabotageEntities.size() > 0) {
            SabotageEffectsComponent effects = Mapper.sabotageEffects.get(sabotageEntities.first());
            if (effects.alienSpawnBoostRemaining > 0f) {
                spawnMultiplier = SabotageEffectsComponent.ENEMY_SPAWN_RATE_MULTIPLIER;
            }
        }
        float spawnEvery = baseInterval / spawnMultiplier;
        spawnTimer += deltaTime;
        if (spawnTimer < spawnEvery) {
            return;
        }
        spawnTimer -= spawnEvery;

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
