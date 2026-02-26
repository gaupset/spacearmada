package com.invaders99.ui;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.invaders99.util.Theme;

public class SpaceButton extends TextButton {

    public SpaceButton(String text) {
        super(text, UiFactory.getInstance().getSkin());
        getLabel().setFontScale(Theme.FONT_SCALE_BUTTON);
    }

    public SpaceButton(String text, String styleName) {
        super(text, UiFactory.getInstance().getSkin(), styleName);
        getLabel().setFontScale(Theme.FONT_SCALE_BUTTON);
    }
}
