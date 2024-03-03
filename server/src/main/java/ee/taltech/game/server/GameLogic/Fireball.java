package ee.taltech.game.server.GameLogic;

import ee.taltech.game.server.player.PlayerCharacter;

public class Fireball {
    private String quartile;
    private double angle;
    int characterXPosition;
    int characterYPosition;
    int playerID;
    int fireballID = 0;
    double fireballXPosition;
    double fireballYPosition;
    double mouseXPosition;
    double mouseYPosition;
    private static int nextId = 1;
    int VELOCITY = 3;


    public Fireball(PlayerCharacter playerCharacter, double MouseXPosition, double MouseYPosition) {
        this.playerID = playerCharacter.getPlayerID();
        this.fireballXPosition = playerCharacter.getxPosition();
        this.fireballYPosition = playerCharacter.getyPosition();
        // These mousepositions are already relative to the player.
        this.mouseXPosition = MouseXPosition;
        this.mouseYPosition = MouseYPosition;
        // Adjust the velocity of the fireball.
        this.VELOCITY = 5;
        this.fireballID = nextId++;
        this.angle = Math.atan2(mouseYPosition, mouseXPosition);
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
        this.fireballXPosition += VELOCITY * Math.cos(angle);
        this.fireballYPosition -= VELOCITY * Math.sin(angle);
    }

    public double getFireballXPosition() {return this.fireballXPosition;}
    public double getFireballYPosition() {return this.fireballYPosition;}
}
