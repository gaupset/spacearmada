package no.ntnu.tdt4240.project.engine.entity.config;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.engine.entity.EntityAssembler;

/**
 * The Base class represents the initial base values for a new {@link Entity}. This data object
 * is used by the {@link EntityAssembler} to package all base values necessary during entity
 * creation.
 */
public class Base {
    public Vector2 pos, dim;
    public Texture tex;

    public Base(Vector2 pos, Vector2 dim, Texture tex) {
        this.pos = pos;
        this.dim = dim;
        this.tex = tex;
    }
}
