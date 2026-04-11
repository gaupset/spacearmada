package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimationComponent implements Component {
    public TextureRegion[] frames;
    public float frameDuration;
    public float stateTime = 0f;

    public AnimationComponent(TextureRegion[] frames, float frameDuration) {
        this.frames = frames;
        this.frameDuration = frameDuration;
    }
}
