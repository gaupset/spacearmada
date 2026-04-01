package no.ntnu.tdt4240.project.state;

import java.util.Stack;

public class StateManager {
    private Stack<State> states;

    public StateManager() {
        states = new Stack<>();
    }

    public void push(State state) {
        state.setup();
        states.push(state);
    }

    public void pop() {
        states.pop().dispose();
        states.peek().setup();
    }

    public void set(State state) {
        // Pop
        states.pop().dispose();
        // Push
        state.setup();
        states.push(state);
    }

    public void update(float dt) {
        states.peek().update(dt);
    }

    public void render() {
        states.peek().render();
    }

    public void resize(int width, int height) {
        states.peek().resize(width, height);
    }

    public void dispose() {
        while (!states.isEmpty()) states.pop().dispose();
    }
}
