package ee.taltech.server.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.ItemTypes;
import ee.taltech.server.entities.collision.CollisionBodyTypes;

import java.util.List;

public class Spell implements Entity {
    private Vector2 spellVector;
    private final PlayerCharacter playerCharacter;
    int playerId;
    int spellId;
    private final ItemTypes type;
    private Body spellBody;
    float spellXStart;
    float spellYStart;
    private static int nextId = 1;
    private float manaCost;
    private float hitBoxSize;
    private float spellSpeed;
    private int damage;
    private float speed;
    private float amplitude;
    private float frequency;
    private float elapsedTime;
    private float angle;

    /**
     * Construct Spell.
     *
     * @param playerCharacter player that casts spell
     * @param mouseXPosition mouse x coordinate
     * @param mouseYPosition mouse y coordinate
     */
    public Spell(PlayerCharacter playerCharacter, double mouseXPosition, double mouseYPosition, ItemTypes type) {
        this.playerCharacter = playerCharacter;
        playerId = playerCharacter.getPlayerID();
        spellId = nextId++;
        this.type = type;

        setParametersBasedOnSpellType();
        playerCharacter.setMana(playerCharacter.mana - manaCost);

        spellXStart = playerCharacter.getXPosition();
        spellYStart = playerCharacter.getYPosition(); // Position the fireball on top of the head.
        float dx = (float) (mouseXPosition);
        float dy = (float) (mouseYPosition);
        angle = (float) (Math.atan2(dy, dx));
        // These mouse positions are already relative to the player.
        spellVector = new Vector2((float) mouseXPosition, (float) mouseYPosition);
        spellVector.nor();

        this.amplitude = 10F;
        this.frequency = 7;
        this.elapsedTime = 0f;
    }

    /**
     * Get parameters based on the incoming spell type.
     */
    private void setParametersBasedOnSpellType() {
        if (type == ItemTypes.FIREBALL) {
            // Fireballs take precision to hit, but deal a lot of damage.
            manaCost = 20;
            hitBoxSize = 0.3f;
            spellSpeed = Constants.FIREBALL_SPEED;
            damage = 30;
        } else if (type == ItemTypes.PLASMA) {
            // Plasma is a pea-gun, shoots fast and takes little mana, but also deals little damage.
            manaCost = 8;
            hitBoxSize = 0.2f;
            spellSpeed = Constants.PLASMA_SPEED;
            damage = 7;
        } else if (type == ItemTypes.METEOR) {
            // Meteors are huge and really slow, but deal huge damage.
            manaCost = 25;
            hitBoxSize = 0.5f;
            spellSpeed = Constants.METEOR_SPEED;
            damage = 50;
        } else if (type == ItemTypes.KUNAI) {
            // Kunais move fast, and deal big damage, but take a lot of mana.
            // supposed to be a sniper
            manaCost = 40;
            hitBoxSize = 0.2f;
            spellSpeed = Constants.KUNAI_SPEED;
            damage = 30;
        } else if (type == ItemTypes.MAGICMISSILE) {
            // magic missiles move in sinusoidal pattern
            // cheap to cast but hard to aim
            manaCost = 12;
            hitBoxSize = 0.2f;
            spellSpeed = Constants.MAGICMISSILE_SPEED;
            damage = 25;
        } else if (type == ItemTypes.ICE_SHARD) {
            // Ice shard is a shotgun spell with 5 ice shards.
            manaCost = 6; // per one
            hitBoxSize = 0.2f;
            spellSpeed = Constants.ICE_SHARD_SPEED;
            damage = 10;
        }
        // Helix beam only does damage short range
    }

    /**
     * Spell damage getter.
     * @return
     */
    public int getSpellDamage() {
        return damage;
    }

    /**
     * Update spell position.
     */
    public void updatePosition(float deltaTime) {
        elapsedTime += deltaTime;
        if (type == ItemTypes.MAGICMISSILE) {
            // Calculate the perpendicular vector
            Vector2 perpendicularVector = new Vector2(-spellVector.y, spellVector.x);

            // Calculate the scalar value for the perpendicular vector
            float scalar = (float) Math.sin(frequency * elapsedTime) * amplitude * 0.1f;

            // Scale the perpendicular vector by the scalar value
            Vector2 scaledPerpendicularVector = perpendicularVector.scl(scalar);

            // Calculate the velocity of the poison ball vector by adding the scaled perpendicular vector to the main vector
            Vector2 poisonBallVector = spellVector.cpy().add(scaledPerpendicularVector);

            // Set the linear velocity of the spell body using the poison ball vector
            spellVector.clamp(spellSpeed, spellSpeed);
            spellBody.setLinearVelocity(poisonBallVector);
        } else {
            // Update regular projectile position based on angle and velocity
            // In case it is a regular projectile
            spellVector.clamp(spellSpeed, spellSpeed);
            spellBody.setLinearVelocity(spellVector);
        }
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
