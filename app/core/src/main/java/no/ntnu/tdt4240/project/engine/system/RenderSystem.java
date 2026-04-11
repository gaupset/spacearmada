package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.DimensionComponent;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.PositionComponent;
import no.ntnu.tdt4240.project.engine.component.RemoveComponent;
import no.ntnu.tdt4240.project.engine.component.TextureComponent;

public class RenderSystem extends IteratingSystem {
    private TextureComponent tex;
    private PositionComponent pos;
    private DimensionComponent dim;
    private SpriteBatch batch;
    private Viewport viewport;

    public RenderSystem(SpriteBatch batch, Viewport viewport, int priority) {
        super(Family
            .all(TextureComponent.class)
            .exclude(RemoveComponent.class)
            .get(),
            priority
        );
        this.batch = batch;
        this.viewport = viewport;
    }

    @Override
    public void update(float deltaTime) {
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        super.update(deltaTime);
        batch.end();
    }

    @Override
    public void processEntity(Entity e, float dt) {
        tex = Mapper.texture.get(e);
        pos = Mapper.position.get(e);
        dim = Mapper.dimension.get(e);

        boolean faded = false;
        if (Mapper.player.has(e)) {
            HealthComponent hp = Mapper.health.get(e);
            if (hp != null && hp.isInvincible()) {
                batch.setColor(1f, 1f, 1f, 0.4f);
                faded = true;
            }
        }

        batch.draw(
            tex.region,
            centerHorizontal(),
            centerVertical(),
            dim.width,
            dim.height
        );

        if (faded) {
            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    /**
     * Calculates the <code>x</code> coordinate needed to render the entity horizontally centered
     * relative to its position.
     *
     * @return Calculated <code>x</code> coordinate for horizontal centering
     */
    private float centerHorizontal() {
        return pos.x - dim.width / 2f;
    }

    /**
     * Calculates the <code>y</code> coordinate needed to render the entity vertically centered
     * relative to its position.
     *
     * @return Calculated <code>y</code> coordinate for vertical centering
     */
    private float centerVertical() {
        return pos.y - dim.height / 2f;
    }
}
