package no.ntnu.tdt4240.project.config.bullet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.data.NonPlayable;

/**
 * The EnemyBullet class represents a configuration template for creating enemy bullet data
 * objects. All attributes controlling enemy bullet behavior are included here.
 */
public class EnemyBullet extends Bullet {
    private static final float ENEMY_BULLET_VEL = 300f;

    public EnemyBullet(TextureRegion tex) {
        super(tex);
    }

    /**
     * Creates an enemy bullet data object. Certain attributes of the data are calculated from
     * attributes of its origin object (object shooting the bullet).
     *
     * @param originPos Position of origin object
     * @param originDim Dimension of origin object
     * @return Enemy bullet data object
     */
    @Override
    public NonPlayable create(Vector2 originPos, Vector2 originDim) {
        Vector2 dim = new Vector2(BULLET_WIDTH, BULLET_HEIGHT);
        Vector2 vel = new Vector2(0f, -ENEMY_BULLET_VEL);
        Vector2 pos = calculatePosition(originPos, originDim, dim.y);
        return new NonPlayable(pos, vel, dim, tex);
    }

    /**
     * Calculates the initial enemy bullet position. The calculation is based on the position of
     * its origin object. The height of the bullet is also included in the calculation.
     *
     * @param originPos Position of origin object
     * @param originDim Dimension of origin object
     * @param height Enemy bullet height
     * @return Initial enemy bullet position
     */
    @Override
    protected Vector2 calculatePosition(Vector2 originPos, Vector2 originDim, float height) {
        return new Vector2(originPos.x, originPos.y - originDim.y * 0.5f + height * 0.5f);
    }
}
