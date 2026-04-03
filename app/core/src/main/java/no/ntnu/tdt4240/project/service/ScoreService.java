package no.ntnu.tdt4240.project.service;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class ScoreService {
    private static final String PREFS_NAME = "spacearmada_prefs";
    private static final String PREF_HIGH_SCORE = "high_score";

    private static ScoreService instance;
    private final Preferences prefs;
    private int highScore;

    private ScoreService() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        highScore = prefs.getInteger(PREF_HIGH_SCORE, 0);
    }

    public static ScoreService getInstance() {
        if (instance == null) {
            instance = new ScoreService();
        }
        return instance;
    }

    public int getHighScore() {
        return highScore;
    }

    public boolean updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            prefs.putInteger(PREF_HIGH_SCORE, highScore);
            prefs.flush();
            return true;
        }
        return false;
    }
}
