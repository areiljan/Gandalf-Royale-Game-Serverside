package ee.taltech.game.server.messages;

public class UpdateHealth {

    public Integer playerId;
    public Integer health;

    /**
     * Empty constructor for Kryonet.
     */
    public UpdateHealth() {}

    /**
     * Construct UpdateHealth message.
     *
     * @param id player's ID, who's health is updated
     * @param health new health of the player
     */
    public UpdateHealth(Integer id, Integer health) {
        this.playerId = id;
        this.health = health;
    }
}
