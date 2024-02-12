package ee.taltech.game.server.messages;

public class Position {
    public int xPosition;
    public int yPosition;
    public int userID;
    public Position(int userID, int xPosition, int yPosition) {
        this.userID = userID;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
