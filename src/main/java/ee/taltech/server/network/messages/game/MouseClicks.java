package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.ItemTypes;

public class MouseClicks {
    public boolean leftMouse;
    public double mouseXPosition;
    public double mouseYPosition;
    public ItemTypes type;

    /**
     * Empty constructor for Kryonet.
     */
    public MouseClicks() {
        // Empty constructor for client to fill.
    }
}
