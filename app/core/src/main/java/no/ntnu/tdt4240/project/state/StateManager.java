package no.ntnu.tdt4240.project.state;

import com.badlogic.gdx.Gdx;

import java.util.Stack;

/**
 * StateManager handles the state stack for the game.
 * It manages state transitions, lifecycle methods, and ensures proper cleanup.
 */
public class StateManager {
    private Stack<State> states;

    public StateManager() {
        states = new Stack<>();
    }

    /**
     * Pushes a new state onto the stack and makes it active.
     * Calls setup() once, then show() to activate the state.
     * @param state The state to push
     */
    public void push(State state) {
        // First-time initialization
        state.setup();
        state.show();
        state.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        states.push(state);
    }

    /**
     * Pops the current state from the stack and returns to the previous state.
     * Calls hide() and dispose() on the popped state, then show() on the previous state.
     */
    public void pop() {
        if (!states.isEmpty()) {
            State old = states.pop();
            old.hide();
            old.dispose();

            // Restore the previous state (if any)
            if (!states.isEmpty()) {
                State top = states.peek();
                top.show(); // Restore input and resume, but don't re-setup
                top.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            }
        }
    }

    /**
     * Replaces the current state with a new one.
     * Disposes the old state and sets up the new one.
     * @param state The new state to set
     */
    public void set(State state) {
        // Remove old state
        if (!states.isEmpty()) {
            State old = states.pop();
            old.hide();
            old.dispose();
        }
        // Add new state
        state.setup();
        state.show();
        state.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        states.push(state);
    }

    public void update(float dt) {
        if (!states.isEmpty()) {
            states.peek().update(dt);
        }
    }

    public void render() {
        if (!states.isEmpty()) {
            states.peek().render();
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
