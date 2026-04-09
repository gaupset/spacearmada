package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    public TextureRegion texture;

    public TextureComponent(TextureRegion tex) {
        this.texture = tex;
    }
}
