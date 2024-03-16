package ee.taltech.server.entities.collision;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.entities.Item;
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
                || dataA instanceof Spell && dataB instanceof PlayerCharacter) {
            spellAndPlayerCollision(dataA, dataB);
        }

        if (dataA instanceof PlayerCharacter && dataB instanceof Item
                || dataA instanceof Item && dataB instanceof PlayerCharacter) {
            beginItemAndPlayerCollision(dataA, dataB);
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
        // Get spell
        Spell spell = dataA instanceof Spell ? (Spell) dataA : (Spell) dataB;

        // If player is not the person who cast the action then damage the player
        if (spell.getPlayerId() != player.getPlayerID()) {
            game.damagePlayer(player.playerID, 10);
        }
    }

    /**
     * Apply logic that happens when item and player collide.
     *
     * @param dataA one collision body
     * @param dataB second collision body
     */
    private void beginItemAndPlayerCollision(Object dataA, Object dataB) {
        // Get player
        PlayerCharacter player = dataA instanceof PlayerCharacter ? (PlayerCharacter) dataA : (PlayerCharacter) dataB;
        // Get Item
        Item item = dataA instanceof Item ? (Item) dataA : (Item) dataB;

        // Set items collidingWith value to the player that is colliding with the item
        item.setCollidingWith(player);
    }

    /**
     * Detect collision ending.
     *
     * @param contact contact
     */
    @Override
    public void endContact(Contact contact) {
        // Fixtures that are in contact
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        // Fixtures data aka objects that collide
        Object dataA = fixtureA.getUserData();
        Object dataB = fixtureB.getUserData();

        if (dataA instanceof PlayerCharacter && dataB instanceof Item
                || dataA instanceof Item && dataB instanceof PlayerCharacter) {
            endItemAndPlayerCollision(dataA, dataB);
        }
    }

    /**
     * Apply logic that happens when item and player stop colliding.
     *
     * @param dataA one collision body
     * @param dataB second collision body
     */
    private void endItemAndPlayerCollision(Object dataA, Object dataB) {
        // Get Item
        Item item = dataA instanceof Item ? (Item) dataA : (Item) dataB;

        // Set items collidingWith value to null if no player is no longer colliding with the item
        item.setCollidingWith(null);
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
