package ee.taltech.server.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import ee.taltech.server.GameServer;
import ee.taltech.server.entities.Item;
import ee.taltech.server.entities.PlayZone;
import ee.taltech.server.entities.Spell;
import ee.taltech.server.entities.PlayerCharacter;
import ee.taltech.server.entities.collision.CollisionListener;
import ee.taltech.server.network.messages.game.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Game {

    public final Lobby lobby;
    public final GameServer server;
    public final Integer gameId;
    public final Map<Integer, PlayerCharacter> gamePlayers; // Previous alivePlayers
    private final PlayZone playZone;
    private int killedPlayerId;
    public Map<Integer, PlayerCharacter> deadPlayers;
    private ArrayList<Spell> spellsToAdd;
    private ArrayList<Integer> spellsToDispel;
    public Map<Integer, Spell> spells;
    public final Map<Integer, Item> items;
    private final World world;
    private long startTime;
    private int currentTime;


    /**
     * Construct Game.
     *
     * @param server main GameServer
     * @param lobby given players that will be playing in this game
     */
    public Game(GameServer server, Lobby lobby) {
        world = new World(new Vector2(0, 0), true); // Create a new Box2D world
        CollisionListener collisionListener = new CollisionListener(this);
        world.setContactListener(collisionListener); // Set collision listener that detects collision
        startTime = System.currentTimeMillis();
        this.currentTime = 0;

        this.server = server;
        this.lobby = lobby;
        this.gameId = lobby.lobbyId;
        this.gamePlayers = createPlayersMap();
        this.deadPlayers = new HashMap<>();
        this.spells = new HashMap<>();
        this.items = new HashMap<>();
        this.spellsToDispel = new ArrayList<>();
        this.spellsToAdd = new ArrayList<>();
        this.killedPlayerId = 0;
        this.playZone = new PlayZone();
    }


    /**
     * Basically.
     */
    public void update() {
        world.step(1 / 60f, 6, 2); // Stepping world to update bodies
        currentTime = (int) ((System.currentTimeMillis() - startTime) / 1000);
        for (Integer spellToDispel : spellsToDispel) {
            if (spells.containsKey(spellToDispel)) {
                spells.get(spellToDispel).removeSpellBody(world);
                for (Integer playerId : gamePlayers.keySet()) {
                    server.server.sendToUDP(playerId, new SpellDispel(spells.get(spellToDispel).getSpellId()));
                }
                spells.remove(spellToDispel);
            }
        }
        for (Spell spellToAdd : spellsToAdd) {
            System.out.println(spellToAdd.getSpellId() + "x:" + spellToAdd.getSpellXPosition() + "y: " + spellToAdd.getSpellYPosition() + " ID:" + spellToAdd.getPlayerId());
            if(!spells.containsValue(spellToAdd)) {
                spells.put(spellToAdd.getSpellId(), spellToAdd);
            }
        }
        // resets the id to 0 each iteration.
        killedPlayerId = 0;
        for (PlayerCharacter deadPlayer : deadPlayers.values()) {
            deadPlayer.removeBody(world);
            // this class integer id will be used to send a one-time message to the client.
            killedPlayerId = deadPlayer.getPlayerID();
        }
        spellsToDispel.clear();
        spellsToAdd.clear();
        deadPlayers.clear();
    }

    /**
     * Add a spell to dispel.
     */
    public void removeSpell(Integer spellId) {
        spellsToDispel.add(spellId);
    }

    /**
     * Add new spell to game.
     *
     * @param spellToAdd new spells Id.
     */
    public void addSpell(Spell spellToAdd) {
        spellsToAdd.add(spellToAdd);
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
     * Set player's action based on what key they pressed.
     *
     * @param keyPress keyPress message
     * @param player player who pressed the key
     */
    public void setPlayerAction(KeyPress keyPress, PlayerCharacter player) {
        if (keyPress.action.equals(KeyPress.Action.DROP) && keyPress.extraField != null) {
            Item droppedItem = player.dropItem(keyPress.extraField);
            addItem(droppedItem, player);
        }
        if (keyPress.action.equals(KeyPress.Action.INTERACT)) {
            for (Item item : items.values()) {
                if (Objects.equals(item.getCollidingWith(), player)) {
                    player.pickUpItem(item);
                    removeItem(item, player);
                    break;
                }
            }
        }
    }

    /**
     * Create Map where all the player objects are in by their ID.
     * Also create hit box for player character.
     *
     * @return players Map
     */
    private Map<Integer, PlayerCharacter> createPlayersMap(){
        Map<Integer, PlayerCharacter> result = new HashMap<>(); // New Map

        for (Integer playerID : server.connections.keySet()) {
            if (lobby.players.contains(playerID)) { // If player ID is in lobby's players list
                server.connections.put(playerID, gameId);
                PlayerCharacter player = new PlayerCharacter(playerID); // Create character for player
                player.createHitBox(world); // Create hit box for player
                result.put(player.playerID, player);
            }
        }
        return result;
    }


    /**
     * Damage player and put them into deadPlayers if they have 0 hp
     *
     * @param id player ID
     * @param amount amount of damage done to player
     */
    public void damagePlayer(Integer id, Integer amount) {
        PlayerCharacter player = gamePlayers.get(id); // Get player

        int newHealth = Math.max(player.health - amount, 0); // Health can not be less than 0
        player.setHealth(newHealth); // 10 damage per hit

        // If player has 0 health move them to dead players
        if (player.health == 0) {
            deadPlayers.put(id, player);
        }
    }

    /**
     * Add item to game aka add dropped item.
     *
     * @param item item that is added
     * @param playerCharacter player character that dropped item if player dropped else null
     */
    public void addItem(Item item, PlayerCharacter playerCharacter) {
        if (playerCharacter != null) { // If player is not null aka player dropped item, then update items position
            item.setXPosition(playerCharacter.getXPosition());
            item.setYPosition(playerCharacter.getYPosition());
        }
        item.createBody(world); // Create body for item
        item.updateBody(); // Update item's body
        items.put(item.getId(), item); // Put it in the items map

        for (Integer playerId : gamePlayers.keySet()) { // Send message for every player in the lobby
            ItemDropped message = createItemDropped(item, playerCharacter);
            server.server.sendToUDP(playerId, message);
        }
    }

    /**
     * Create item dropped message.
     *
     * @param item item, that is dropped
     * @param playerCharacter player character that dropped item if player dropped else null
     * @return ItemDropped message
     */
    private ItemDropped createItemDropped(Item item, PlayerCharacter playerCharacter) {
        ItemDropped message;
        // If player in not null aka player dropped item send message with player's ID
        if (playerCharacter != null) {
            message = new ItemDropped(playerCharacter.getPlayerID(), item.getId(), item.getType(),
                    playerCharacter.getXPosition(), playerCharacter.getYPosition());
        } else { // If player is null aka game dropped item then send message without player's ID
            message = new ItemDropped(null, item.getId(), item.getType(),
                    item.getXPosition(), item.getYPosition());
        }
        return message;
    }

    /**
     * Remove item from the game aka pick it up from the ground.
     *
     * @param item item that is removed
     * @param playerCharacter player character that picked item up if player picked up else null
     */
    public void removeItem(Item item, PlayerCharacter playerCharacter) {
        item.removeBody(world); // Remove items body
        items.remove(item.getId()); // Remove item form items map

        for (Integer playerId : gamePlayers.keySet()) {
            ItemPickedUp message;
            // If player is not null aka player picked up item send message with player's ID
            if (playerCharacter != null) {
                message = new ItemPickedUp(playerCharacter.getPlayerID(), item.getId(), item.getType());
            } else { // If player is null aka game removed item send message without player's ID
                message = new ItemPickedUp(null, item.getId(), item.getType());
            }
            server.server.sendToUDP(playerId, message);
        }
    }

    /**
     * Get the killed player id.
     * Zero if no killed players this tick.
     * @return - 0 or an id.
     */
    public int getKilledPlayerId() {
        return killedPlayerId;
    }

    /**
     * startTime getter.
     * @return - startTime as long.
     */
    public int getCurrentTime() {
        return currentTime;
    }

    /**
     * PlayZone getter.
     * @return - PlayZone.
     */
    public PlayZone getPlayZone() {
        return playZone;
    }
}
