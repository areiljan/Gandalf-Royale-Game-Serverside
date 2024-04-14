package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.SpellTypes;

public class SpellPosition {
    public int senderPlayerID;
    public double xPosition;
    public double yPosition;
    public int id;
    public SpellTypes type;

    /**
     * Empty constructor for Kryonet.
     */
    public SpellPosition() {
    }

    /**
     * Construct SpellPosition message.
     *
     * @param senderPlayerID action caster ID
     * @param id action's ID
     * @param xPosition action's x coordinate
     * @param yPosition action's y coordinate
     * @param type action's type
     */
    public SpellPosition(int senderPlayerID, int id, double xPosition, double yPosition, SpellTypes type) {
        this.senderPlayerID = senderPlayerID;
        this.xPosition = xPosition;
        this.yPosition = yPosition;
        this.id = id;
        this.type = type;
    }
}
