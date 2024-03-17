package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.SpellTypes;

public class ItemPickedUp {
    public Integer playerId;
    public Integer itemId;

    public SpellTypes type;

    /**
     * Empty constructor for Kryonet.
     */
    public ItemPickedUp() {
    }

    public ItemPickedUp(Integer playerId, Integer itemId, SpellTypes type) {
        this.playerId = playerId;
        this.itemId = itemId;
        this.type = type;
    }
}
