package no.ntnu.tdt4240.project.config.bullet;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.data.NonPlayable;

/**
 * The PlayerBullet class represents a configuration template for creating player bullet data
 * objects. All attributes controlling player bullet behavior are included here.
 */
public class PlayerBullet extends Bullet {
    private static final float PLAYER_BULLET_VEL = 500f;

    public PlayerBullet(Texture tex) {
        super(tex);
    }

    /**
     * Creates a player bullet data object. Certain attributes of the data are calculated from
     * attributes of its origin object (object shooting the bullet).
     *
     * @param originPos Position of origin object
     * @param originDim Dimension of origin object
     * @return Player bullet data object
     */
    @Override
    public NonPlayable create(Vector2 originPos, Vector2 originDim) {
        Vector2 dim = new Vector2(BULLET_WIDTH, BULLET_HEIGHT);
        Vector2 vel = new Vector2(0f, PLAYER_BULLET_VEL);
        Vector2 pos = calculatePosition(originPos, originDim, dim.y);
        return new NonPlayable(pos, vel, dim, tex);
    }

    /**
     * Calculates the initial player bullet position. The calculation is based on the position of
     * its origin object. The height of the bullet is also included in the calculation.
     *
     * @param originPos Position of origin object
     * @param originDim Dimension of origin object
     * @param height Player bullet height
     * @return Initial player bullet position
     */
    @Override
    protected Vector2 calculatePosition(Vector2 originPos, Vector2 originDim, float height) {
        return new Vector2(originPos.x, originPos.y + originDim.y * 0.5f + height * 0.5f);
    }
}
