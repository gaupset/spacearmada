package no.ntnu.tdt4240.project.data;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

/**
 * The Base class represents the initial base values for a new game data object.
 */
public class Base {
    public Vector2 pos, dim;
    public Array<TextureRegion> tex;

    public Base(Vector2 pos, Vector2 dim, Array<TextureRegion> tex) {
        this.pos = pos;
        this.dim = dim;
        this.tex = tex;
    }
}
