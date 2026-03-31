package no.ntnu.tdt4240.project.layout;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import no.ntnu.tdt4240.project.AppProperties;

public abstract class Layout {
    protected Stage stage;

    protected Layout() {
        stage = new Stage(new ExtendViewport(AppProperties.WIDTH, AppProperties.HEIGHT));
        build();
    }

    public Stage get() {
        return stage;
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    protected abstract void build();
}
