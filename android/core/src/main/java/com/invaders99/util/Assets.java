package com.invaders99.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

/** Centralized asset loading and disposal. */
public class Assets {
    private Texture starsBackground;
    private Texture logoCrop;
    private BitmapFont defaultFont;
    private FreeTypeFontGenerator fontGenerator;

    public void load() {
        // Stars background
        starsBackground = new Texture("stars2.jpg");
        starsBackground.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Logo
        logoCrop = new Texture("invaders99-logo-crop.png");
        logoCrop.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        // Font
        fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("Roboto-Bold.ttf"));
        FreeTypeFontParameter param = new FreeTypeFontParameter();
        param.size = 32;
        param.minFilter = Texture.TextureFilter.Linear;
        param.magFilter = Texture.TextureFilter.Linear;
        defaultFont = fontGenerator.generateFont(param);
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

    public void dispose() {
        if (starsBackground != null) starsBackground.dispose();
        if (logoCrop != null) logoCrop.dispose();
        if (defaultFont != null) defaultFont.dispose();
        if (fontGenerator != null) fontGenerator.dispose();
    }
}
