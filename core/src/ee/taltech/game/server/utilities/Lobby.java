package ee.taltech.game.server.utilities;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    public String lobbyName;
    public List<Integer> players;
    public Integer lobbyId;
    private static int lastGivenId = -1;

    public Lobby(String lobbyName, Integer hostId) {
        this.lobbyName = lobbyName;
        this.players = new ArrayList<>();
        this.lobbyId = getAndIncrementNextId();
        this.players.add(hostId);
    }

    /**
     * @param playerId Player you want to add to the lobby.
     */
    public void addPlayer(Integer playerId) {
        this.players.add(playerId);
    }

    /**
     * @param playerId Player you want to remove from the lobby.
     */
    public void removePlayer(Integer playerId) {
        this.players.remove(playerId);
    }

    /**
     * @return gameID, that will be in ascending order. Every game/lobby gets its new ID.
     */
    private static int getAndIncrementNextId() {
        return ++lastGivenId;
    }
}
