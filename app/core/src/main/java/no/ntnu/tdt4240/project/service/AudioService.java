package no.ntnu.tdt4240.project.service;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class AudioService {
    private static final String PREFS_NAME = "spacearmada_prefs";
    private static final String PREF_MUSIC_VOLUME = "music_volume";
    private static final String PREF_SOUND_VOLUME = "sound_volume";

    private static AudioService instance;

    private float musicVolume;
    private float soundVolume;
    private Music currentMusic;
    private String currentMusicFile;
    private final Preferences prefs;

    private AudioService() {
        prefs = Gdx.app.getPreferences(PREFS_NAME);
        musicVolume = prefs.getFloat(PREF_MUSIC_VOLUME, 0.5f);
        soundVolume = prefs.getFloat(PREF_SOUND_VOLUME, 0.5f);
        // Clear any stale enabled/disabled flags from old versions
        if (prefs.contains("music_enabled") || prefs.contains("sound_enabled")) {
            prefs.remove("music_enabled");
            prefs.remove("sound_enabled");
            prefs.flush();
        }
    }

    public static AudioService getInstance() {
        if (instance == null) {
            instance = new AudioService();
        }
        return instance;
    }

    public void playMusic(String fileName, boolean loop) {
        // Don't restart if already playing the same track
        if (currentMusic != null && fileName.equals(currentMusicFile)) {
            if (!currentMusic.isPlaying()) {
                currentMusic.play();
            }
            return;
        }
        stopMusic();
        try {
            currentMusic = Gdx.audio.newMusic(Gdx.files.internal(fileName));
            currentMusicFile = fileName;
            currentMusic.setLooping(loop);
            currentMusic.setVolume(musicVolume);
            currentMusic.play();
        } catch (Exception e) {
            Gdx.app.error("AudioService", "Could not play music: " + fileName, e);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            currentMusicFile = null;
        }
    }

    public void playSound(Sound sound) {
        if (sound != null && soundVolume > 0f) {
            sound.play(soundVolume);
        }
    }

    public float getMusicVolume() {
        return musicVolume;
    }

    public void setMusicVolume(float volume) {
        this.musicVolume = volume;
        if (currentMusic != null) {
            currentMusic.setVolume(volume);
        }
        prefs.putFloat(PREF_MUSIC_VOLUME, volume);
        prefs.flush();
    }

    public float getSoundVolume() {
        return soundVolume;
    }

    public void setSoundVolume(float volume) {
        this.soundVolume = volume;
        prefs.putFloat(PREF_SOUND_VOLUME, volume);
        prefs.flush();
    }
}
