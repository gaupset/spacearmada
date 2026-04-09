package no.ntnu.tdt4240.project.data;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * The NonPlayable class represents the initial values for a new non-playable game data object.
 */
public class NonPlayable extends Base {
    public Vector2 vel;

    public NonPlayable(Vector2 pos, Vector2 vel, Vector2 dim, TextureRegion tex) {
        super(pos, dim, tex);
        this.vel = vel;
    }
}
