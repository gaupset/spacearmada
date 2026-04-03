package no.ntnu.tdt4240.project.util;

import com.badlogic.gdx.graphics.Color;

public class Theme {

    public final Color background;
    public final Color primary;
    public final Color buttonIdle;
    public final Color buttonHover;
    public final Color buttonPressed;
    public final Color border;
    public final Color textSecondary;
    public final Color panelBackground;

    public Theme(Color background, Color primary, Color buttonIdle, Color buttonHover,
                 Color buttonPressed, Color border, Color textSecondary, Color panelBackground) {

        this.background = background;
        this.primary = primary;
        this.buttonIdle = buttonIdle;
        this.buttonHover = buttonHover;
        this.buttonPressed = buttonPressed;
        this.border = border;
        this.textSecondary = textSecondary;
        this.panelBackground = panelBackground;
    }

    // Button dimensions (shared by UiFactory skin builder)
    public static final float BUTTON_WIDTH = 280f;
    public static final float BUTTON_HEIGHT = 56f;
    public static final float BORDER_THICKNESS = 2f;

    // Font scales (relative to 32px base font)
    public static final float FONT_SCALE_BUTTON = 1.0f;
    public static final float FONT_SCALE_SMALL = 0.7f;

    public static final Theme CLASSIC = new Theme(
        new Color(0f, 0f, 0f, 1f),             // background — pure black
        new Color(0f, 1f, 1f, 1f),             // primary — neon cyan
        new Color(0.02f, 0.02f, 0.08f, 1f),   // buttonIdle — near-black
        new Color(0.05f, 0.15f, 0.2f, 1f),    // buttonHover — dark cyan tint
        new Color(0.1f, 0.25f, 0.35f, 1f),    // buttonPressed — brighter cyan
        new Color(0f, 0.9f, 1f, 1f),           // border — neon cyan outline
        new Color(0.7f, 0.5f, 1f, 1f),         // textSecondary — soft purple
        new Color(0.03f, 0.03f, 0.1f, 1f)      // panelBackground — dark blue
    );
}
