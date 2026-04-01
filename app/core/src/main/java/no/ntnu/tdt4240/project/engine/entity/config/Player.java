package no.ntnu.tdt4240.project.engine.entity.config;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;

/**
 * The Player class represents the initial player values for a new {@link Entity}. This data object
 * is used by the {@link EntityAssembler} to package all player values necessary during entity
 * creation.
 */
public class Player extends Base {
    public int health;

    public Player(Vector2 pos, Vector2 dim, int health, Texture tex) {
        super(pos, dim, tex);
        this.health = health;
    }
}
