package no.ntnu.tdt4240.project.config;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.data.Playable;

/**
 * The Player class represents a configuration template for creating player data objects. All
 * attributes controlling player behavior are included here.
 */
public class Player {
    private static final float PLAYER_MARGIN_BOTTOM = 40f;
    private static final float PLAYER_WIDTH = 32f;
    private static final float PLAYER_HEIGHT = 24f;
    private static final int HEALTH = 3;

    private final Array<TextureRegion> tex;

    public Player(Array<TextureRegion> tex) {
        this.tex = tex;
    }

    /**
     * Creates a player data object.
     *
     * @return Player data object
     */
    public Playable create() {
        Vector2 dim = new Vector2(PLAYER_WIDTH, PLAYER_HEIGHT);
        Vector2 pos = calculatePosition(dim.y);
        return new Playable(pos, dim, HEALTH, tex);
    }

    /**
     * Calculates the initial player position. The initial position should place the player at the
     * horizontal center of the screen.
     *
     * @param height Object height
     * @return Initial player position
     */
    private Vector2 calculatePosition(float height) {
        return new Vector2(AppProperties.WIDTH * 0.5f, PLAYER_MARGIN_BOTTOM + height * 0.5f);
    }
}
