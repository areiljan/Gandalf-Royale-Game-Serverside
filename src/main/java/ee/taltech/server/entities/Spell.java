package ee.taltech.server.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.ItemTypes;
import ee.taltech.server.entities.collision.CollisionBodyTypes;

import java.util.List;

public class Spell implements Entity {
    private final Vector2 spellVector;
    int playerId;
    int spellId;
    private final ItemTypes type;
    private Body spellBody;
    double spellXStart;
    double spellYStart;
    private static int nextId = 1;
    private float manaCost;
    private float hitBoxSize;
    private float spellSpeed;
    private int damage;

    /**
     * Construct Spell.
     *
     * @param playerCharacter player that casts spell
     * @param mouseXPosition mouse x coordinate
     * @param mouseYPosition mouse y coordinate
     */
    public Spell(PlayerCharacter playerCharacter, double mouseXPosition, double mouseYPosition, ItemTypes type) {
        playerId = playerCharacter.getPlayerID();
        spellId = nextId++;
        this.type = type;

        setParametersBasedOnSpellType();
        playerCharacter.setMana(playerCharacter.mana - manaCost);

        spellXStart = playerCharacter.getXPosition();
        spellYStart = playerCharacter.getYPosition(); // Position the fireball on top of the head.

        // These mouse positions are already relative to the player.
        spellVector = new Vector2((float) mouseXPosition, (float) mouseYPosition);
        spellVector.nor();
    }

    private void setParametersBasedOnSpellType() {
        if (type == ItemTypes.FIREBALL) {
            // Fireballs take precision to hit, but deal a lot of damage.
            manaCost = 25;
            hitBoxSize = 0.3f;
            spellSpeed = Constants.FIREBALL_SPEED;
            damage = 25;
        } else if (type == ItemTypes.PLASMA) {
            // Plasma is a pea-gun, shoots fast and takes little mana, but also deals little damage.
            manaCost = 5;
            hitBoxSize = 0.2f;
            spellSpeed = Constants.PLASMA_SPEED;
            damage = 7;
        } else if (type == ItemTypes.METEOR) {
            // Meteors are huge and really slow, but deal huge damage.
            manaCost = 33;
            hitBoxSize = 0.4f;
            spellSpeed = Constants.METEOR_SPEED;
            damage = 60;
        } else if (type == ItemTypes.KUNAI) {
            // Kunais move fast, and deal big damage, but take a lot of mana.
            // supposed to be a sniper
            manaCost = 50;
            hitBoxSize = 0.2f;
            spellSpeed = Constants.KUNAI_SPEED;
            damage = 30;
        }
        // Ice shard is a shotgun spell with 5 ice shards.
        // Helix beam only does damage short range
    }

    public int getSpellDamage() {
        return damage;
    }

    /**
     * Update spell position.
     */
    public void updatePosition() {
        // Update fireball position based on angle and velocity
        spellVector.clamp(spellSpeed, spellSpeed);
        spellBody.setLinearVelocity(spellVector);
    }

    /**
     * Get spell's type.
     *
     * @return type
     */
    public ItemTypes getType() {
        return type;
    }

    /**
     * Get spell's ID.
     *
     * @return fireballID
     */
    public int getSpellId() {
        return spellId;
    }

    /**
     * Get spell's caster ID.
     *
     * @return playerID
     */
    public int getPlayerId() {
        return playerId;
    }

    /**
     * Get spell's x position.
     *
     * @return fireballXPosition
     */
    public double getSpellXPosition() {return spellBody.getPosition().x;}

    /**
     * Get spell's y position.
     *
     * @return fireballYPosition
     */
    public double getSpellYPosition() {return spellBody.getPosition().y;}

    /**
     * Create fireball hit box.
     *
     * @param world world where the fireball body is created
     */
    public void createBody(World world) {
        // Create fireball body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((float) spellXStart, (float) spellYStart); // Initial position
        Body body = world.createBody(bodyDef);

        // Create fixture for fireball hit box
        CircleShape shape = new CircleShape();
        shape.setRadius(hitBoxSize);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        // Dispose shape
        shape.dispose();

        // Set user data to identify fireball
        body.getFixtureList().get(0).setUserData(List.of(this, CollisionBodyTypes.HIT_BOX));
        spellBody = body;
    }

    /**
     * Remove body.
     */
    public void removeSpellBody(World world) {
        if (spellBody != null) {
            world.destroyBody(spellBody); // Destroy the spell's body
        }
    }
}
