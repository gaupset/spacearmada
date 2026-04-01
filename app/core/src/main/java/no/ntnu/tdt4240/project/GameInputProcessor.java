package no.ntnu.tdt4240.project;

import com.badlogic.gdx.InputAdapter;

public class GameInputProcessor extends InputAdapter {
    private boolean touched = false;
    private int x;
    private int y;

    public GameInputProcessor() {
        // Intentionally left blank
    }

    // Input handling

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        touched = true;
        x = screenX;
        y = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        touched = false;
        return true;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        x = screenX;
        y = screenY;
        return true;
    }

    // Getters

    public boolean isTouched() {
        return touched;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}
