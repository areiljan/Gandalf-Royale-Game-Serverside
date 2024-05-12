package ee.taltech.server.network.messages.game;

public class GameOver {
    public Integer winner;

    /**
     * Empty constructor for kryonet.
     */
    public GameOver() {
    }

    /**
     * Construct GameOver message.
     *
     * @param winner player's ID who won
     */
    public GameOver(Integer winner) {
        this.winner = winner;
    }
}
