package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.SpellTypes;

public class ItemDropped {

    public Integer playerId;
    public Integer itemId;
    public SpellTypes type;
    public float xPosition;
    public float yPosition;

    /**
     * Empty constructor for Kryonet.
     */
    public ItemDropped() {
    }

    /**
     * Construct ItemDropped message.
     *
     * @param playerId player ID, who dropped item
     * @param itemId item ID, that was dropped
     * @param type item's type
     * @param xPosition item's x coordinate
     * @param yPosition item's y coordinate
     */
    public ItemDropped(Integer playerId, Integer itemId, SpellTypes type, float xPosition, float yPosition) {
        this.playerId = playerId;
        this.itemId = itemId;
        this.type = type;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
    }
}
