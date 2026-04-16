package no.ntnu.tdt4240.project.engine.component;

import com.badlogic.ashley.core.Component;

public class TutorialScenarioComponent implements Component {
    public static final int MODE_POWERUP = 0;
    public static final int MODE_SABOTAGE = 1;

    public static final float FORCED_INVINCIBILITY_SECONDS = 99999f;

    public int mode = MODE_POWERUP;
    public int scoreThreshold = 5;
    public float nextPromptDelaySeconds = 5f;

    public boolean pauseRequested = false;
    public boolean powerupChosen = false;
    public boolean sabotageChosen = false;
    public boolean nextPromptVisible = false;
    public float postSelectionElapsedSeconds = 0f;
}
