package ee.taltech.server;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Files;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3NativesLoader;


public class Headless {

    /**
     * Start headless GDX.
     */
    public static void loadHeadless() {
        Lwjgl3NativesLoader.load();
        Gdx.files = new Lwjgl3Files();
    }
}
