package ee.taltech.server.network.messages.game;

public class HealingPotionUsed {
    public Integer playerId;
    public Integer itemId;

    /**
     * Empty constructor for Kryonet.
     */
    public HealingPotionUsed() {
    }

    /**
     * Construct HealingPotionUsed.
     *
     * @param playerId player that use the potion
     * @param itemId potion's id that is used
     */
    public HealingPotionUsed(Integer playerId, Integer itemId) {
        this.playerId = playerId;
        this.itemId = itemId;
    }
}
