package com.invaders99.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.invaders99.view.state.State;

import java.util.Stack;

public class GameStateManager {
    private final Stack<State> states = new Stack<>();

    public void push(State state) {
        state.show();
        state.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        states.push(state);
    }

    public void pop() {
        if (!states.isEmpty()) {
            State old = states.pop();
            old.hide();
            old.dispose();
            if (!states.isEmpty()) {
                State top = states.peek();
                top.show();
                top.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }
    }

    public void set(State state) {
        if (!states.isEmpty()) {
            State old = states.pop();
            old.hide();
            old.dispose();
        }
        state.show();
        state.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        states.push(state);
    }

    public void update(float dt) {
        if (!states.isEmpty()) {
            states.peek().update(dt);
        }
    }

    public void render(SpriteBatch batch) {
        if (!states.isEmpty()) {
            states.peek().render(batch);
        }
    }

    public void resize(int width, int height) {
        if (!states.isEmpty()) {
            states.peek().resize(width, height);
        }
    }

    public void dispose() {
        while (!states.isEmpty()) {
            State state = states.pop();
            state.hide();
            state.dispose();
        }
    }
}
