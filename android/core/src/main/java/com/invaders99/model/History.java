package com.invaders99.model;

import com.badlogic.gdx.utils.Array;

public class History {
    public final Array<Entry> entries = new Array<>();

    public static class Entry {
        public final int score;
        public final long timestamp;
        public final int wavesCompleted;

        public Entry(int score, long timestamp, int wavesCompleted) {
            this.score = score;
            this.timestamp = timestamp;
            this.wavesCompleted = wavesCompleted;
        }
    }
}
