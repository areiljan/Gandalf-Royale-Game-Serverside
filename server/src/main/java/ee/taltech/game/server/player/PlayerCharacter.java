package ee.taltech.game.server.player;

import ee.taltech.game.server.messages.KeyPress;

public class PlayerCharacter {
    public int xPosition;
    public int yPosition;
    public int playerID;
    boolean moveLeft;
    boolean moveRight;
    boolean moveDown;
    boolean moveUp;
    public Integer health;
    public Integer mana;

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
        health = newMana;
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
}
