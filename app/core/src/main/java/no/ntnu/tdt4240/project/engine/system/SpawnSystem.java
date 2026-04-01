package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.systems.IntervalSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.util.Assets;
import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;
import no.ntnu.tdt4240.project.engine.entity.config.Movable;

public class SpawnSystem extends IntervalSystem {
    private static final float ENEMY_WIDTH = 28f;
    private static final float ENEMY_HEIGHT = 20f;
    private static final float ENEMY_VEL = 120f;

    private Assets assets;
    private boolean spawnLeft;

    public SpawnSystem(Assets assets, float interval, int priority) {
        super(interval, priority);
        this.assets = assets;
        this.spawnLeft = true;
    }

    @Override
    protected void updateInterval() {
        EntityAssembler assembler = new EntityAssembler(getEngine());
        Movable config = createConfig();
        // Randomly spawn enemy as shooter
        if (MathUtils.randomBoolean()) {
            assembler.createEnemy(config);
        }
        else {
            assembler.createEnemyShooter(config);
        }
    }

    /**
     * Creates a new Moveable configuration. The different properties of the entity using this
     * configuration is set here.
     *
     * @return Movable configuration
     * @see Movable
     */
    private Movable createConfig() {
        Vector2 dim = new Vector2(ENEMY_WIDTH, ENEMY_HEIGHT);
        Vector2 vel = new Vector2(0f, -ENEMY_VEL);
        Vector2 pos = createPosition(dim.x);
        return new Movable(pos, vel, dim, assets.enemy);
    }

    /**
     * Creates a random position alternating between the left and right hand side of the screen.
     * When determining the <code>x</code> coordinate, <code>marginLeft</code> and
     * <code>marginRight</code> are appended relative to the specified entity width to make it not
     * appear in the corners of either side.
     *
     * @param width Specified entity width
     * @return Random position
     */
    private Vector2 createPosition(float width) {
        float marginLeft = width * 0.5f;
        float marginRight = width * 1.5f;

        float x;
        if (spawnLeft) {
            x = MathUtils.random(0f + marginLeft, AppProperties.WIDTH / 2f - marginRight);
        }
        else {
            x = MathUtils.random(AppProperties.WIDTH / 2f + marginLeft, AppProperties.WIDTH - marginRight);
        }
        spawnLeft = !spawnLeft;

        return new Vector2(x, AppProperties.HEIGHT);
    }
}
