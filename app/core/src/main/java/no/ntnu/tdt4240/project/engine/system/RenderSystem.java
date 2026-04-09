package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.RemoveComponent;
import no.ntnu.tdt4240.project.engine.component.TextureComponent;

public class RenderSystem extends IteratingSystem {
    private TextureComponent tex;
    private PositionComponent pos;
    private DimensionComponent dim;
    private SpriteBatch batch;

    public RenderSystem(SpriteBatch batch, int priority) {
        super(Family
            .all(TextureComponent.class)
            .exclude(RemoveComponent.class)
            .get(),
            priority
        );
        this.batch = batch;
    }

    @Override
    public void processEntity(Entity e, float dt) {
        tex = Mapper.texture.get(e);
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);
        process();
    }

    private void process() {
        batch.begin();
        batch.draw(
            tex.frames.get(tex.frame),
            centerHorizontal(),
            centerVertical(),
            dim.width,
            dim.height
        );
        batch.end();
    }

    /**
     * Calculates the <code>x</code> coordinate needed to render the entity horizontally centered
     * relative to its position. This is necessary as the {@link SpriteBatch} draws textures
     * starting from the bottom left corner.
     *
     * @return Calculated <code>x</code> coordinate for horizontal centering
     */
    private float centerHorizontal() {
        return pos.x - dim.width / 2f;
    }

    /**
     * Calculates the <code>y</code> coordinate needed to render the entity vertically centered
     * relative to its position. This is necessary as the {@link SpriteBatch} draws textures
     * starting from the bottom left corner.
     *
     * @return Calculated <code>y</code> coordinate for vertical centering
     */
    private float centerVertical() {
        return pos.y - dim.height / 2f;
    }
}
