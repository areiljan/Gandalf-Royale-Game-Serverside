package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.network.messages.game.KeyPress;
import ee.taltech.server.components.ItemTypes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PlayerCharacter implements Entity {

    public static final Integer WIDTH = 12;
    public static final Integer HEIGHT = 24;
    private static final float HEAL_PER_TICK = 0.17f; // 10hp per sec
    private static final Integer FULL_HEALING_TICKS = 600; // 10 seconds

    private Body body;

    public int xPosition;
    public int yPosition;

    public int mouseXPosition;
    public int mouseYPosition;
    private boolean mouseLeftClick;

    public final int playerID;

    boolean moveLeft;
    boolean moveRight;
    boolean moveDown;
    boolean moveUp;

    public float health;
    public double mana;
    private final Map<Integer, Item> inventory;
    private Integer coins;

    private Integer healingTicks;

    /**
     * Construct PlayerCharacter.
     *
     * @param playerID player's ID
     */
    public PlayerCharacter(Integer playerID) {
        this.playerID = playerID;

        // Here should be the semi-random spawn points for a PlayerCharacter
        this.xPosition = 4700;
        this.yPosition = 5800;

        health = 100;
        mana = 100;
        healingTicks = 0;
        inventory = new HashMap<>();
        coins = 0;
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
     * Get mouse Y position.
     *
     * @return mouseYPosition
     */
    public double getMouseYPosition() {
        return mouseYPosition;
    }

    /**
     * Get player's left mouse click.
     *
     * @return true if left mouse click is pressed else false
     */
    public boolean getMouseLeftClick() {
        return mouseLeftClick;
    }

    /**
     * Set health value.
     *
     * @param newHealth new health value
     */
    public void setHealth(float newHealth) {
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
     * Get player's healing ticks.
     *
     * @return healingTicks
     */
    public Integer getHealingTicks() {
        return healingTicks;
    }

    /**
     * Start player's healing.
     */
    public void startHealing() {
        this.healingTicks = FULL_HEALING_TICKS;
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
    public void setMouseControl(boolean leftMouse, int mouseXPosition, int mouseYPosition, ItemTypes type){
        this.mouseXPosition = mouseXPosition;
        this.mouseYPosition = mouseYPosition;
        this.mouseLeftClick = leftMouse;
    }

    /**
     * Get player's coins.
     *
     * @return integer value of coin amount that player has
     */
    public Integer getCoins() {
        return coins;
    }

    /**
     * Add one coin to player's coins.
     */
    public void addCoin() {
        coins++;
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
     * Remove item from inventory.
     *
     * @param itemId item's id that is removed
     * @return item that is removed
     */
    public Item removeItem(Integer itemId) {
        Item droppedItem = inventory.get(itemId);
        inventory.remove(itemId);
        return droppedItem;
    }

    /**
     * Create player's hit box.
     *
     * @param world world, where hit boxes are in
     */
    public void createBody(World world) {
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
        hitBoxBody.getFixtureList().get(0).setUserData(List.of(this, "Hit_Box"));
        body = hitBoxBody;
    }

    /**
     * Remove body.
     */
    public void removeBody(World world) {
        world.destroyBody(body); // Destroy the player's body
        body = null;
    }

    /**
     * Update players position.
     */
    public void updatePosition() {
        // updatePosition is activated every TPS.
        // One key press distance that a character travels.
        int distance = 3;
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

    /**
     * Regenerate health.
     */
    public void regenerateHealth() {
        if (healingTicks > 0) {
            setHealth(Math.min(health + HEAL_PER_TICK, 100));
            healingTicks--;
        }
    }

    /**
     * Damage player with zone.
     */
    public void receiveZoneDamage() {
        if (health > 0) {
            health = health - 0.03f;
        }
    }
}
