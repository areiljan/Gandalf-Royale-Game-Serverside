package ee.taltech.server.network.messages.game;
public class ActionTaken {
    public int userID;
    public final boolean action;
    public final int mouseX;
    public final int mouseY;
    /**
     * Construct action message.
     * Used for fluent animation on enemy characters.
     *
     * @param userID player's ID
     */
    public ActionTaken (int userID, boolean action, int mouseX, int mouseY) {
        this.userID = userID;
        this.action = action;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}
