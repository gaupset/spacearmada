package no.ntnu.tdt4240.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Assets {
    public TextureRegion player;
    public TextureRegion playerBullet;
    public TextureRegion enemy;
    public TextureRegion enemyBullet;
    public TextureRegion[] playerFrames;
    public TextureRegion[] playerBulletFrames;
    public TextureRegion[] enemyFrames;
    public TextureRegion[] enemyBulletFrames;
    private Texture playerSheet;
    private Texture playerBulletSheet;
    private Texture enemySheet;
    private Texture enemyBulletSheet;
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
        // Sprites — load sprite sheets and extract 32x32 frames
        playerSheet = new Texture("sprites/alien.png");
        playerSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerFrames = flattenFrames(TextureRegion.split(playerSheet, 32, 32));
        player = playerFrames[0];

        playerBulletSheet = new Texture("sprites/bullet.png");
        playerBulletSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        playerBulletFrames = flattenFrames(TextureRegion.split(playerBulletSheet, 32, 32));
        playerBullet = playerBulletFrames[0];

        enemySheet = new Texture("sprites/enemy.png");
        enemySheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyFrames = flattenFrames(TextureRegion.split(enemySheet, 32, 32));
        enemy = enemyFrames[0];

        enemyBulletSheet = new Texture("sprites/enemy_bullet.png");
        enemyBulletSheet.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        enemyBulletFrames = flattenFrames(TextureRegion.split(enemyBulletSheet, 32, 32));
        enemyBullet = enemyBulletFrames[0];

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

    private static TextureRegion[] flattenFrames(TextureRegion[][] grid) {
        int count = 0;
        for (TextureRegion[] row : grid) count += row.length;
        TextureRegion[] flat = new TextureRegion[count];
        int i = 0;
        for (TextureRegion[] row : grid) {
            for (TextureRegion region : row) {
                flat[i++] = region;
            }
        }
        return flat;
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
        // Sprite sheets
        if (playerSheet != null) playerSheet.dispose();
        if (playerBulletSheet != null) playerBulletSheet.dispose();
        if (enemySheet != null) enemySheet.dispose();
        if (enemyBulletSheet != null) enemyBulletSheet.dispose();

        // UI and sound
        if (starsBackground != null) starsBackground.dispose();
        if (logoCrop != null) logoCrop.dispose();
        if (defaultFont != null) defaultFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        if (laserSound != null) laserSound.dispose();
    }
}
