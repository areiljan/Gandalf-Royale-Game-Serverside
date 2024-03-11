package ee.taltech.game.server.logic;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.game.server.player.PlayerCharacter;

public class Fireball {
    int playerID;
    int fireballID;
    private final Body fireballBody;
    double fireballXPosition;
    double fireballYPosition;
    double mouseXPosition;
    double mouseYPosition;
    private double angle;
    private static int nextId = 1;
    private final int velocity;

    /**
     * Construct Fireball.
     *
     * @param playerCharacter player that cast fireball
     * @param mouseXPosition mouse x coordinate
     * @param mouseYPosition mouse y coordinate
     * @param world world where fireball body will be put in
     */
    public Fireball(PlayerCharacter playerCharacter, double mouseXPosition, double mouseYPosition, World world) {
        playerID = playerCharacter.getPlayerID();
        fireballID = nextId++;
        fireballBody = createBody(world);

        fireballXPosition = playerCharacter.getxPosition();
        fireballYPosition = playerCharacter.getyPosition();

        // These mousepositions are already relative to the player.
        this.mouseXPosition = mouseXPosition;
        this.mouseYPosition = mouseYPosition;

        // Adjust the velocity of the fireball.
        velocity = 3;
        angle = Math.atan2(mouseYPosition, mouseXPosition);
    }

    /**
     * Get fireball angle.
     *
     * @return angle
     */
    public double getAngle() {
        return this.angle;
    }

    /**
     * Get fireball ID.
     *
     * @return fireballID
     */
    public int getFireballID() {
        return fireballID;
    }

    /**
     * Get fireball caster ID.
     *
     * @return playerID
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Get fireball x position.
     *
     * @return fireballXPosition
     */
    public double getFireballXPosition() {return this.fireballXPosition;}

    /**
     * Get fireball y position.
     *
     * @return fireballYPosition
     */
    public double getFireballYPosition() {return this.fireballYPosition;}

    /**
     * Set hit box position.
     *
     * @param x x coordinate
     * @param y y coordinate
     */
    public void setHitBoxPosition(double x, double y) {
        fireballBody.setTransform((float) x, (float) y, fireballBody.getAngle());
    }

    /**
     * Update fireball position.
     */
    public void updatePosition() {
        // Update fireball position based on angle and velocity
        this.fireballXPosition += velocity * Math.cos(angle);
        this.fireballYPosition -= velocity * Math.sin(angle);
        setHitBoxPosition(fireballXPosition, fireballYPosition);
    }

    /**
     * Create fireball hit box.
     *
     * @param world world where the fireball body is created
     * @return fireball body
     */
    private Body createBody(World world) {
        // Create fireball body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((float) fireballXPosition, (float) fireballYPosition); // Initial position
        Body body = world.createBody(bodyDef);

        // Create fixture for fireball hit box
        CircleShape shape = new CircleShape();
        shape.setRadius(20.0f); // Example hit box size
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        // Dispose shape
        shape.dispose();

        // Set user data to identify fireball
        body.getFixtureList().get(0).setUserData(this);
        return body;
    }
}
