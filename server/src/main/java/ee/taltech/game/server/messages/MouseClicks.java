package ee.taltech.game.server.messages;

public class MouseClicks {
    public enum Spell {
        NOTHING, FIREBALL
    }
    public boolean leftMouse;
    public double mouseXPosition;
    public double mouseYPosition;
    public Spell spell;

    public MouseClicks() {

    }
}