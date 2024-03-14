package ee.taltech.server.network.messages.lobby;

public class Leave {
    public Integer gameId; // Game ID that the player wants to leave
    public Integer playerId; // Players ID who wants to leave

    /**
     * Empty constructor for server to fill.
     */
    public Leave() {

    }

    /**
     * Construct leave message.
     *
     * @param gameId given game ID where player leaves
     * @param playerId given player ID who wants to leaves
     */
    public Leave(int gameId, int playerId) {
        this.gameId = gameId;
        this.playerId = playerId;
    }
}