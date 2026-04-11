package no.ntnu.tdt4240.project.config.bullet;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.data.NonPlayable;

/**
 * The Bullet class represents a configuration template for creating bullet data objects. All
 * attributes controlling general bullet behavior are included here.
 */
public abstract class Bullet {
    protected static final float BULLET_WIDTH = 32f;
    protected static final float BULLET_HEIGHT = 32f;

    protected final TextureRegion tex;
    protected final TextureRegion[] frames;

    protected Bullet(TextureRegion tex, TextureRegion[] frames) {
        this.tex = tex;
        this.frames = frames;
    }

    protected abstract NonPlayable create(
        Vector2 originPos,
        Vector2 originDim
    );
    protected abstract Vector2 calculatePosition(
        Vector2 originPos,
        Vector2 originDim,
        float height
    );
}
