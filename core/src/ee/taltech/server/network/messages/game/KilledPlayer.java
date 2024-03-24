package ee.taltech.server.network.messages.game;

public class KilledPlayer {
    public int id;

    /**
     * Construct KilledPlayer message.
     * This will get rid of a spell.
     * @param id - playerId.
     */
    public KilledPlayer (int id) {
        this.id = id;
    }
}
