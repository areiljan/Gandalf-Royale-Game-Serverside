package ee.taltech.game.server.messages;

public class LobbyDismantle {
    public Integer gameId; // Game ID that will be dismantled

    public LobbyDismantle(){}
    /**
     * Construct lobby dismantle message.
     *
     * @param gameId given game ID that will be dismantled
     */
    public LobbyDismantle(int gameId) {
        this.gameId = gameId;
    }
}