package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.Vector2;
import no.ntnu.tdt4240.project.Assets;
import no.ntnu.tdt4240.project.config.bullet.PlayerBullet;
import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;

public class TutorialPlayerShootingSystem extends IteratingSystem {
    private final PlayerBullet playerBullet;
    private ImmutableArray<Entity> powerupEntities;

    public TutorialPlayerShootingSystem(Assets assets, int priority) {
        super(Family.all(ShooterComponent.class, PlayerComponent.class).exclude(BulletComponent.class).get(), priority);
        this.playerBullet = new PlayerBullet(assets.playerBullet, assets.playerBulletFrames);
    }

    @Override
    public void addedToEngine(com.badlogic.ashley.core.Engine engine) {
        super.addedToEngine(engine);
        powerupEntities = engine.getEntitiesFor(Family.all(PowerupEffectsComponent.class).get());
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        PositionComponent pos = Mapper.position.get(entity);
        ShooterComponent shooter = Mapper.shooter.get(entity);
        float interval = shooter.baseInterval;
        if (powerupEntities != null && powerupEntities.size() > 0) {
            PowerupEffectsComponent effects = Mapper.powerupEffects.get(powerupEntities.first());
            if (effects.rapidFireRemaining > 0f) {
                interval *= PowerupEffectsComponent.PLAYER_SHOOT_INTERVAL_MULTIPLIER;
            }
        }
        shooter.timer += deltaTime;
        if (shooter.timer < interval) {
            return;
        }
        shooter.timer -= interval;

        EntityAssembler assembler = new EntityAssembler(getEngine());
        Vector2 posData = new Vector2(pos.x, pos.y);
        Vector2 dimData = new Vector2(Mapper.dimension.get(entity).width, Mapper.dimension.get(entity).height);
        assembler.createPlayerBullet(playerBullet.create(posData, dimData));
    }
}
