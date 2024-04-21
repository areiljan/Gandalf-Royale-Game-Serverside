package ee.taltech.server.network.messages.game;

public class MobPosition {

    public Integer mobId;
    public float xPosition;
    public float yPosition;

    /**
     * Empty constructor for Kryonet.
     */
    public MobPosition() {
    }

    /**
     * Construct MobPosition message.
     *
     * @param mobId mob's ID
     * @param xPosition mob's x coordinate
     * @param yPosition mob's y coordinate
     */
    public MobPosition(Integer mobId, float xPosition, float yPosition) {
        this.mobId = mobId;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
