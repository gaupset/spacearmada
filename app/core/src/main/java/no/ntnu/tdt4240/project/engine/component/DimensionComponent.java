package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class DimensionComponent implements Component {
    public float width, height;

    public DimensionComponent(float width, float height) {
        this.width = width;
        this.height = height;
    }
}
