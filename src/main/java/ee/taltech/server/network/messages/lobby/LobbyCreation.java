package ee.taltech.server.network.messages.lobby;

public class LobbyCreation {
    public String gameName; // Game name that is displayed in lobby screen
    public Integer gameId; // Game ID
    public Integer hostId; // Lobby creator aka Lobby host ID

    /**
     * Empty constructor for server to fill.
     */
    public LobbyCreation() {
    }

    /**
     * Construct game creation message.
     *
     * @param gameName given game name by game creator
     * @param hostId   game creators ID
     */
    public LobbyCreation(String gameName, Integer hostId, Integer gameId) {
        this.gameName = gameName;
        this.hostId = hostId;
        this.gameId = gameId;
    }
}
