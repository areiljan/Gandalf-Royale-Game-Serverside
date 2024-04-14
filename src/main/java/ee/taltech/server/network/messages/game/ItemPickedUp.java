package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.ItemTypes;

public class ItemPickedUp {
    public Integer playerId;
    public Integer itemId;

    public ItemTypes type;

    /**
     * Empty constructor for Kryonet.
     */
    public ItemPickedUp() {
    }

    public ItemPickedUp(Integer playerId, Integer itemId, ItemTypes type) {
        this.playerId = playerId;
        this.itemId = itemId;
        this.type = type;
    }
}
