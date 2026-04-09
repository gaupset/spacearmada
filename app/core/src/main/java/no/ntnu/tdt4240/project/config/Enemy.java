package no.ntnu.tdt4240.project.config;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.data.NonPlayable;

/**
 * The Enemy class represents a configuration template for creating enemy data objects. All
 * attributes controlling enemy behavior is included here.
 */
public class Enemy {
    private static final float ENEMY_WIDTH = 28f;
    private static final float ENEMY_HEIGHT = 20f;
    private static final float ENEMY_VEL = 120f;

    private final TextureRegion tex;

    private boolean spawnLeft;

    public Enemy(TextureRegion tex) {
        this.tex = tex;
        this.spawnLeft = true;
    }

    /**
     * Creates an enemy data object.
     *
     * @return Enemy data object
     */
    public NonPlayable create() {
        Vector2 dim = new Vector2(ENEMY_WIDTH, ENEMY_HEIGHT);
        Vector2 vel = new Vector2(0f, -ENEMY_VEL);
        Vector2 pos = calculatePosition(dim.x);
        return new NonPlayable(pos, vel, dim, tex);
    }

    /**
     * Calculates a random position alternating between the left and right hand side of the screen.
     * When determining the <code>x</code> coordinate, <code>marginLeft</code> and
     * <code>marginRight</code> are appended relative to the specified object width to make it not
     * appear in the corners of either side.
     *
     * @param width Specified object width
     * @return Random position
     */
    private Vector2 calculatePosition(float width) {
        float marginLeft = width * 0.5f;
        float marginRight = width * 1.5f;

        float x;
        if (spawnLeft) {
            x = MathUtils.random(marginLeft, AppProperties.WIDTH * 0.5f - marginRight);
        }
        else {
            x = MathUtils.random(AppProperties.WIDTH * 0.5f + marginLeft, AppProperties.WIDTH - marginRight);
        }
        spawnLeft = !spawnLeft;

        return new Vector2(x, AppProperties.HEIGHT);
    }
}
