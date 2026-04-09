package no.ntnu.tdt4240.project;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class Assets {
    public TextureRegion player;
    public TextureRegion playerBullet;
    public TextureRegion enemy;
    public TextureRegion enemyBullet;
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
        // Load sprite sheets and extract first frame
        Texture playerSheet = new Texture("alien.png");
        playerSheet.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] playerFrames = TextureRegion.split(playerSheet, 32, 32);
        TextureRegion playerFrame1 = playerFrames[0][0];
        TextureRegion playerFrame2 = playerFrames.length > 1 ? playerFrames[1][0] : playerFrames[0][0];

        Texture bulletSheetTex = new Texture("bullet.png");
        bulletSheetTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] bulletFrames = TextureRegion.split(bulletSheetTex, 32, 32);

        Texture enemySheetTex = new Texture("enemy.png");
        enemySheetTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] enemyFrames = TextureRegion.split(enemySheetTex, 32, 32);

        Texture enemyBulletSheetTex = new Texture("enemy_bullet.png");
        enemyBulletSheetTex.setFilter(TextureFilter.Nearest, TextureFilter.Nearest);
        TextureRegion[][] enemyBulletFrames = TextureRegion.split(enemyBulletSheetTex, 32, 32);

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

    public Texture getStarsBackground() {
        return starsBackground;
    }

    public Texture getPlayer() {
        return player;
    }

    public Texture getPlayerBullet() {
        return playerBullet;
    }

    public Texture getEnemy() {
        return enemy;
    }

    public Texture getEnemyBullet() {
        return enemyBullet;
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
