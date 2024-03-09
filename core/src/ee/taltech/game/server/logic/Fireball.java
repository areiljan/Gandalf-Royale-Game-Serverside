package ee.taltech.game.server.logic;

import ee.taltech.game.server.player.PlayerCharacter;

public class Fireball {
    private double angle;
    int playerID;
    int fireballID;
    double fireballXPosition;
    double fireballYPosition;
    double mouseXPosition;
    double mouseYPosition;
    private static int nextId = 1;
    private int velocity;


    public Fireball(PlayerCharacter playerCharacter, double mouseXPosition, double mouseYPosition) {
        playerID = playerCharacter.getPlayerID();
        fireballXPosition = playerCharacter.getxPosition();
        fireballYPosition = playerCharacter.getyPosition();
        // These mousepositions are already relative to the player.
        this.mouseXPosition = mouseXPosition;
        this.mouseYPosition = mouseYPosition;
        // Adjust the velocity of the fireball.
        fireballID = nextId++;
        velocity = 3;
        angle = Math.atan2(mouseYPosition, mouseXPosition);
    }

    public double getAngle() {
        return this.angle;
    }

    public int getFireballID() {
        return fireballID;
    }

    public int getPlayerID() {
        return playerID;
    }

    public void updatePosition() {
        // Update fireball position based on angle and velocity
        this.fireballXPosition += velocity * Math.cos(angle);
        this.fireballYPosition -= velocity * Math.sin(angle);
    }

    public double getFireballXPosition() {return this.fireballXPosition;}
    public double getFireballYPosition() {return this.fireballYPosition;}
}
