package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.config.bullet.EnemyBullet;
import no.ntnu.tdt4240.project.config.bullet.PlayerBullet;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.data.NonPlayable;
import no.ntnu.tdt4240.project.service.AudioService;

public class ShootingSystem extends IteratingSystem {
    private final PlayerBullet playerBullet;
    private final EnemyBullet enemyBullet;
    private final Assets assets;
    private ImmutableArray<Entity> sabotageEntities;

    private PositionComponent pos;
    private DimensionComponent dim;
    private ShooterComponent shooter;
    private boolean isPlayer;

    public ShootingSystem(Assets assets, int priority) {
        super(Family.all(
            ShooterComponent.class
        ).get(), priority);
        this.assets = assets;
        this.playerBullet = new PlayerBullet(assets.playerBullet);
        this.enemyBullet = new EnemyBullet(assets.enemyBullet);
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        super.addedToEngine(engine);
        sabotageEntities = engine.getEntitiesFor(Family.all(SabotageEffectsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity e, float dt) {
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);
        shooter = Mapper.shooter.get(e);
        isPlayer = Mapper.player.has(e);
        process(dt);
    }

    private void process(float dt) {
        float interval = shooter.baseInterval;
        if (isPlayer && sabotageEntities != null && sabotageEntities.size() > 0) {
            SabotageEffectsComponent effects = Mapper.sabotageEffects.get(sabotageEntities.first());
            if (effects.playerFireRateSlowRemaining > 0f) {
                interval *= SabotageEffectsComponent.PLAYER_SHOOT_INTERVAL_MULTIPLIER;
            }
        }
        shooter.timer += dt;
        if (shooter.timer < interval) {
            return;
        }
        shooter.timer -= interval;

        EntityAssembler assembler = new EntityAssembler(getEngine());
        // Create data transfer variables
        Vector2 posData = new Vector2(pos.x, pos.y);
        Vector2 dimData = new Vector2(dim.width, dim.height);
        // Create entity based on config
        NonPlayable config;
        if (isPlayer) {
            config = playerBullet.create(posData, dimData);
            assembler.createPlayerBullet(config);
            AudioService.getInstance().playSound(assets.getLaserSound());
        }
        else {
            config = enemyBullet.create(posData, dimData);
            assembler.createEnemyBullet(config);
        }
    }
}
