package ee.taltech.server.network.messages.game;

public class KeyPress {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    public Direction direction;
    public boolean pressed;

    /**
     * Empty constructor for Kryonet.
     */
    public KeyPress() {
    }

    /**
     * Construct key press message.
     *
     * @param direction where player wants to move
     * @param pressed if key was pressed or realised
     */
    public KeyPress(Direction direction, boolean pressed) {
        this.direction = direction;
        this.pressed = pressed;
    }
}
