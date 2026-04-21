package no.ntnu.tdt4240.project.util;

import java.util.Random;

public final class NameGenerator {
    private static final String[] ADJECTIVES = {
        "Brave", "Happy", "Cosmic", "Swift", "Silent", "Stellar", "Lucky", "Quiet",
        "Bold", "Neon", "Iron", "Frosty", "Sunny", "Clever", "Mighty", "Wild",
        "Merry", "Noble", "Rapid", "Zesty"
    };

    private static final String[] NOUNS = {
        "Rocket", "Brook", "Tiger", "Comet", "Falcon", "Nova", "Wolf", "Star",
        "Fox", "Phoenix", "Whale", "Storm", "Viper", "Ranger", "Pilot", "Blaze",
        "Raven", "Orbit", "Meteor", "Nebula"
    };

    private static final Random RNG = new Random();

    private NameGenerator() {}

    public static String random() {
        return ADJECTIVES[RNG.nextInt(ADJECTIVES.length)]
            + " " + NOUNS[RNG.nextInt(NOUNS.length)];
    }
}
