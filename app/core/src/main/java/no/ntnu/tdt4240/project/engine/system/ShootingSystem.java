package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.config.bullet.EnemyBullet;
import no.ntnu.tdt4240.project.config.bullet.PlayerBullet;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.data.NonPlayable;

public class ShootingSystem extends IntervalIteratingSystem {
    private final PlayerBullet playerBullet;
    private final EnemyBullet enemyBullet;

    private PositionComponent pos;
    private DimensionComponent dim;
    private boolean isPlayer;

    public ShootingSystem(Assets assets, float interval, int priority) {
        super(Family.all(
            ShooterComponent.class
        ).get(), interval, priority);
        this.playerBullet = new PlayerBullet(assets.playerBullet);
        this.enemyBullet = new EnemyBullet(assets.enemyBullet);
    }

    @Override
    protected void processEntity(Entity e) {
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);
        isPlayer = Mapper.player.has(e);
        process();
    }

    private void process() {
        EntityAssembler assembler = new EntityAssembler(getEngine());
        // Create data transfer variables
        Vector2 posData = new Vector2(pos.x, pos.y);
        Vector2 dimData = new Vector2(dim.width, dim.height);
        // Create entity based on config
        NonPlayable config;
        if (isPlayer) {
            config = playerBullet.create(posData, dimData);
            assembler.createPlayerBullet(config);
        }
        else {
            config = enemyBullet.create(posData, dimData);
            assembler.createEnemyBullet(config);
        }
    }
}
