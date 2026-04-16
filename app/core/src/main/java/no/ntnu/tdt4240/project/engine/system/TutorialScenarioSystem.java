package no.ntnu.tdt4240.project.engine.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;

import no.ntnu.tdt4240.project.engine.Mapper;
import no.ntnu.tdt4240.project.engine.component.HealthComponent;
import no.ntnu.tdt4240.project.engine.component.PlayerComponent;
import no.ntnu.tdt4240.project.engine.component.ScoreComponent;
import no.ntnu.tdt4240.project.engine.component.TutorialScenarioComponent;

public class TutorialScenarioSystem extends IteratingSystem {
    public TutorialScenarioSystem(int priority) {
        super(Family.all(TutorialScenarioComponent.class).get(), priority);
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        TutorialScenarioComponent tutorial = Mapper.tutorialScenario.get(entity);
        applyForcedInvincibility();

        int score = getPlayerScore();
        if (tutorial.mode == TutorialScenarioComponent.MODE_POWERUP) {
            updatePowerupTutorial(tutorial, score, deltaTime);
        } else if (tutorial.mode == TutorialScenarioComponent.MODE_SABOTAGE) {
            updateSabotageTutorial(tutorial, score);
        }
    }

    private void updatePowerupTutorial(TutorialScenarioComponent tutorial, int score, float deltaTime) {
        if (!tutorial.powerupChosen && score >= tutorial.scoreThreshold) {
            tutorial.pauseRequested = true;
            return;
        }
        if (tutorial.powerupChosen && !tutorial.nextPromptVisible) {
            tutorial.postSelectionElapsedSeconds += deltaTime;
            if (tutorial.postSelectionElapsedSeconds >= tutorial.nextPromptDelaySeconds) {
                tutorial.nextPromptVisible = true;
            }
        }
    }

    private void updateSabotageTutorial(TutorialScenarioComponent tutorial, int score) {
        if (!tutorial.sabotageChosen && score >= tutorial.scoreThreshold) {
            tutorial.pauseRequested = true;
        }
    }

    private void applyForcedInvincibility() {
        for (Entity player : getEngine().getEntitiesFor(Family.all(PlayerComponent.class, HealthComponent.class).get())) {
            HealthComponent health = Mapper.health.get(player);
            if (health != null) {
                health.invincibilityRemaining = TutorialScenarioComponent.FORCED_INVINCIBILITY_SECONDS;
            }
        }
    }

    private int getPlayerScore() {
        for (Entity player : getEngine().getEntitiesFor(Family.all(PlayerComponent.class, ScoreComponent.class).get())) {
            ScoreComponent score = Mapper.score.get(player);
            return score == null ? 0 : score.score;
        }
        return 0;
    }
}
