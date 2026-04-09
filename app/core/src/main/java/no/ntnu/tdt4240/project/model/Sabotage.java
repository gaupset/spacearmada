package no.ntnu.tdt4240.project.model;

public class Sabotage {
    public static final String TYPE_ENEMY_SPEED = "2x_enemy_speed";
    public static final String TYPE_HALF_PLAYER_BULLETS = "0.5x_player_bullets";
    public static final String TYPE_DOUBLE_ALIENS = "2x_aliens";

    /** Serialized payload type; use TYPE_* constants. */
    public String type = TYPE_ENEMY_SPEED;
    public int duration = 10;
}
