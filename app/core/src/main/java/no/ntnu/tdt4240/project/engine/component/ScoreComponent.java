package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class ScoreComponent implements Component {
    public int score;

    public ScoreComponent() {
        this.score = 0;
    }
}
