package ee.taltech.server.network.messages.lobby;

public class LobbyDismantle {
    public Integer gameId; // Game ID that will be dismantled

    /**
     * Empty constructor for server to fill.
     */
    public LobbyDismantle() {

    }

    /**
     * Construct lobby dismantle message.
     *
     * @param gameId given game ID that will be dismantled
     */
    public LobbyDismantle(int gameId) {
        this.gameId = gameId;
    }
}
