package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.network.messages.game.KeyPress;
import ee.taltech.server.components.SpellTypes;

import java.util.HashMap;
import java.util.Map;


public class PlayerCharacter {

    public static final Integer WIDTH = 12;
    public static final Integer HEIGHT = 24;
    private Body body;
    public int xPosition;
    public int yPosition;
    public int mouseXPosition;
    public int mouseYPosition;
    private boolean mouseLeftClick;
    public final int playerID;
    public SpellTypes type;
    boolean moveLeft;
    boolean moveRight;
    boolean moveDown;
    boolean moveUp;

    public boolean getMouseLeftClick() {
        return mouseLeftClick;
    }

    public Integer health;
    public double mana;
    private Map<Integer, Item> inventory;

    /**
     * Construct PlayerCharacter.
     *
     * @param playerID player's ID
     */
    public PlayerCharacter(Integer playerID) {
        // Here should be the semi-random spawn points for a PlayerCharacter
        this.xPosition = 4700;
        this.yPosition = 5800;
        this.playerID = playerID;
        health = 100;
        mana = 100;
        inventory = new HashMap<>();
    }

    /**
     * Get player's ID.
     *
     * @return playerID
     */
    public int getPlayerID() {
        return playerID;
    }

    /**
     * Get x position.
     *
     * @return xPosition
     */
    public int getXPosition() {
        return this.xPosition;
    }

    /**
     * Get y position.
     *
     * @return yPosition
     */
    public int getYPosition() {
        return this.yPosition;
    }

    /**
     * Get mouse x position.
     *
     * @return mouseXPosition
     */
    public double getMouseXPosition() {
        return mouseXPosition;
    }

    /**
     * Get mouse y position.
     *
     * @return mouseYPosition
     */
    public double getMouseYPosition() {
        return mouseYPosition;
    }

    /**
     * Get players current action.
     *
     * @return action
     */
    public SpellTypes getSpell() {
        return type;
    }

    /**
     * Set health value.
     *
     * @param newHealth new health value
     */
    public void setHealth(Integer newHealth) {
        health = newHealth;
    }

    /**
     * Set mana value.
     *
     * @param newMana new mana value
     */
    public void setMana(double newMana) {
        mana = newMana;
    }

    /**
     * Method sets the heading action for the player, but doesn't update the position coordinates.
     * Only use if's, because multiple buttons can, be pressed simultaneously.
     *
     * @param keyPress Incoming from client. Contains if and what button is pressed.
     */
    public void setMovement(KeyPress keyPress) {
        // Set a action where player should be headed.
        if (keyPress.action == KeyPress.Action.LEFT) {
            this.moveLeft = keyPress.pressed;
        }
        if (keyPress.action == KeyPress.Action.RIGHT) {
            this.moveRight = keyPress.pressed;
        }
        if (keyPress.action == KeyPress.Action.UP) {
            this.moveUp = keyPress.pressed;
        }
        if (keyPress.action == KeyPress.Action.DOWN) {
            this.moveDown = keyPress.pressed;
        }
    }

    /**
     * Set new values for mouse.
     *
     * @param leftMouse is left mouse clicked
     * @param mouseXPosition mouse x coordinate
     * @param mouseYPosition mouse y coordinate
     * @param type action that is chosen
     */
    public void setMouseControl(boolean leftMouse, int mouseXPosition, int mouseYPosition, SpellTypes type){
        this.mouseXPosition = mouseXPosition;
        this.mouseYPosition = mouseYPosition;
        this.mouseLeftClick = leftMouse;
        this.type = type;
    }

    /**
     * Pick up item to inventory.
     *
     * @param item item that is picked up
     */
    public void pickUpItem(Item item) {
        inventory.put(item.getId(), item);
    }

    /**
     * Drop item from inventory.
     *
     * @param itemId item's id that is dropped
     * @return item that is dropped
     */
    public Item dropItem(Integer itemId) {
        Item droppedItem = inventory.get(itemId);
        inventory.remove(itemId);
        return droppedItem;
    }

    /**
     * Create player's hit box.
     *
     * @param world world, where hit boxes are in
     */
    public void createHitBox(World world) {
        // Create a dynamic body for the player
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xPosition, yPosition);
        Body hitBoxBody = world.createBody(bodyDef);

        // Create a fixture defining the hit box shape
        PolygonShape hitBoxShape = new PolygonShape();
        hitBoxShape.setAsBox(PlayerCharacter.WIDTH, PlayerCharacter.HEIGHT);

        // Attach the fixture to the body
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = hitBoxShape;
        fixtureDef.density = 1.0f;
        hitBoxBody.createFixture(fixtureDef);

        // Clean up
        hitBoxShape.dispose();

        // Add this object as data
        hitBoxBody.getFixtureList().get(0).setUserData(this);
        body = hitBoxBody;
    }

    /**
     * Remove body.
     */
    public void removeBody(World world) {
        world.destroyBody(body); // Destroy the spells body
        body = null;
    }

    /**
     * Update players position.
     */
    public void updatePosition() {
        // updatePosition is activated every TPS.
        System.out.println(xPosition + " " + yPosition);
        // One key press distance that a character travels.
        int distance = 4;
        // Diagonal movement correction formula.
        int diagonal = (int) (distance / Math.sqrt(2));

        if (moveLeft && moveUp) {
            this.xPosition -= diagonal;
            this.yPosition += diagonal;
        } else if (moveLeft && moveDown) {
            this.xPosition -= diagonal;
            this.yPosition -= diagonal;
        } else if (moveRight && moveUp) {
            this.xPosition += diagonal;
            this.yPosition += diagonal;
        } else if (moveRight && moveDown) {
            this.xPosition += diagonal;
            this.yPosition -= diagonal;
        } else {
            oneWayMovement(distance);
        }
        if (body != null) {
            // Set the position of the Box2D body to match the player's coordinates
            body.setTransform((float) xPosition + 5, (float) yPosition + 25, body.getAngle());
        }
    }

    /**
     * Move player only in one action.
     *
     * @param distance how much to change player coordinates
     */
    private void oneWayMovement(int distance) {
        if (moveLeft) {
            this.xPosition -= distance;
        }
        if (moveRight) {
            this.xPosition += distance;
        }
        if (moveUp) {
            this.yPosition += distance;
        }
        if (moveDown) {
            this.yPosition -= distance;
        }
    }

    /**
     * Regenerate mana.
     */
    public void regenerateMana() {
        if (mana < 100){
            // Add mana every tick so that one second regenerates around 5 mana
            mana = Math.min(mana + 0.1, 100); // Mana can not be over 100
        }
    }
}
