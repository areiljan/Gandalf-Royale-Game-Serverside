package ee.taltech.game.server.messages;

public class FireballPosition {
    public int senderPlayerID;
    public double xPosition;
    public double yPosition;
    public int id;

    public FireballPosition() {
    }

    public FireballPosition(int senderPlayerID, int id, double xPosition, double yPosition) {
        this.senderPlayerID = senderPlayerID;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.id = id;
    }
}