package ee.taltech.server.network.messages.game;

public class KeyPress {
    public enum Action {
        UP, DOWN, LEFT, RIGHT, INTERACT, DROP
    }
    public Action action;
    public boolean pressed;
    public Integer extraField;

    /**
     * Empty constructor for Kryonet.
     */
    public KeyPress() {
        // Empty constructor for client to fill.
    }
}
