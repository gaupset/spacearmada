package no.ntnu.tdt4240.project.data;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * The Base class represents the initial base values for a new game data object.
 */
public class Base {
    public Vector2 pos, dim;
    public TextureRegion tex;

    public Base(Vector2 pos, Vector2 dim, TextureRegion tex) {
        this.pos = pos;
        this.dim = dim;
        this.tex = tex;
    }
}
