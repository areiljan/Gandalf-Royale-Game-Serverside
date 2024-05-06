package ee.taltech.server.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.components.Constants;
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
    public float xPosition;
    public float yPosition;
    public int mouseXPosition;
    public int mouseYPosition;
    private boolean mouseLeftClick;

    public final int playerID;

    private Vector2 movement;

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
        // Here should be the semi-random spawn points for a PlayerCharacter
        this.xPosition = 8200 / Constants.PPM;
        this.yPosition = 2960 / Constants.PPM;

        this.movement = new Vector2(0f, 0f);
        this.playerID = playerID;
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
     * @return Player x position
     */
    public float getXPosition() {
        if (body == null) {
            return 0;
        }
        return body.getPosition().x;
    }

    /**
     * @return Player y position
     */
    public float getYPosition() {
        if (body == null) {
            return 0;
        }
        return body.getPosition().y;
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
     * Only use if's, because multiple buttons can be pressed simultaneously.
     *
     * @param keyPress Incoming from a client. Contains if and what button is pressed.
     */
    public void setMovement(KeyPress keyPress) {
        // Set an action where player should be headed.
        if (keyPress.pressed) {
            if (keyPress.action == KeyPress.Action.LEFT) {
                movement.x = -1;
            } else if (keyPress.action == KeyPress.Action.RIGHT) {
                movement.x = 1;
            } else if (keyPress.action == KeyPress.Action.UP) {
                movement.y = 1;
            } else if (keyPress.action == KeyPress.Action.DOWN) {
                movement.y = -1;
            }
        } else {
            // Only cancel the movement if one key is pressed down.
            if (keyPress.action == KeyPress.Action.LEFT && movement.x < 0) {
                movement.x = 0;
            } else if (keyPress.action == KeyPress.Action.RIGHT && movement.x > 0) {
                movement.x = 0;
            } else if (keyPress.action == KeyPress.Action.UP && movement.y > 0) {
                movement.y = 0;
            } else if (keyPress.action == KeyPress.Action.DOWN && movement.y < 0) {
                movement.y = 0;
            }
        }
    }

    /**
     * Update players position.
     */
    public void updatePosition() {
        // updatePosition is activated every TPS.
        // One key press distance that a character travels.
        Vector2 scaledMovement = movement.cpy().scl(Constants.movementSpeed);
        float maxSpeed = Constants.movementSpeed * (float) Math.sqrt(2);
        scaledMovement.clamp(maxSpeed, maxSpeed);

        if (body != null) {
            body.setLinearVelocity(scaledMovement);
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
        bodyDef.fixedRotation = true;
        Body playerBody = world.createBody(bodyDef);

        // Create a fixture defining the hit box shape
        PolygonShape hitBoxShape = new PolygonShape();
        hitBoxShape.setAsBox(PlayerCharacter.WIDTH / Constants.PPM, PlayerCharacter.HEIGHT / Constants.PPM);

        CircleShape collisionCircle = new CircleShape();
        float circleRadius = 10 / Constants.PPM;
        collisionCircle.setRadius(circleRadius);
        collisionCircle.setPosition(new Vector2(0f, -(PlayerCharacter.HEIGHT / Constants.PPM) + circleRadius));

        FixtureDef fixtureDefCollisionCircle = new FixtureDef();
        fixtureDefCollisionCircle.shape = collisionCircle;
        fixtureDefCollisionCircle.density = 0.0f;

        // Attach the fixture to the body
        FixtureDef fixtureDefHitbox = new FixtureDef();
        fixtureDefHitbox.shape = hitBoxShape;
        fixtureDefHitbox.density = 1.0f;
        fixtureDefHitbox.isSensor = true;


        playerBody.createFixture(fixtureDefHitbox);
        playerBody.createFixture(fixtureDefCollisionCircle);

        // Clean up
        hitBoxShape.dispose();
        collisionCircle.dispose();

        // Add this object as data
        playerBody.getFixtureList().get(0).setUserData(List.of(this, "Hit_Box"));
        body = playerBody;
    }

    /**
     * Remove body.
     */
    public void removeBody(World world) {
        world.destroyBody(body); // Destroy the player's body
        body = null;
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
