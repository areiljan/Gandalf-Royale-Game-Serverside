package ee.taltech.server.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.ItemTypes;

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
    private static final float FIREBALLVELOCITY = 0.1f;

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

        spellXStart = playerCharacter.getXPosition();
        spellYStart = playerCharacter.getYPosition(); // Position the fireball on top of the head.

        // These mouse positions are already relative to the player.
        spellVector = new Vector2((float) mouseXPosition, (float) mouseYPosition);
        spellVector.nor();
        System.out.println(spellXStart + " : " + spellYStart);
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
     * Update spell position.
     */
    public void updatePosition() {
        // Update fireball position based on angle and velocity
        if (type.equals(ItemTypes.FIREBALL)) {
            spellVector.clamp(Constants.FIREBALL_SPEED, Constants.FIREBALL_SPEED);
            spellBody.setLinearVelocity(spellVector);
        } else if (type.equals(ItemTypes.ICE_SHARD)) {

        }
    }

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
        shape.setRadius(0.3f); // Example hit box size
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef);

        // Dispose shape
        shape.dispose();

        // Set user data to identify fireball
        body.getFixtureList().get(0).setUserData(List.of(this, "Hit_Box"));
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
