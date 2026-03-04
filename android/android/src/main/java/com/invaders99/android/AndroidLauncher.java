package com.invaders99.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.invaders99.Main;
import com.invaders99.util.AppConfig;

/** Launches the Android application. */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true; // Recommended, but not required.
        AppConfig config = new AppConfig(
            "http://10.0.2.2:5001/invaders99-3f807/us-central1",
            "http://10.0.2.2:8080",
            "http://10.0.2.2:9000",
            "invaders99-3f807"
        );
        initialize(new Main(config), configuration);
    }
}
