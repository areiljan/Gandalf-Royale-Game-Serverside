package ee.taltech.game.server.messages;

public class FireballPosition {
    public int fireballSenderPlayerID;
    public double fireballXPosition;
    public double fireballYPosition;
    public int fireballID;
    public FireballPosition(int fireballSenderPlayerID, int fireballID, double fireballXposition, double fireballYposition) {
        this.fireballSenderPlayerID = fireballSenderPlayerID;
        this.fireballXPosition = fireballXposition;
        this.fireballYPosition = fireballYposition;
        this.fireballID = fireballID;
    }
}