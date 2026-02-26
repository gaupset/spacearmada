package com.invaders99.util;

public class AppConfig {
    private static AppConfig instance;

    public final String firebaseBaseUrl;

    public AppConfig(String firebaseBaseUrl) {
        this.firebaseBaseUrl = firebaseBaseUrl;
    }

    public static void init(AppConfig config) {
        instance = config;
    }

    public static AppConfig get() {
        return instance;
    }
}
