package no.ntnu.tdt4240.project;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameInputProcessor extends InputAdapter {
    private boolean touched = false;
    private float x;
    private float y;
    private final Viewport viewport;
    private final Vector2 tmpVec = new Vector2();

    public GameInputProcessor(Viewport viewport) {
        this.viewport = viewport;
    }

    // Converts screen coordinates to world coordinates using the viewport's unproject method
    private void unproject(int screenX, int screenY) {
        tmpVec.set(screenX, screenY);
        viewport.unproject(tmpVec);
        x = tmpVec.x;
        y = tmpVec.y;
    }

    // Input handling

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touched = true;
        unproject(screenX, screenY);
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touched = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        unproject(screenX, screenY);
        return true;
    }

    // Getters

    public boolean isTouched() {
        return touched;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
