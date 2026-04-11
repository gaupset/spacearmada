package no.ntnu.tdt4240.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Assets {
    public static final Color PLAYER_COLOR = new Color(0f, 1f, 1f, 1f);
    public static final Color PLAYER_BULLET_COLOR = new Color(1f, 1f, 0.4f, 1f);
    public static final Color ENEMY_COLOR = new Color(1f, 0.2f, 0.5f, 1f);
    public static final Color ENEMY_BULLET_COLOR = new Color(1f, 0.4f, 0.1f, 1f);

    public Texture player;
    public Texture playerBullet;
    public Texture enemy;
    public Texture enemyBullet;
    private Texture starsBackground;
    private Texture logoCrop;
    private BitmapFont defaultFont;
    private FreeTypeFontGenerator fontGenerator;

    private Sound laserSound;

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

        starsBackground = new Texture("ui/stars2.jpg");
        starsBackground.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        logoCrop = new Texture("ui/invaders99-logo-crop.png");
        logoCrop.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/Roboto-Bold.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = 32;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        defaultFont = fontGenerator.generateFont(param);

        laserSound = Gdx.audio.newSound(Gdx.files.internal("audio/laser_sound.mp3"));
    }

    public Texture getStarsBackground() {
        return starsBackground;
    }

    public Texture getLogoCrop() {
        return logoCrop;
    }

    public BitmapFont getDefaultFont() {
        return defaultFont;
    }

    public Sound getLaserSound() {
        return laserSound;
    }

    public static Texture createColorTexture(Color color) {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture tex = new Texture(pixmap);
        pixmap.dispose();
        return tex;
    }

    /**
     * Disposes all textures.
     */
    public void dispose() {
        // ECS
        if (player != null) player.dispose();
        if (playerBullet != null) playerBullet.dispose();
        if (enemy != null) enemy.dispose();
        if (enemyBullet != null) enemyBullet.dispose();

        // UI and sound
        if (starsBackground != null) starsBackground.dispose();
        if (logoCrop != null) logoCrop.dispose();
        if (defaultFont != null) defaultFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        if (laserSound != null) laserSound.dispose();
    }
}
