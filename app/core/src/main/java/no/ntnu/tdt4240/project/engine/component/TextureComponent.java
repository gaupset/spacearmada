package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class TextureComponent implements Component {
    public Array<TextureRegion> frames;
    public int frame;

    public TextureComponent(Array<TextureRegion> frames) {
        this.frames = frames;
        this.frame = 0;
    }
}
