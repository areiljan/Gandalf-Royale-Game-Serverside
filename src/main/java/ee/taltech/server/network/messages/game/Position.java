package ee.taltech.server.network.messages.game;

public class Position {
    public float xPosition;
    public float yPosition;
    public int userID;

    /**
     * Construct position message.
     *
     * @param userID player's ID
     * @param xPosition player's x coordinate
     * @param yPosition player's y coordinate
     */
    public Position(int userID, float xPosition, float yPosition) {
        this.userID = userID;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
