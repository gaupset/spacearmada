package no.ntnu.tdt4240.project.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

/**
 * Service class for handling all audio operations, including volume management
 * and persistent storage of audio settings.
 */
public class AudioService {
    // Preference keys for persistent storage. It makes sure that the game remembers the volume next time the user opens the it.
    private static final String PREFS_NAME = "spacearmada_prefs";
    private static final String PREF_MUSIC_VOLUME = "music_volume";
    private static final String PREF_SOUND_VOLUME = "sound_volume";
    private static final String PREF_MUSIC_ENABLED = "music_enabled";
    private static final String PREF_SOUND_ENABLED = "sound_enabled";

    private static AudioService instance;

    private float musicVolume;
    private float soundVolume;
    private boolean musicEnabled;
    private boolean soundEnabled;
    private Music currentMusic;
    private final Preferences prefs;

    private AudioService() {
        // Load saved preferences or use default values (0.5f volume, enabled by default)
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        musicVolume = prefs.getFloat(PREF_MUSIC_VOLUME, 0.5f);
        soundVolume = prefs.getFloat(PREF_SOUND_VOLUME, 0.5f);
        musicEnabled = prefs.getBoolean(PREF_MUSIC_ENABLED, true);
        soundEnabled = prefs.getBoolean(PREF_SOUND_ENABLED, true);
    }

    /**
     * Returns the singleton instance of the AudioService.
     */
    public static AudioService getInstance() {
        if (instance == null) {
            instance = new AudioService();
        }
        return instance;
    }

    /**
     * Plays background music from the specified internal file path.
     *
     * @param fileName Path to the music file in assets.
     * @param loop Whether the music should loop automatically.
     */
    public void playMusic(String fileName, boolean loop) {
        stopMusic(); // Stop any currently playing music before starting new track
        try {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(fileName));
            currentMusic.setLooping(loop);
            updateMusicVolume(); // Apply current volume/mute settings
            currentMusic.play();
        } catch (Exception e) {
            Gdx.app.error("AudioService", "Could not play music: " + fileName, e);
        }
    }

    /**
     * Stops the currently playing music and disposes of its resources.
     */
    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }
    }

    /**
     * Plays a sound effect once, respecting the global sound volume and enabled state.
     *
     * @param sound The Sound object to play.
     */
    public void playSound(Sound sound) {
        if (sound != null && soundEnabled) {
            sound.play(soundVolume);
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    /**
     * Updates the music volume and persists the setting.
     */
    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        updateMusicVolume();
        prefs.putFloat(PREF_MUSIC_VOLUME, volume);
        prefs.flush();
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    /**
     * Updates the sound effects volume and persists the setting.
     */
    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
        prefs.putFloat(PREF_SOUND_VOLUME, volume);
        prefs.flush();
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /**
     * Enables or disables music. If disabled, volume is set to 0.
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        updateMusicVolume();
        prefs.putBoolean(PREF_MUSIC_ENABLED, enabled);
        prefs.flush();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /**
     * Enables or disables sound effects.
     */
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        prefs.putBoolean(PREF_SOUND_ENABLED, enabled);
        prefs.flush();
    }

    /**
     * Internal helper to update the volume of the active music track based on
     * both the volume level and the enabled/disabled state.
     */
    private void updateMusicVolume() {
        if (currentMusic != null) {
            currentMusic.setVolume(musicEnabled ? musicVolume : 0f);
        }
    }
}
