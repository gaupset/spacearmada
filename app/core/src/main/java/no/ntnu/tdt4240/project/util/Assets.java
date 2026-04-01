package no.ntnu.tdt4240.project.util;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class Assets {
    public static final Color PLAYER_COLOR = new Color(0f, 1f, 1f, 1f);
    public static final Color PLAYER_BULLET_COLOR = new Color(1f, 1f, 0.4f, 1f);
    public static final Color ENEMY_COLOR = new Color(1f, 0.2f, 0.5f, 1f);
    public static final Color ENEMY_BULLET_COLOR = new Color(1f, 0.4f, 0.1f, 1f);

    public Texture player;
    public Texture playerBullet;
    public Texture enemy;
    public Texture enemyBullet;

    public Assets() {
        // Intentionally left blank
    }

    /**
     * Loads and initializes all textures as solid colors using {@link Pixmap}.
     */
    public void load() {
        Pixmap pix = new Pixmap(1, 1, Pixmap.Format.RGBA8888);

        // Player
        pix.setColor(PLAYER_COLOR);
        pix.fill();
        player = new Texture(pix);

        // Player bullet
        pix.setColor(PLAYER_BULLET_COLOR);
        pix.fill();
        playerBullet = new Texture(pix);

        // Enemy
        pix.setColor(ENEMY_COLOR);
        pix.fill();
        enemy = new Texture(pix);

        // Enemy bullet
        pix.setColor(ENEMY_BULLET_COLOR);
        pix.fill();
        enemyBullet = new Texture(pix);

        pix.dispose();
    }

    /**
     * Disposes all textures.
     */
    public void dispose() {
        player.dispose();
        playerBullet.dispose();
        enemy.dispose();
        enemyBullet.dispose();
    }
}
