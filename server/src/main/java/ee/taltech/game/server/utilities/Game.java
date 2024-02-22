package ee.taltech.game.server.utilities;

import ee.taltech.game.server.datamanagement.GameServer;
import ee.taltech.game.server.player.PlayerCharacter;

import java.util.HashMap;
import java.util.Map;

public class Game {

    public final Lobby lobby;
    public final GameServer gameServer;
    public final Integer gameId;
    public final Map<Integer, PlayerCharacter> players;

    /**
     * Construct Game.
     *
     * @param gameServer main GameServer
     * @param lobby given players that will be playing in this game
     */
    public Game(GameServer gameServer, Lobby lobby) {
        this.gameServer = gameServer;
        this.lobby = lobby;
        this.gameId = lobby.lobbyId;
        this.players = createPlayersMaps();
    }

    /**
     * Create players Map.
     *
     * @return players Map
     */
    private Map<Integer, PlayerCharacter> createPlayersMaps(){
        Map<Integer, PlayerCharacter> result = new HashMap<>(); // New Map

        for (PlayerCharacter player : gameServer.players.values()) {
            if (lobby.players.contains(player.playerID)) { // If player ID is in lobby's players list
                result.put(player.playerID, player);
            }
        }
        return result;
    }
}
