package ee.taltech.game.server.utilities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.game.server.datamanagement.GameServer;
import ee.taltech.game.server.logic.Fireball;
import ee.taltech.game.server.player.PlayerCharacter;

import java.util.HashMap;
import java.util.Map;

public class Game {

    public final Lobby lobby;
    public final GameServer gameServer;
    public final Integer gameId;
    public final Map<Integer, PlayerCharacter> alivePlayers;
    public final Map<Integer, PlayerCharacter> deadPlayers;
    public final Map<Integer, Fireball> fireballs;

    private final World world;

    /**
     * Construct Game.
     *
     * @param gameServer main GameServer
     * @param lobby given players that will be playing in this game
     */
    public Game(GameServer gameServer, Lobby lobby) {
        world = new World(new Vector2(0, 0), true); // Create a new Box2D world
        CollisionListener collisionListener = new CollisionListener(this);
        world.setContactListener(collisionListener); // Set collision listener that detects collision

        this.gameServer = gameServer;
        this.lobby = lobby;
        this.gameId = lobby.lobbyId;
        this.alivePlayers = createPlayersMap();
        this.deadPlayers = new HashMap<>();
        this.fireballs = new HashMap<>();
    }

    /**
     * Get world where hit boxes exist.
     *
     * @return world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Create Map where all the player objects are in by their ID.
     * Also create hit box for player character.
     *
     * @return players Map
     */
    private Map<Integer, PlayerCharacter> createPlayersMap(){
        Map<Integer, PlayerCharacter> result = new HashMap<>(); // New Map

        for (PlayerCharacter player : gameServer.players.values()) {
            if (lobby.players.contains(player.playerID)) { // If player ID is in lobby's players list
                player.createHitBox(world); // Create hit box for player
                result.put(player.playerID, player);
            }
        }
        return result;
    }

    /**
     * Add new fireball to game.
     *
     * @param fireball new fireball
     */
    public void addFireball(Fireball fireball) {
        fireballs.put(fireball.getFireballID(), fireball);
    }

    /**
     * Damage player and put them into deadPlayers if they have 0 hp
     *
     * @param id player ID
     * @param amount amount of damage done to player
     */
    public void damagePlayer(Integer id, Integer amount) {
        PlayerCharacter player = alivePlayers.get(id); // Get player

        int newHealth = Math.max(player.health - amount, 0); // Health can not be less than 0
        player.setHealth(newHealth); // 10 damage per hit

        // If player has 0 health move them to dead players
        if (player.health == 0) {
            deadPlayers.put(id, player);
        }
    }
}
