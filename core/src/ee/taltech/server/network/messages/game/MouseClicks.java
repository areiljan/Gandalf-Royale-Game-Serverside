package ee.taltech.server.network.messages.game;

import ee.taltech.server.components.SpellTypes;

public class MouseClicks {
    public boolean leftMouse;
    public double mouseXPosition;
    public double mouseYPosition;
    public SpellTypes type;

    /**
     * Empty constructor for Kryonet.
     */
    public MouseClicks() {
        // Empty constructor for server to fill.
    }
}
