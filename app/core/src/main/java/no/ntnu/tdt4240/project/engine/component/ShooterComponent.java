package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

/**
 * The ShooterComponent class represents the shooter identifier. This is an empty tag component
 * that may be used to mark entities as shooters.
 */
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
