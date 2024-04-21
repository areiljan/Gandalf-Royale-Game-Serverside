package ee.taltech.server.network.messages.game;

public class CoinPickedUp {
    public Integer playerId;
    public Integer coinId;

    /**
     * Empty constructor for Kryonet.
     */
    public CoinPickedUp() {
    }

    public CoinPickedUp(Integer playerId, Integer coinId) {
        this.playerId = playerId;
        this.coinId = coinId;
    }
}
