package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.World;

public class Terrain implements Entity {

    /**
     * Construct Terrain.
     * This class will be used just to make world collision detection work.
     */
    public Terrain() {
        // Constructor will be empty because this class is used only for getting instance
    }

    /**
     * Create body for entity.
     *
     * @param world world where the bodies are in
     */
    @Override
    public void createBody(World world) {
        // This will be empty.
    }
}
