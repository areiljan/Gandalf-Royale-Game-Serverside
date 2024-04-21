package ee.taltech.server.network.messages.lobby;

public class StartGame {
    public Integer gameId;

    /**
     * Empty constructor for server to fill.
     */
    public StartGame() {
    }

    /**
     * Construct StartGame message.
     *
     * @param gameId given game ID
     */
    public StartGame(Integer gameId) {
        this.gameId = gameId;
    }
}
