package ee.taltech.server.entities.collision;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.ItemTypes;
import ee.taltech.server.entities.*;
import ee.taltech.server.components.Game;

import java.util.List;
import java.util.Objects;

public class CollisionListener implements ContactListener {

    private final Game game;

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
        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {
            Entity entityA = (Entity) ((List<?>) fixtureA.getUserData()).getFirst();
            String typeA = (String) ((List<?>) fixtureA.getUserData()).getLast();

            // Get data out from fixture B's user data
            Entity entityB = (Entity) ((List<?>) fixtureB.getUserData()).getFirst();
            String typeB = (String) ((List<?>) fixtureB.getUserData()).getLast();
            // ------------------------------------------------------------------- \\

            // If player and spell collide
            if (entityA instanceof PlayerCharacter && typeA.equals("Hit_Box") && entityB instanceof Spell
                    || entityA instanceof Spell && entityB instanceof PlayerCharacter && typeB.equals("Hit_Box")) {
                spellAndPlayerCollision(entityA, entityB);
            }

            // If player and coin collide
            else if (entityA instanceof PlayerCharacter
                    && typeA.equals("World_Collision")
                    && entityB instanceof Item itemB
                    && itemB.getType() == ItemTypes.COIN
                    || entityA instanceof Item itemA
                    && itemA.getType() == ItemTypes.COIN
                    && entityB instanceof PlayerCharacter
                    && typeB.equals("World_Collision")) {
                beginCoinAndPlayerCollision(entityA, entityB);
            }

            // If player and item collide
            else if (entityA instanceof PlayerCharacter
                    && typeA.equals("World_Collision")
                    && entityB instanceof Item
                    || entityA instanceof Item
                    && entityB instanceof PlayerCharacter
                    && typeB.equals("World_Collision")) {
                beginItemAndPlayerCollision(entityA, entityB);
            }

            // If player and mob's triggering range collide
            else if (entityA instanceof PlayerCharacter && entityB instanceof Mob && Objects.equals(typeB, "Triggering_Range")
                    || entityA instanceof Mob && Objects.equals(typeA, "Triggering_Range")
                    && entityB instanceof PlayerCharacter) {
                playerInMobsTriggeringRange(entityA, entityB);
            }

            // If player and mob's hit box collide
            else if (entityA instanceof PlayerCharacter && typeA.equals("Hit_Box")
                    && entityB instanceof Mob && Objects.equals(typeB, "Hit_Box")
                    || entityA instanceof Mob && Objects.equals(typeA, "Hit_Box")
                    && entityB instanceof PlayerCharacter && typeA.equals("Hit_Box")) {
                mobAndPlayerCollision(entityA, entityB);
            }

            // If spell and mob's hit box collide
            else if (entityA instanceof Spell && entityB instanceof Mob && Objects.equals(typeB, "Hit_Box")
                    || entityA instanceof Mob && Objects.equals(typeA, "Hit_Box") && entityB instanceof Spell) {
                mobAndSpellCollision(entityA, entityB);
            }
        }
    }


    /**
     * Apply logic that happens when spell and player collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void spellAndPlayerCollision(Entity entityA, Entity entityB) {
        PlayerCharacter player;
        Spell spell;
        if (entityA instanceof PlayerCharacter playerA) {
            player = playerA;
            spell = (Spell) entityB;
        } else {
            player = (PlayerCharacter) entityB;
            spell = (Spell) entityA;
        }

        // If player is not the person who cast the action then damage the player
        if (player != null && spell.getPlayerId() != player.getPlayerID()) {
            game.damagePlayer(player.playerID, 10);
            game.removeSpell(spell.getSpellId());
        }
    }

    /**
     * Apply logic that happens when coin and player collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void beginCoinAndPlayerCollision(Entity entityA, Entity entityB) {
        PlayerCharacter player;
        Item coin;
        if (entityA instanceof PlayerCharacter playerA) {
            player = playerA;
            coin = (Item) entityB;
        } else {
            player = (PlayerCharacter) entityB;
            coin = (Item) entityA;
        }

        game.pickUpCoin(player, coin);
    }

    /**
     * Apply logic that happens when item and player collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void beginItemAndPlayerCollision(Entity entityA, Entity entityB) {
        PlayerCharacter player;
        Item item;
        if (entityA instanceof PlayerCharacter playerA) {
            player = playerA;
            item = (Item) entityB;
        } else {
            player = (PlayerCharacter) entityB;
            item = (Item) entityA;
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
        PlayerCharacter player;
        Mob mob;
        if (entityA instanceof PlayerCharacter playerA) {
            player = playerA;
            mob = (Mob) entityB;
        } else {
            player = (PlayerCharacter) entityB;
            mob = (Mob) entityA;
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
        PlayerCharacter player;
        if (entityA instanceof PlayerCharacter playerA) {
            player = playerA;
        } else {
            player = (PlayerCharacter) entityB;
        }

        if (player != null) {
            game.damagePlayer(player.playerID, Constants.MOB_DAMAGE); // Mob damages player
        }
    }

    /**
     * Apply logic that happens when spell and mob collide.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void mobAndSpellCollision(Entity entityA, Entity entityB) {
        Spell spell;
        Mob mob;
        if (entityA instanceof Spell spellA) {
            spell = spellA;
            mob = (Mob) entityB;
        } else {
            spell = (Spell) entityB;
            mob = (Mob) entityA;
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

        if (fixtureA.getUserData() != null && fixtureB.getUserData() != null) {

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
    }

    /**
     * Apply logic that happens when item and player stop colliding.
     *
     * @param entityA one collision body
     * @param entityB second collision body
     */
    private void endItemAndPlayerCollision(Entity entityA, Entity entityB) {
        Item item;
        if (entityA instanceof Item itemA) {
            item =  itemA;
        } else {
            item = (Item) entityB;
        }

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
        PlayerCharacter player;
        Mob mob;
        if (entityA instanceof PlayerCharacter playerA) {
            player = playerA;
            mob = (Mob) entityB;
        } else {
            player = (PlayerCharacter) entityB;
            mob = (Mob) entityA;
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
