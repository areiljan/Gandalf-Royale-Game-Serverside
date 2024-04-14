package ee.taltech.server.network.messages.lobby;

public class Join {
    public Integer gameId; // Game ID that the player wants to join
    public Integer playerId; // Players ID who wants to join

    /**
     * Empty constructor for server to fill.
     */
    public Join() {

    }

    /**
     * Construct join message.
     *
     * @param gameId given game ID where player joins
     * @param playerId given player ID who wants to join
     */
    public Join(int gameId, int playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }
}
