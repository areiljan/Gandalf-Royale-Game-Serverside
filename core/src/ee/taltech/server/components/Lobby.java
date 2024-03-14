package ee.taltech.server.components;

import java.util.ArrayList;
import java.util.List;

public class Lobby {
    public final String lobbyName;
    public final List<Integer> players;
    public final Integer lobbyId;
    private static int lastGivenId = 0;

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
        return lastGivenId++;
    }
}
