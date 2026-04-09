package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.TextureComponent;

public class AnimationSystem extends IntervalIteratingSystem {

    private TextureComponent tex;

    public AnimationSystem(float interval, int priority) {
        super(Family.all(TextureComponent.class).get(), interval, priority);

    }

    @Override
    protected void processEntity(Entity entity) {
        tex = Mapper.texture.get(entity);
        tex.frame++;
        if (tex.frame >= tex.frames.size) {
            tex.frame = 0;
        }
    }
}
