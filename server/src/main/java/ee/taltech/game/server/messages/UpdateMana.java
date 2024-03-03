package ee.taltech.game.server.messages;

public class UpdateMana {

    public Integer playerId;
    public Integer mana;

    /**
     * Empty constructor for Kryonet.
     */
    public UpdateMana() {}

    /**
     * Construct UpdateHealth message.
     *
     * @param id player's ID, who's health is updated
     * @param mana new mana of the player
     */
    public UpdateMana(Integer id, Integer mana) {
        this.playerId = id;
        this.mana = mana;
    }
}
