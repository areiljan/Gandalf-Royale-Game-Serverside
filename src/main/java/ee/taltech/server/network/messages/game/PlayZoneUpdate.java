package ee.taltech.server.network.messages.game;

public class PlayZoneUpdate {
    public int timer;
    public int stage;

    /**
     * Update PlayZone Radius.
     * @param timer - new game timer.
     */
    public PlayZoneUpdate(int timer, int stage) {
        this.stage = stage;
        this.timer = timer;
    }
}
