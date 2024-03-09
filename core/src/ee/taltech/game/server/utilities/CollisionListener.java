package ee.taltech.game.server.utilities;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;

public class CollisionListener implements ContactListener {

    /**
     * Detect collision beginning.
     *
     * @param contact contact
     */
    @Override
    public void beginContact(Contact contact) {
        System.out.println("COLLISION!!!!!!!!!!");
//        Fixture fixtureA = contact.getFixtureA();
//        Fixture fixtureB = contact.getFixtureB();
//
//        if (fixtureA.getUserData().equals("player") && fixtureB.getUserData().equals("fireball")
//                || fixtureA.getUserData().equals("fireball") && fixtureB.getUserData().equals("player")) {
//            System.out.println("CORRECT");
//        }
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