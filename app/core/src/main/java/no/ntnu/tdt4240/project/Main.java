package no.ntnu.tdt4240.project;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

import no.ntnu.tdt4240.project.service.AudioService;
import no.ntnu.tdt4240.project.service.FirebaseService;
import no.ntnu.tdt4240.project.state.StateManager;
import no.ntnu.tdt4240.project.state.MenuState;
import no.ntnu.tdt4240.project.ui.UiFactory;
import no.ntnu.tdt4240.project.util.AppConfig;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private StateManager sm;
    private Assets assets;

    @Override
    public void create() {
        // Toggle this flag to switch between local emulator and deployed Firebase services.
        // true  → Android emulator on the same machine (10.0.2.2 = host machine localhost)
        // false → Deployed Firebase project (Cloud Functions + production RTDB)
        final boolean DEV_MODE = false;

        if (AppConfig.get() == null) {
            if (DEV_MODE) {
                AppConfig.init(new AppConfig(
                    "http://10.0.2.2:5001/invaders99-3f807/us-central1", // Cloud Functions emulator
                    "http://10.0.2.2:8082",                               // Firestore emulator
                    "http://10.0.2.2:9000",                               // RTDB emulator
                    "invaders99-3f807"
                ));
            } else {
                AppConfig.init(new AppConfig(
                    "https://us-central1-invaders99-3f807.cloudfunctions.net", // Cloud Functions
                    "",                                                         // Firestore: native SDK only
                    "https://invaders99-3f807-default-rtdb.europe-west1.firebasedatabase.app", // RTDB
                    "invaders99-3f807"
                ));
            }
        }
        FirebaseService.init();

        batch = new SpriteBatch();

        assets = new Assets();
        assets.load();

        UiFactory.init(assets.getDefaultFont());

        AudioService.getInstance().playMusic("audio/elevator_music.mp3", true);

        sm = new StateManager();
        // State entry point - passing assets to the state
        sm.push(new MenuState(sm, batch, assets));
    }

    @Override
    public void render() {
        ScreenUtils.clear(Color.BLACK);

        // Draw global background
        batch.begin();
        batch.draw(assets.getStarsBackground(), 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        sm.update(Gdx.graphics.getDeltaTime());
        sm.render();
    }

    @Override
    public void resize(int width, int height) {
        sm.resize(width, height);
    }

    @Override
    public void dispose() {
        batch.dispose();
        sm.dispose();
        UiFactory.getInstance().dispose();
        if (assets != null) assets.dispose();
        AudioService.getInstance().stopMusic();
    }
}
