package com.invaders99.model;

import com.badlogic.gdx.utils.Array;

public class Leaderboard {
    public final Array<Entry> entries = new Array<>();

    public static class Entry {
        public final String playerName;
        public final int score;
        public final long timestamp;

        public Entry(String playerName, int score, long timestamp) {
            this.playerName = playerName;
            this.score = score;
            this.timestamp = timestamp;
        }
    }
}
