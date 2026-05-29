package com.svalero.Super_Plumber.manager;

public class ConfigurationManager {

    private static boolean musicEnabled = true;
    private static boolean soundEnabled = true;
    private static boolean hardMode = false;

    private ConfigurationManager() {
    }

    public static boolean isMusicEnabled() {
        return musicEnabled;
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static boolean isHardMode() {
        return hardMode;
    }

    public static void toggleMusic() {
        musicEnabled = !musicEnabled;
    }

    public static void toggleSound() {
        soundEnabled = !soundEnabled;
    }

    public static void toggleHardMode() {
        hardMode = !hardMode;
    }
}
