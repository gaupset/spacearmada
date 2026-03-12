package com.invaders99.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.invaders99.util.Theme;

public class UiFactory {
    private static UiFactory instance;

    private Skin skin;
    private BitmapFont font;
    private Theme theme;
    private int texCounter;

    private UiFactory(BitmapFont font) {
        this.font = font;
        this.theme = Theme.CLASSIC;
        buildSkin();
    }

    /** Initialize the singleton. Call once from Main.create(). */
    public static void init(BitmapFont font) {
        instance = new UiFactory(font);
    }

    public static UiFactory getInstance() {
        return instance;
    }

    public Skin getSkin() {
        return skin;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
        if (skin != null) skin.dispose();
        buildSkin();
    }

    private void buildSkin() {
        skin = new Skin();
        texCounter = 0;

        // Button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.up = createBorderedDrawable(
            theme.buttonIdle, theme.border,
            (int) Theme.BUTTON_WIDTH, (int) Theme.BUTTON_HEIGHT,
            (int) Theme.BORDER_THICKNESS
        );
        buttonStyle.over = createBorderedDrawable(
            theme.buttonHover, theme.primary,
            (int) Theme.BUTTON_WIDTH, (int) Theme.BUTTON_HEIGHT,
            (int) Theme.BORDER_THICKNESS
        );
        buttonStyle.down = createBorderedDrawable(
            theme.buttonPressed, theme.primary,
            (int) Theme.BUTTON_WIDTH, (int) Theme.BUTTON_HEIGHT,
            (int) Theme.BORDER_THICKNESS
        );
        buttonStyle.font = font;
        buttonStyle.fontColor = new Color(theme.primary);
        buttonStyle.overFontColor = new Color(1f, 0.2f, 0.6f, 1f);  // hot pink on hover
        buttonStyle.downFontColor = new Color(1f, 0.4f, 0.7f, 1f);  // lighter pink on press
        skin.add("default", buttonStyle);

        // TextField style
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = font;
        textFieldStyle.fontColor = new Color(theme.primary);
        textFieldStyle.background = createBorderedDrawable(
            theme.buttonIdle, theme.border,
            200, 40,
            (int) Theme.BORDER_THICKNESS
        );
        textFieldStyle.cursor = createSimpleDrawable(theme.primary, 2, 30);
        textFieldStyle.selection = createSimpleDrawable(new Color(theme.primary.r, theme.primary.g, theme.primary.b, 0.5f), 10, 30);
        skin.add("default", textFieldStyle);

        // Default label style
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = new Color(theme.primary);
        skin.add("default", labelStyle);

        // Secondary label style
        Label.LabelStyle secondaryLabelStyle = new Label.LabelStyle();
        secondaryLabelStyle.font = font;
        secondaryLabelStyle.fontColor = new Color(theme.textSecondary);
        skin.add("secondary", secondaryLabelStyle);

        // Slider style
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = createBorderedDrawable(
            theme.buttonIdle, theme.border,
            (int) Theme.BUTTON_WIDTH, 5,
            1
        );
        sliderStyle.knob = createBorderedDrawable(
            theme.primary, theme.primary,
            5, 10,
            0
        );
        skin.add("default-horizontal", sliderStyle);
    }

    private static final int SCALE = 4;

    private TextureRegionDrawable createBorderedDrawable(
        Color fillColor, Color borderColor,
        int width, int height, int borderThickness
    ) {
        int w = width * SCALE;
        int h = height * SCALE;
        int b = borderThickness * SCALE;
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        pixmap.setColor(borderColor);
        pixmap.fill();

        pixmap.setColor(fillColor);
        pixmap.fillRectangle(b, b, w - 2 * b, h - 2 * b);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        skin.add("tex-" + (texCounter++), texture);

        return new TextureRegionDrawable(texture);
    }

    private TextureRegionDrawable createSimpleDrawable(Color color, int width, int height) {
        Pixmap pixmap = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        pixmap.setColor(color);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        skin.add("tex-" + (texCounter++), texture);
        return new TextureRegionDrawable(texture);
    }

    public void dispose() {
        if (skin != null) skin.dispose();
    }
}
