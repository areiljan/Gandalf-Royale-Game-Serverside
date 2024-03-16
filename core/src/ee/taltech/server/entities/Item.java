package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.components.SpellTypes;

public class Item {
    private final Integer id;
    private final SpellTypes type;
    private Float xPosition;
    private Float yPosition;
    private Body body;
    private PlayerCharacter collidingWith;
    private static int currentId = 1;

    /**
     * Construct Item.
     *
     * @param type item's type
     * @param xPosition item's x coordinate
     * @param yPosition item's y coordinate
     */
    public Item(SpellTypes type, Float xPosition, Float yPosition) {
        this.id = currentId++;
        this.type = type;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.body = null;
    }

    /**
     * Get item's ID.
     *
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Get item's type.
     *
     * @return type
     */
    public SpellTypes getType() {
        return type;
    }

    /**
     * Get item's x coordinate.
     *
     * @return xPosition
     */
    public Float getXPosition() {
        return xPosition;
    }

    /**
     * Get item's y coordinate.
     *
     * @return yPosition
     */
    public Float getYPosition() {
        return yPosition;
    }

    /**
     * Get player character that the item is colliding with.
     *
     * @return collidingWith
     */
    public PlayerCharacter getCollidingWith() {
        return collidingWith;
    }

    /**
     * Set item's x coordinate.
     *
     * @param xPosition new x coordinate
     */
    public void setXPosition(Float xPosition) {
        this.xPosition = xPosition;
    }

    /**
     * Set item's y coordinate.
     *
     * @param yPosition new y coordinate
     */
    public void setYPosition(Float yPosition) {
        this.yPosition = yPosition;
    }

    /**
     * Set player character that the item is colliding.
     *
     * @param collidingWith  player character
     */
    public void setCollidingWith(PlayerCharacter collidingWith) {
        this.collidingWith = collidingWith;
    }

    public void createBody(World world) {
        // Create a dynamic body for item
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xPosition, yPosition);
        Body hitBoxBody = world.createBody(bodyDef);

        // Create a fixture defining the hit box shape
        PolygonShape hitBoxShape = new PolygonShape();
        hitBoxShape.setAsBox(25, 25);

        // Attach the fixture to the body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = hitBoxShape;
        hitBoxBody.createFixture(fixtureDef);

        // Clean up
        hitBoxShape.dispose();

        hitBoxBody.getFixtureList().get(0).setUserData(this);
        this.body = hitBoxBody;
        body.setTransform(xPosition, yPosition, body.getAngle());
    }

    public void removeBody() {
        body = null;
    }
}
