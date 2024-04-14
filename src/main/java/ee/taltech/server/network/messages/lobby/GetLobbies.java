package ee.taltech.server.network.messages.lobby;

import java.util.List;

public class GetLobbies {
    public String name; // Game name
    public Integer gameId; // Game ID
    public List<Integer> players; // Players in game

    /**
     * Empty constructor for server to fill.
     */
    public GetLobbies() {
    }

    /**
     * Construct GetLobbies message.
     *
     * @param name lobby's name
     * @param gameId lobby's ID
     * @param players player IDs that are in lobby
     */
    public GetLobbies(String name, Integer gameId, List<Integer> players) {
        this.name = name;
        this.gameId = gameId;
        this.players = players;
    }
}
