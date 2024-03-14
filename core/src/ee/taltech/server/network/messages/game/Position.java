package ee.taltech.server.network.messages.game;

public class Position {
    public int xPosition;
    public int yPosition;
    public int userID;

    /**
     * Construct position message.
     *
     * @param userID player's ID
     * @param xPosition player's x coordinate
     * @param yPosition player's y coordinate
     */
    public Position(int userID, int xPosition, int yPosition) {
        this.userID = userID;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
