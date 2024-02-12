package ee.taltech.game.server.messages;

public class KeyPress {
    public enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    public Direction direction;
    public boolean pressed;

    public KeyPress() {

    }

    public KeyPress(Direction direction, boolean pressed) {
        this.direction = direction;
        this.pressed = pressed;
    }
}
