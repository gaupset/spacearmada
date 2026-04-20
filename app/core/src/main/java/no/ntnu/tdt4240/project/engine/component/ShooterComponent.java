package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class ShooterComponent implements Component {
    public float baseInterval;
    public float timer;

    public ShooterComponent() {
        this(1f);
    }

    public ShooterComponent(float baseInterval) {
        this.baseInterval = baseInterval;
        this.timer = 0f;
    }
}
