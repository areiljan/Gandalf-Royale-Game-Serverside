package ee.taltech.game.server.player;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.game.server.messages.KeyPress;
import ee.taltech.game.server.messages.MouseClicks;
import ee.taltech.game.server.messages.MouseClicks.Spell;


public class PlayerCharacter {

    public static final Integer WIDTH = 55;
    public static final Integer HEIGHT = 70;
    private Body body;

    public int xPosition;
    public int yPosition;
    public double mouseXPosition;
    public double mouseYPosition;
    public boolean mouseLeftClick;

    public int playerID;
    public Spell spell;
    boolean moveLeft;
    boolean moveRight;
    boolean moveDown;
    boolean moveUp;
    public Integer health;
    public Integer mana;

    public int getPlayerID() {
        return playerID;
    }

    /**
     * Construct PlayerCharacter.
     *
     * @param playerID player's ID
     */
    public PlayerCharacter(Integer playerID) {
        // Here should be the random spawn points for a PlayerCharacter
        this.xPosition = 0;
        this.yPosition = 0;
        this.playerID = playerID;
        health = 100;
        mana = 100;
    }

    public int getxPosition() {
        return this.xPosition;
    }

    public int getyPosition() {
        return this.yPosition;
    }

    public double getMouseXPosition() {
        return mouseXPosition;
    }

    public double getMouseYPosition() {
        return mouseYPosition;
    }

    public boolean isMouseLeftClick() {
        return this.mouseLeftClick;
    }

    public Spell getSpell() {
        return spell;
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
    public void setMana(Integer newMana) {
        mana = newMana;
    }

    /**
     * Method sets the heading direction for the player, but doesn't update the position coordinates.
     * Only use if's, because multiple buttons can, be pressed simultaneously.
     *
     * @param keyPress Incoming from client. Contains if and what button is pressed.
     */
    public void setMovement(KeyPress keyPress) {
        // Set a direction where player should be headed.
        if (keyPress.direction == KeyPress.Direction.LEFT) {
            this.moveLeft = keyPress.pressed;
        }
        if (keyPress.direction == KeyPress.Direction.RIGHT) {
            this.moveRight = keyPress.pressed;
        }
        if (keyPress.direction == KeyPress.Direction.UP) {
            this.moveUp = keyPress.pressed;
        }
        if (keyPress.direction == KeyPress.Direction.DOWN) {
            this.moveDown = keyPress.pressed;
        }
    }

    public void setMouseControl(boolean leftMouse, double mouseXPosition, double mouseYPosition, Spell spell){
        this.mouseXPosition = mouseXPosition;
        this.mouseYPosition = mouseYPosition;
        this.mouseLeftClick = leftMouse;
        this.spell = spell;
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

        hitBoxBody.setUserData(this);
        this.body = hitBoxBody;
    }

    /**
     * Update players position.
     */
    public void updatePosition() {
        // updatePosition is activated every TPS.

        // One key press distance that a character travels.
        int distance = 8;
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
    }

    /**
     * Move player only in one direction.
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
     *
     * @return true, if regenerate else false
     */
    public boolean regenerateMana() {
        if (mana < 100){
            mana++;
            return true;
        }
        return false;
    }
}
