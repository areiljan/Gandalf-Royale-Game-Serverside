package ee.taltech.server.network.messages.game;

public class UpdateMobHealth {

    public Integer mobId;
    public Integer health;

    /**
     * Empty constructor for Kryonet.
     */
    public UpdateMobHealth() {
    }

    /**
     * Construct UpdateMobHealth message.
     *
     * @param mobId mob's ID
     * @param health mob's new health
     */
    public UpdateMobHealth(Integer mobId, Integer health) {
        this.mobId = mobId;
        this.health = health;
    }
}
