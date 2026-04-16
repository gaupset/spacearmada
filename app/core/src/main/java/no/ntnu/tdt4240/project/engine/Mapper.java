package no.ntnu.tdt4240.project.engine;

import com.badlogic.ashley.core.ComponentMapper;

import no.ntnu.tdt4240.project.engine.component.AnimationComponent;
import no.ntnu.tdt4240.project.engine.component.BulletComponent;
import no.ntnu.tdt4240.project.engine.component.WaveComponent;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.EnemyComponent;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.ScoreComponent;
import no.ntnu.tdt4240.project.engine.component.PowerupEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.SabotageEffectsComponent;
import no.ntnu.tdt4240.project.engine.component.ShooterComponent;
import no.ntnu.tdt4240.project.engine.component.TextureComponent;
import no.ntnu.tdt4240.project.engine.component.TutorialScenarioComponent;
import no.ntnu.tdt4240.project.engine.component.VelocityComponent;

/**
 * The Mapper class represents a utility for fetching all components of a specific type. The class
 * contains a {@link ComponentMapper} for each component type, enabling systems to efficiently
 * perform operations on all instances of that type.
 */
public class Mapper {
    public static final ComponentMapper<PositionComponent> position = ComponentMapper.getFor(PositionComponent.class);
    public static final ComponentMapper<VelocityComponent> velocity = ComponentMapper.getFor(VelocityComponent.class);
    public static final ComponentMapper<DimensionComponent> dimension = ComponentMapper.getFor(DimensionComponent.class);
    public static final ComponentMapper<TextureComponent> texture = ComponentMapper.getFor(TextureComponent.class);
    public static final ComponentMapper<HealthComponent> health = ComponentMapper.getFor(HealthComponent.class);
    public static final ComponentMapper<ScoreComponent> score = ComponentMapper.getFor(ScoreComponent.class);
    public static final ComponentMapper<PlayerComponent> player = ComponentMapper.getFor(PlayerComponent.class);
    public static final ComponentMapper<EnemyComponent> enemy = ComponentMapper.getFor(EnemyComponent.class);
    public static final ComponentMapper<ShooterComponent> shooter = ComponentMapper.getFor(ShooterComponent.class);
    public static final ComponentMapper<BulletComponent> bullet = ComponentMapper.getFor(BulletComponent.class);
    public static final ComponentMapper<SabotageEffectsComponent> sabotageEffects = ComponentMapper.getFor(SabotageEffectsComponent.class);
    public static final ComponentMapper<PowerupEffectsComponent> powerupEffects = ComponentMapper.getFor(PowerupEffectsComponent.class);
    public static final ComponentMapper<AnimationComponent> animation = ComponentMapper.getFor(AnimationComponent.class);
    public static final ComponentMapper<WaveComponent> wave = ComponentMapper.getFor(WaveComponent.class);
    public static final ComponentMapper<TutorialScenarioComponent> tutorialScenario = ComponentMapper.getFor(TutorialScenarioComponent.class);
}
