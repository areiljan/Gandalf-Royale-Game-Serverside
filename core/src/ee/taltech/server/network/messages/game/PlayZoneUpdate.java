package ee.taltech.server.network.messages.game;

public class PlayZoneUpdate {
    public int timer;

    /**
     * Update PlayZone Radius.
     * @param timer - new game timer.
     */
    public PlayZoneUpdate(int timer) {
        this.timer = timer;
    }
}
