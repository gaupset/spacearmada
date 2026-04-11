package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
    public TextureRegion region;

    public TextureComponent(TextureRegion region) {
        this.region = region;
    }
}
