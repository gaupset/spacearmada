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
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.data.NonPlayable;

public class ShootingSystem extends IteratingSystem {
    private final PlayerBullet playerBullet;
    private final EnemyBullet enemyBullet;
    private ImmutableArray<Entity> sabotageEntities;
    private ImmutableArray<Entity> powerupEntities;
    private ImmutableArray<Entity> players;

    public ShootingSystem(Assets assets, int priority) {
        super(Family.all(ShooterComponent.class).get(), priority);
        this.playerBullet = new PlayerBullet(assets.playerBullet, assets.playerBulletFrames);
        this.enemyBullet = new EnemyBullet(assets.enemyBullet, assets.enemyBulletFrames);
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        super.addedToEngine(engine);
        sabotageEntities = engine.getEntitiesFor(Family.all(SabotageEffectsComponent.class).get());
        powerupEntities = engine.getEntitiesFor(Family.all(PowerupEffectsComponent.class).get());
        players = engine.getEntitiesFor(Family.all(PlayerComponent.class).exclude(BulletComponent.class).get());
    }

    @Override
    protected void processEntity(Entity e, float dt) {
        PositionComponent pos = Mapper.position.get(e);
        ShooterComponent shooter = Mapper.shooter.get(e);
        boolean isPlayer = Mapper.player.has(e);

        // Disable enemy shooting in this branch: only process player shooters
        if (!isPlayer) {
            return;
        }

        float interval = shooter.baseInterval;
        if (sabotageEntities != null && sabotageEntities.size() > 0) {
            SabotageEffectsComponent effects = Mapper.sabotageEffects.get(sabotageEntities.first());
            if (effects.playerFireRateSlowRemaining > 0f) {
                interval *= SabotageEffectsComponent.PLAYER_SHOOT_INTERVAL_MULTIPLIER;
            }
        }
        if (powerupEntities != null && powerupEntities.size() > 0) {
            PowerupEffectsComponent pwr = Mapper.powerupEffects.get(powerupEntities.first());
            if (pwr.rapidFireRemaining > 0f) {
                interval *= PowerupEffectsComponent.PLAYER_SHOOT_INTERVAL_MULTIPLIER;
            }
        }

        shooter.timer += dt;
        if (shooter.timer < interval) {
            return;
        }
        shooter.timer -= interval;

        EntityAssembler assembler = new EntityAssembler(getEngine());
        Vector2 posData = new Vector2(pos.x, pos.y);
        Vector2 dimData = new Vector2(Mapper.dimension.get(e).width, Mapper.dimension.get(e).height);

        assembler.createPlayerBullet(playerBullet.create(posData, dimData));
    }
}
