package no.ntnu.tdt4240.project.data;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

/**
 * The Playable class represents the initial values for a new playable game data object.
 */
public class Playable extends Base {
    public int health;

    public Playable(Vector2 pos, Vector2 dim, int health, Texture tex) {
        super(pos, dim, tex);
        this.health = health;
    }
}
