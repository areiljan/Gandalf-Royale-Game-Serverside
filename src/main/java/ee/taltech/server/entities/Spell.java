package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.ItemTypes;

import java.util.List;

public class Spell implements Entity {
    int playerId;
    int spellId;
    private final ItemTypes type;
    private Body spellBody;
    double spellXPosition;
    double spellYPosition;
    double mouseXPosition;
    double mouseYPosition;
    private final double angle;
    private static int nextId = 1;
    private static final float FIREBALLVELOCITY = 0.1f;

    /**
     * Construct Spell.
     *
     * @param playerCharacter player that casts spell
     * @param mouseXPosition mouse x coordinate
     * @param mouseYPosition mouse y coordinate
     * @param world world where spell's body will be put in
     */
    public Spell(PlayerCharacter playerCharacter, double mouseXPosition, double mouseYPosition,
                 World world, ItemTypes type) {
        playerId = playerCharacter.getPlayerID();
        spellId = nextId++;
        this.type = type;
        createBody(world);

        spellXPosition = playerCharacter.getXPosition();
        spellYPosition = playerCharacter.getYPosition() + 50 / Constants.PPM;

        // These mousepositions are already relative to the player.
        this.mouseXPosition = mouseXPosition;
        this.mouseYPosition = mouseYPosition;

        // Adjust the velocity of the spell.
        angle = Math.atan2(mouseYPosition, mouseXPosition);
        System.out.println(spellXPosition + " : " + spellYPosition);
    }

    /**
     * Get spell's angle.
     *
     * @return angle
     */
    public double getAngle() {
        return this.angle;
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
    public double getSpellXPosition() {return this.spellXPosition;}

    /**
     * Get spell's y position.
     *
     * @return fireballYPosition
     */
    public double getSpellYPosition() {return this.spellYPosition;}

    /**
     * Set hit box position.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setHitBoxPosition(double x, double y) {
        if (spellBody != null) {
            spellBody.setTransform((float) x, (float) y, spellBody.getAngle());
        }
    }

    /**
     * Update spell position.
     */
    public void updatePosition() {
        // Update fireball position based on angle and velocity
        if (type.equals(ItemTypes.FIREBALL)) {
            this.spellXPosition += FIREBALLVELOCITY * Math.cos(angle);
            this.spellYPosition -= FIREBALLVELOCITY * Math.sin(angle);
            setHitBoxPosition(spellXPosition, spellYPosition - 30 / Constants.PPM);
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
        bodyDef.position.set((float) spellXPosition, (float) spellYPosition); // Initial position
        Body body = world.createBody(bodyDef);

        // Create fixture for fireball hit box
        CircleShape shape = new CircleShape();
        shape.setRadius(0.15f); // Example hit box size
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
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
        world.destroyBody(spellBody); // Destroy the spell's body
    }
}
