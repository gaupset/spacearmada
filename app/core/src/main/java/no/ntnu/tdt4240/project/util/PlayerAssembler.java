package no.ntnu.tdt4240.project.util;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;

import no.ntnu.tdt4240.project.AppProperties;
import no.ntnu.tdt4240.project.engine.entity.config.Player;

public class PlayerAssembler {
    private static final float PLAYER_WIDTH = 32f;
    private static final float PLAYER_HEIGHT = 24f;
    private static final int HEALTH = 3;

    public static Player create(Texture tex) {
        Vector2 dim = new Vector2(PLAYER_WIDTH, PLAYER_HEIGHT);
        Vector2 pos = new Vector2(AppProperties.WIDTH * 0.5f - PLAYER_WIDTH * 0.5f, 40f);
        return new Player(pos, dim, HEALTH, tex);
    }
}
