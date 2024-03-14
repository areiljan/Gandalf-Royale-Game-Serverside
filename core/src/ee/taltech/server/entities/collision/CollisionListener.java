package ee.taltech.server.entities.collision;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.entities.Spell;
import ee.taltech.server.entities.PlayerCharacter;
import ee.taltech.server.components.Game;

public class CollisionListener implements ContactListener {

    private Game game;

    public CollisionListener(Game game) {
        this.game = game;
    }

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

        if (dataA instanceof PlayerCharacter && dataB instanceof Spell
                || dataA instanceof Spell && dataB instanceof  PlayerCharacter) {
            spellAndPlayerCollision(dataA, dataB);
        }
    }

    /**
     * Apply logic that happens when spell and player collide.
     *
     * @param dataA one collision body
     * @param dataB second collision body
     */
    private void spellAndPlayerCollision(Object dataA, Object dataB) {
        // Get player
        PlayerCharacter player = dataA instanceof PlayerCharacter ? (PlayerCharacter) dataA : (PlayerCharacter) dataB;
        // Get action
        Spell spell = dataA instanceof Spell ? (Spell) dataA : (Spell) dataB;

        // If player is not the person who cast the action then damage the player
        if (spell.getPlayerId() != player.getPlayerID()) {
            game.damagePlayer(player.playerID, 10);
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
