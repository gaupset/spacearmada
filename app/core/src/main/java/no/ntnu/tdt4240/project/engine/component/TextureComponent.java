package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureComponent implements Component {
    public Texture texture;

    public TextureComponent(Texture tex) {
        texture = tex;
    }
}
