package no.ntnu.tdt4240.project.util;

public class AppConfig {
    private static AppConfig instance;
    
    public final String firebaseBaseUrl;
    public final String firestoreBaseUrl;
    public final String databaseBaseUrl;
    public final String projectId;

    public AppConfig(String firebaseBaseUrl, String firestoreBaseUrl, String databaseBaseUrl, String projectId) {
        this.firebaseBaseUrl = firebaseBaseUrl;
        this.firestoreBaseUrl = firestoreBaseUrl;
        this.databaseBaseUrl = databaseBaseUrl;
        this.projectId = projectId;
    }

    public static void init(AppConfig config) {
        instance = config;
    }

    public static AppConfig get() {
        return instance;
    }
}
