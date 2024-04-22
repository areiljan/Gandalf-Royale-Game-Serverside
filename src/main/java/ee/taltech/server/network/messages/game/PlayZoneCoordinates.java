package ee.taltech.server.network.messages.game;

public class PlayZoneCoordinates {
    public int firstPlayZoneX;
    public int firstPlayZoneY;
    public int secondPlayZoneX;
    public int secondPlayZoneY;
    public int thirdPlayZoneX;
    public int thirdPlayZoneY;
    public PlayZoneCoordinates(int firstPlayZoneX, int firstPlayZoneY,
                               int secondPlayZoneX, int secondPlayZoneY,
                               int thirdPlayZoneX, int thirdPlayZoneY) {
        this.firstPlayZoneX = firstPlayZoneX;
        this.firstPlayZoneY = firstPlayZoneY;
        this.secondPlayZoneX = secondPlayZoneX;
        this.secondPlayZoneY = secondPlayZoneY;
        this.thirdPlayZoneX = thirdPlayZoneX;
        this.thirdPlayZoneY = thirdPlayZoneY;
    }
}
