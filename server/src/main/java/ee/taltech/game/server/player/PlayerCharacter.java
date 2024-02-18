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

    public PlayerCharacter(Integer playerID) {
        // Here should be the random spawn points for a PlayerCharacter
        this.xPosition = 0;
        this.yPosition = 0;
        this.playerID = playerID;
    }

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
        System.out.println("xPosition is " + this.xPosition + " yPosition is " + this.yPosition);
    }
}

