package ee.taltech.server.network.messages.game;

public class PlayZoneUpdate {
    public int radius;

    /**
     * Update PlayZone Radius.
     * @param radius - new radius of zone.
     */
    public PlayZoneUpdate(int radius) {
        this.radius = radius;
    }
}
