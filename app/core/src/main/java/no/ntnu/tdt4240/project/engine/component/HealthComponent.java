package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class HealthComponent implements Component {
    public static final float INVINCIBILITY_DURATION = 2f;

    public int health;
    public float invincibilityRemaining;

    public HealthComponent(int health) {
        this.health = health;
    }

    public boolean isInvincible() {
        return invincibilityRemaining > 0f;
    }
}
