package no.ntnu.tdt4240.project.engine.entity.config;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;

/**
 * The Movable class represents the initial values for a new {@link Entity} able to move on its
 * own. This data object is used by the {@link EntityAssembler} to package all values of movable
 * entities during entity creation.
 */
public class Movable extends Base {
    public Vector2 vel;

    public Movable(Vector2 pos, Vector2 vel, Vector2 dim, Texture tex) {
        super(pos, dim, tex);
        this.vel = vel;
    }
}
