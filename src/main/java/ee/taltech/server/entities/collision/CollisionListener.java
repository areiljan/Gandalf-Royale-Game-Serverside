package ee.taltech.server.entities.collision;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.entities.*;
import ee.taltech.server.components.Game;

import java.util.List;
import java.util.Objects;

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

        // Get data out from fixture A's user data
        Entity entityA = (Entity) ((List<?>) fixtureA.getUserData()).getFirst();
        String typeA = (String) ((List<?>) fixtureA.getUserData()).getLast();

        // Get data out from fixture B's user data
        Entity entityB = (Entity) ((List<?>) fixtureB.getUserData()).getFirst();
        String typeB = (String) ((List<?>) fixtureB.getUserData()).getLast();
        // ------------------------------------------------------------------- \\

        // If player and spell collide
        if (entityA instanceof PlayerCharacter && entityB instanceof Spell
                || entityA instanceof Spell && entityB instanceof PlayerCharacter) {
            spellAndPlayerCollision(entityA, entityB);
        }

        // If player and item collide
        if (entityA instanceof PlayerCharacter && entityB instanceof Item
                || entityA instanceof Item && entityB instanceof PlayerCharacter) {
            System.out.println("Player and item collide");
            beginItemAndPlayerCollision(entityA, entityB);
        }

        // If player and mob's triggering range collide
        if (entityA instanceof PlayerCharacter && entityB instanceof Mob && Objects.equals(typeB, "Triggering_Range")
                || entityA instanceof Mob && Objects.equals(typeA, "Triggering_Range")
                && entityB instanceof PlayerCharacter) {
            System.out.println("Player in triggering range");
            playerInMobsTriggeringRange(entityA, entityB);
        }

        // If player and mob's hit box collide
        if (entityA instanceof PlayerCharacter && entityB instanceof Mob && Objects.equals(typeB, "Hit_Box")
                || entityA instanceof Mob && Objects.equals(typeA, "Hit_Box") && entityB instanceof PlayerCharacter) {
            System.out.println("Player and mob collide");
            mobAndPlayerCollision(entityA, entityB);
        }

        // If spell and mob's hit box collide
        if (entityA instanceof Spell && entityB instanceof Mob && Objects.equals(typeB, "Hit_Box")
                || entityA instanceof Mob && Objects.equals(typeA, "Hit_Box") && entityB instanceof Spell) {
            System.out.println("Mob got hit by a spell");
            mobAndSpellCollision(entityA, entityB);
        }
    }

    /**
     * Apply logic that happens when spell and player collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void spellAndPlayerCollision(Entity entityA, Entity entityB) {
        // Get player
        PlayerCharacter player = entityA instanceof PlayerCharacter ? (PlayerCharacter) entityA :
                (PlayerCharacter) entityB;

        // Get spell
        Spell spell;
        if (entityA instanceof Spell) {
            spell = (Spell) entityA;
        } else {
            assert entityB instanceof Spell;
            spell = (Spell) entityB;
        }

        // If player is not the person who cast the action then damage the player
        if (player != null && spell.getPlayerId() != player.getPlayerID()) {
            game.damagePlayer(player.playerID, 10);
            game.removeSpell(spell.getSpellId());
        }
    }

    /**
     * Apply logic that happens when item and player collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void beginItemAndPlayerCollision(Entity entityA, Entity entityB) {
        // Get player
        PlayerCharacter player = entityA instanceof PlayerCharacter ? (PlayerCharacter) entityA :
                (PlayerCharacter) entityB;

        // Get Item
        Item item;
        if (entityA instanceof Item) {
            item = (Item) entityA;
        } else {
            assert entityB instanceof Item;
            item = (Item) entityB;
        }

        // Set items collidingWith value to the player that is colliding with the item
        item.setCollidingWith(player);
    }

    /**
     * Apply logic that happens when player steps into mob's triggering range.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void playerInMobsTriggeringRange(Entity entityA, Entity entityB) {
        // Get player
        PlayerCharacter player = entityA instanceof PlayerCharacter ? (PlayerCharacter) entityA :
                (PlayerCharacter) entityB;

        // Get Mob
        Mob mob;
        if (entityA instanceof Mob) {
            mob = (Mob) entityA;
        } else {
            assert entityB instanceof Mob;
            mob = (Mob) entityB;
        }

        if (player != null) {
            mob.addPlayerInRange(player); // Add player to mob's players in range list
        }
    }

    /**
     * Apply logic that happens when player and mob collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void mobAndPlayerCollision(Entity entityA, Entity entityB) {
        // Get player
        PlayerCharacter player = entityA instanceof PlayerCharacter ? (PlayerCharacter) entityA :
                (PlayerCharacter) entityB;

        if (player != null) {
            game.damagePlayer(player.playerID, Mob.MOB_DAMAGE); // Mob damages player
        }
    }

    /**
     * Apply logic that happens when spell and mob collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void mobAndSpellCollision(Entity entityA, Entity entityB) {
        // Get spell
        Spell spell;
        if (entityA instanceof Spell) {
            spell = (Spell) entityA;
        } else {
            assert entityB instanceof Spell;
            spell = (Spell) entityB;
        }

        // Get Mob
        Mob mob;
        if (entityA instanceof Mob) {
            mob = (Mob) entityA;
        } else {
            assert entityB instanceof Mob;
            mob = (Mob) entityB;
        }

        game.damageMob(mob.getId(), 10); // Damage mob
        game.removeSpell(spell.getSpellId()); // Remove spell
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
        // Get data out from fixture A's user data
        Entity entityA = (Entity) ((List<?>) fixtureA.getUserData()).getFirst();
        String typeA = (String) ((List<?>) fixtureA.getUserData()).getLast();
        // Get data out from fixture B's user data
        Entity entityB = (Entity) ((List<?>) fixtureB.getUserData()).getFirst();
        String typeB = (String) ((List<?>) fixtureB.getUserData()).getLast();
        // ------------------------------------------------------------------- \\

        // If player and item ends colliding
        if (entityA instanceof PlayerCharacter && entityB instanceof Item
                || entityA instanceof Item && entityB instanceof PlayerCharacter) {
            endItemAndPlayerCollision(entityA, entityB);
        }

        // If player and mob's triggering range stop colliding
        if (entityA instanceof PlayerCharacter && Objects.equals(typeB, "Triggering_Range")
                || Objects.equals(typeA, "Triggering_Range") && entityB instanceof PlayerCharacter) {
            playerNotInMobsTriggeringRange(entityA, entityB);
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
     * Apply logic that happens when player steps out of mob's triggering range.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void playerNotInMobsTriggeringRange(Entity entityA, Entity entityB) {
        // Get player
        PlayerCharacter player = entityA instanceof PlayerCharacter ? (PlayerCharacter) entityA :
                (PlayerCharacter) entityB;

        // Get Mob
        Mob mob;
        if (entityA instanceof Mob) {
            mob = (Mob) entityA;
        } else {
            assert entityB instanceof Mob;
            mob = (Mob) entityB;
        }

        if (player != null) {
            mob.removePlayerInRange(player); // Remove player from mob's players in range list
        }
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
