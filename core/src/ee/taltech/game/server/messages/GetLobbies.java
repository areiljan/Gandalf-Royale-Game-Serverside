package ee.taltech.game.server.messages;

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

    public GetLobbies(String name, Integer gameId, List<Integer> players) {
        this.name = name;
        this.gameId = gameId;
        this.players = players;
    }
}