package com.invaders99.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.invaders99.service.FirebaseService;
import com.invaders99.util.Theme;

public class FirebaseTestWidget extends Table {
    private static final Color STATUS_SUCCESS = new Color(0.2f, 1.0f, 0.4f, 1f);
    private static final Color STATUS_ERROR = new Color(1.0f, 0.3f, 0.3f, 1f);
    private static final Color STATUS_PENDING = new Color(1.0f, 1.0f, 0.3f, 1f);

    private final Label statusLabel;
    private final Label.LabelStyle statusStyle;

    public FirebaseTestWidget() {
        Skin skin = UiFactory.getInstance().getSkin();
        Theme theme = UiFactory.getInstance().getTheme();
        setSkin(skin);

        setBackground(createBackground(theme));

        // Title
        Label titleLabel = new Label("FIREBASE CONNECTION", skin);
        titleLabel.setFontScale(Theme.FONT_SCALE_SMALL);

        // Status
        statusStyle = new Label.LabelStyle(skin.get("secondary", Label.LabelStyle.class));
        statusLabel = new Label("Not tested", statusStyle);
        statusLabel.setFontScale(Theme.FONT_SCALE_SMALL);

        // Test button
        TextButton testButton = new TextButton("TEST", skin);
        testButton.getLabel().setFontScale(Theme.FONT_SCALE_SMALL);
        testButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                testConnection();
            }
        });

        // Layout
        pad(12f);
        add(titleLabel).center().row();
        add(statusLabel).center().padTop(6f).row();
        add(testButton).center().padTop(8f).width(100f).height(36f);
    }

    private void testConnection() {
        statusLabel.setText("Testing...");
        statusStyle.fontColor = STATUS_PENDING;

        FirebaseService.getInstance().testConnection(new FirebaseService.FirebaseCallback() {
            @Override
            public void onSuccess(String response) {
                statusLabel.setText(response);
                statusStyle.fontColor = STATUS_SUCCESS;
            }

            @Override
            public void onFailure(String error) {
                statusLabel.setText(error);
                statusStyle.fontColor = STATUS_ERROR;
            }
        });
    }

    public void autoTest() {
        testConnection();
    }

    private static final int SCALE = 4;

    private static TextureRegionDrawable createBackground(Theme theme) {
        int w = (int) Theme.BUTTON_WIDTH * SCALE;
        int h = 80 * SCALE;
        int b = SCALE; // 1px border at logical size
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);
        pixmap.setColor(theme.primary);
        pixmap.fill();
        pixmap.setColor(theme.panelBackground);
        pixmap.fillRectangle(b, b, w - 2 * b, h - 2 * b);
        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();
        return new TextureRegionDrawable(texture);
    }
}
