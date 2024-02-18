package ee.taltech.game.server.messages;

public class Join {
    public Integer gameId; // Game ID that the player wants to join
    public Integer playerId; // Players ID who wants to join

    public Join(){}
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