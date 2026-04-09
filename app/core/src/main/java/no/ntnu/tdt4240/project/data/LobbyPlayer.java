package no.ntnu.tdt4240.project.data;

public class LobbyPlayer {
    public String actualName;
    public boolean inGameOverScreen;
    public boolean gameOver;
    public boolean leftLobby;
    public Object lastTimeOnline;
    public int personalHighScore;
    public int score;
    public Sabotage sabotage;
    /** Session id of the player to receive this player's sabotages; set when the match starts. */
    public String sabotageTargetId;

    public LobbyPlayer() {}
}
