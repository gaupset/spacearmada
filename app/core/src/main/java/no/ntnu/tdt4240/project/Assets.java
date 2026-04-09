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
import com.badlogic.gdx.utils.Array;

public class Assets {

    public Array<TextureRegion> player;
    public Array<TextureRegion> playerBullet;
    public Array<TextureRegion> enemy;
    public Array<TextureRegion> enemyBullet;
    private Texture playerTex;
    private Texture playerBulletTex;
    private Texture enemyTex;
    private Texture enemyBulletTex;
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
        playerTex = new Texture("player.png");
        player = getFrames(playerTex, 2);

        playerBulletTex = new Texture("bullet.png");
        playerBullet = getFrames(playerBulletTex, 2);

        enemyTex = new Texture("enemy.png");
        enemy = getFrames(enemyTex, 4);

        enemyBulletTex = new Texture("enemy_bullet.png");
        enemyBullet = getFrames(enemyBulletTex, 2);

        starsBackground = new Texture("stars2.jpg");
        starsBackground.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        logoCrop = new Texture("invaders99-logo-crop.png");
        logoCrop.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Bold.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = 32;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        defaultFont = fontGenerator.generateFont(param);

        laserSound = Gdx.audio.newSound(Gdx.files.internal("laser_sound.mp3"));
    }

    private Array<TextureRegion> getFrames(Texture frame, int count) {
        Array<TextureRegion> frames = new Array<TextureRegion>();
        int frameWidth = frame.getWidth()/count;
        for (int i = 0; i < count; i++) {
            TextureRegion reg = new TextureRegion(
                frame,
                i*frameWidth,
                0,
                frameWidth,
                frame.getHeight()
            );

            frames.add(reg);
        }
        return frames;

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
        if (playerTex != null) playerTex.dispose();
        if (playerBulletTex != null) playerBulletTex.dispose();
        if (enemyTex != null) enemyTex.dispose();
        if (enemyBulletTex != null) enemyBulletTex.dispose();

        // UI and sound
        if (starsBackground != null) starsBackground.dispose();
        if (logoCrop != null) logoCrop.dispose();
        if (defaultFont != null) defaultFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
        if (laserSound != null) laserSound.dispose();
    }
}
