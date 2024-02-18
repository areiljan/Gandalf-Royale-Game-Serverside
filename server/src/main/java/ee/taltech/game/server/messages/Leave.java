package ee.taltech.game.server.messages;

public class Leave {
    public Integer gameId; // Game ID that the player wants to leave
    public Integer playerId; // Players ID who wants to leave

    public Leave(){}
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