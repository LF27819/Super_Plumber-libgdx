package com.svalero.Super_Plumber.manager;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class ScoreManager {

    private static final String SCORES_FILE = "scores.txt";
    private static final int MAX_SCORES = 10;

    public static class ScoreEntry implements Comparable<ScoreEntry> {
        public final String name;
        public final int score;

        public ScoreEntry(String name, int score) {
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score);
        }
    }

    public static Array<ScoreEntry> loadScores() {
        Array<ScoreEntry> scores = new Array<>();
        FileHandle file = Gdx.files.local(SCORES_FILE);

        if (!file.exists()) return scores;

        String[] lines = file.readString().split("\n");

        for (String line : lines) {
            line = line.trim();

            if (line.isEmpty()) continue;

            String[] parts = line.split(",", 2);

            if (parts.length < 2) continue;

            try {
                scores.add(new ScoreEntry(parts[0].trim(), Integer.parseInt(parts[1].trim())));
            } catch (NumberFormatException ignored) {
            }
        }

        scores.sort();
        return scores;
    }

    public static int addScore(String name, int score) {
        Array<ScoreEntry> scores = loadScores();

        scores.add(new ScoreEntry(name.isEmpty() ? "PLAYER" : name, score));
        scores.sort();

        while (scores.size > MAX_SCORES) {
            scores.removeIndex(scores.size - 1);
        }

        int position = -1;

        for (int i = 0; i < scores.size; i++) {
            ScoreEntry entry = scores.get(i);

            if (entry.name.equals(name) && entry.score == score) {
                position = i + 1;
                break;
            }
        }

        saveScores(scores);
        return position;
    }

    private static void saveScores(Array<ScoreEntry> scores) {
        StringBuilder builder = new StringBuilder();

        for (ScoreEntry entry : scores) {
            builder.append(entry.name).append(",").append(entry.score).append("\n");
        }

        Gdx.files.local(SCORES_FILE).writeString(builder.toString(), false);
    }
}
