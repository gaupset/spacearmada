package com.invaders99.model;

import com.badlogic.gdx.utils.Array;

public class Game {
    public static final float WORLD_WIDTH = 360f;
    public static final float WORLD_HEIGHT = 640f;
    /** Score milestones: one sabotage charge earned per this many points (10, 20, 30, …). */
    public static final int SABOTAGE_POINTS_PER_CHARGE = 10;
    /** After leaving the pause screen, the HUD pause button stays disabled for this many seconds of active play. */
    public static final float PAUSE_BUTTON_COOLDOWN_SECONDS = 10f;

    public final Player player;
    public final Array<Bullet> bullets = new Array<>();
    public final Array<Enemy> enemies = new Array<>();
    public int score;
    /**
     * How many sabotage uses the player has spent (each successful pick on the sabotage screen).
     * Available charges = {@link #getEarnedSabotageChargeCount()} − this (floored at 0).
     */
    public int sabotagesUsedCount;
    public int lives = 3;
    public boolean invincible;
    public boolean menuOpen;
    /**
     * True while the pause screen or sabotage selection screen is stacked over gameplay.
     * Incoming lobby sabotage is not applied until this is false; {@link #updateSabotageTimers} no-ops so effect
     * timers only count down during active play (also skipped when {@link #menuOpen}).
     */
    public boolean gameplayPaused;
    /**
     * While &gt; 0, the HUD pause control is disabled (counts down only during active play, not while paused or in the in-game menu).
     */
    public float pauseButtonCooldownRemaining;
    public final Array<Bullet> enemyBullets = new Array<>();

    /** Seconds left for sabotage: enemies move faster (see {@link #ENEMY_SPEED_SABOTAGE_MULTIPLIER}). */
    public float enemySpeedBoostRemaining;
    /** Multiplier on enemy descent speed while {@link #enemySpeedBoostRemaining} &gt; 0 (doubled). */
    public static final float ENEMY_SPEED_SABOTAGE_MULTIPLIER = 2f;

    /** Seconds left: player shoots at half rate (0.5× bullets). */
    public float playerFireRateSlowRemaining;
    /** Seconds left: alien spawn rate doubled. */
    public float alienSpawnBoostRemaining;

    public Game() {
        player = new Player(WORLD_WIDTH / 2f, 40f);
    }

    public boolean isGameOver() {
        return lives <= 0;
    }

    /** Sabotage charges earned from score (unused stack is this minus {@link #sabotagesUsedCount}). */
    public int getEarnedSabotageChargeCount() {
        return score / SABOTAGE_POINTS_PER_CHARGE;
    }

    /** Charges still available to spend on the sabotage screen. */
    public int getAvailableSabotageCount() {
        int earned = getEarnedSabotageChargeCount();
        return Math.max(0, earned - sabotagesUsedCount);
    }

    public boolean isSabotageHudVisible() {
        return getAvailableSabotageCount() > 0;
    }

    /** Call once when the player confirms a sabotage type (consumes one charge). */
    public void recordSabotageUse() {
        sabotagesUsedCount++;
    }

    /**
     * Enemy speed sabotage: adds {@code durationSeconds} to the existing timer for this effect.
     * Re-selecting the same sabotage while it is active extends the remaining time (effect stays 2×).
     */
    public void applyEnemySpeedSabotage(float durationSeconds) {
        enemySpeedBoostRemaining += durationSeconds;
    }

    /**
     * Player fire-rate sabotage: adds {@code durationSeconds} to the existing timer (0.5× bullets).
     * Re-applying while active extends the remaining time.
     */
    public void applyPlayerFireRateSabotage(float durationSeconds) {
        playerFireRateSlowRemaining += durationSeconds;
    }

    /**
     * Alien spawn sabotage: adds {@code durationSeconds} to the existing timer (2× spawn rate).
     * Re-applying while active extends the remaining time.
     */
    public void applyAlienSpawnSabotage(float durationSeconds) {
        alienSpawnBoostRemaining += durationSeconds;
    }

    /** Decrements {@link #pauseButtonCooldownRemaining} when the player is actively playing (same window as sabotage timers). */
    public void updatePauseButtonCooldown(float delta) {
        if (gameplayPaused || menuOpen) {
            return;
        }
        if (pauseButtonCooldownRemaining > 0f) {
            pauseButtonCooldownRemaining -= delta;
            if (pauseButtonCooldownRemaining <= 0f) {
                pauseButtonCooldownRemaining = 0f;
            }
        }
    }

    public boolean isPauseButtonReady() {
        return pauseButtonCooldownRemaining <= 0f;
    }

    public void startPauseButtonCooldown() {
        pauseButtonCooldownRemaining = PAUSE_BUTTON_COOLDOWN_SECONDS;
    }

    public void updateSabotageTimers(float delta) {
        if (gameplayPaused || menuOpen) {
            return;
        }
        if (enemySpeedBoostRemaining > 0f) {
            enemySpeedBoostRemaining -= delta;
            if (enemySpeedBoostRemaining <= 0f) {
                enemySpeedBoostRemaining = 0f;
            }
        }
        if (playerFireRateSlowRemaining > 0f) {
            playerFireRateSlowRemaining -= delta;
            if (playerFireRateSlowRemaining <= 0f) {
                playerFireRateSlowRemaining = 0f;
            }
        }
        if (alienSpawnBoostRemaining > 0f) {
            alienSpawnBoostRemaining -= delta;
            if (alienSpawnBoostRemaining <= 0f) {
                alienSpawnBoostRemaining = 0f;
            }
        }
    }

    /** Used for enemy ship descent and enemy bullet speed in {@link com.invaders99.controller.state.GameController}. */
    public float getEnemyVerticalSpeedMultiplier() {
        return enemySpeedBoostRemaining > 0f ? ENEMY_SPEED_SABOTAGE_MULTIPLIER : 1f;
    }

    /** &gt;1 while fire-rate sabotage active: multiply base shoot interval (half bullets ⇒ 2× interval). */
    public float getPlayerShootIntervalMultiplier() {
        return playerFireRateSlowRemaining > 0f ? 2f : 1f;
    }

    /** &gt;1 while alien spawn sabotage active: spawn that many times as often (interval / multiplier). */
    public float getEnemySpawnRateMultiplier() {
        return alienSpawnBoostRemaining > 0f ? 2f : 1f;
    }
}
