package ee.taltech.game.server.utilities;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.game.server.logic.Fireball;
import ee.taltech.game.server.player.PlayerCharacter;

public class CollisionListener implements ContactListener {

    /**
     * Detect collision beginning.
     *
     * @param contact contact
     */
    @Override
    public void beginContact(Contact contact) {
        // Fixtures that are in contact
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Fixtures data aka objects that collide
        Object dataA = fixtureA.getUserData();
        Object dataB = fixtureB.getUserData();

        if (dataA instanceof PlayerCharacter && dataB instanceof Fireball
                || dataA instanceof Fireball && dataB instanceof  PlayerCharacter) {
            System.out.println("Player and fireball collision!");
        }
    }

    /**
     * IGNORED.
     *
     * @param contact ignored
     */
    @Override
    public void endContact(Contact contact) {
        // Optional: Handle end of contact events
    }

    /**
     * IGNORED.
     *
     * @param contact ignored
     * @param oldManifold ignored
     */
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // Optional: Pre-solve callback
    }

    /**
     * IGNORED.
     *
     * @param contact ignored
     * @param impulse ignored
     */
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // Optional: Post-solve callback
    }
}
