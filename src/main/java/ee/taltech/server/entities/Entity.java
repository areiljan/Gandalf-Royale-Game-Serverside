package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.World;

public interface Entity {

    /**
     * Create body for entity.
     *
     * @param world world where the bodies are in
     */
    void createBody(World world);
}
