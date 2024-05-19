package ee.taltech.server.components;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.esotericsoftware.kryo.Kryo;
import ee.taltech.server.GameServer;
import ee.taltech.server.entities.Item;
import ee.taltech.server.entities.Mob;
import ee.taltech.server.entities.Spell;
import ee.taltech.server.entities.PlayerCharacter;
import ee.taltech.server.entities.collision.CollisionListener;
import ee.taltech.server.network.messages.game.*;
import ee.taltech.server.world.WorldCollision;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Game {
    public static final Random random = new Random();

    public final Lobby lobby;
    public final GameServer server;
    public final Integer gameId;

    private final PlayZone playZone;

    public final Map<Integer, PlayerCharacter> gamePlayers;
    private final List<PlayerCharacter> deadPlayers;
    private final List<PlayerCharacter> playersToRemove;

    private final List<Spell> spellsToAdd;
    private final List<Integer> spellsToDispel;
    public final Map<Integer, Spell> spells;

    public final Map<Integer, Item> items;
    private final Map<Item, PlayerCharacter> itemsToAdd;
    private final Map<Item, PlayerCharacter> itemsToRemove;
    private final List<Item> coinsToRemove;

    public final Map<Integer, Mob> mobs;
    public final List<Mob> mobsToAdd;
    public final List<Mob> mobsToRemove;

    private final World world;

    private int staringTicks;
    private int endingTicks;
    private final long startTime;
    private int currentTime;


    /**
     * Construct Game.
     *
     * @param server main GameServer
     * @param lobby given players that will be playing in this game
     */
    public Game(GameServer server, Lobby lobby) {
        world = new World(new Vector2(0, 0), true);
        Kryo kryo = server.server.getKryo();
        new WorldCollision(world, kryo);

        CollisionListener collisionListener = new CollisionListener(this);
        world.setContactListener(collisionListener); // Set collision listener that detects collision

        startTime = System.currentTimeMillis();
        this.currentTime = 0;

        this.server = server;
        this.lobby = lobby;
        this.gameId = lobby.lobbyId;

        this.gamePlayers = createPlayersMap();
        this.playersToRemove = new CopyOnWriteArrayList<>();
        this.deadPlayers = new CopyOnWriteArrayList<>();

        this.spells = new HashMap<>();
        this.spellsToAdd = new CopyOnWriteArrayList<>();
        this.spellsToDispel = new CopyOnWriteArrayList<>();

        this.items = new HashMap<>();
        this.itemsToRemove = new HashMap<>();
        this.itemsToAdd = new HashMap<>();
        this.coinsToRemove = new ArrayList<>();

        this.mobs = new HashMap<>();
        this.mobsToAdd = new CopyOnWriteArrayList<>();
        this.mobsToRemove = new CopyOnWriteArrayList<>();

        this.playZone = new PlayZone();

        this.staringTicks = 0;
        this.endingTicks = 0;
    }

    /**
     * Get how many ticks game have run.
     *
     * @return ticks
     */
    public int getStaringTicks() {
        return staringTicks;
    }

    public int getEndingTicks() {
        return endingTicks;
    }

    /**
     * Send playZone information.
     */
    public void sendPlayZoneCoordinates() {
        for (Integer playerId : lobby.players) {
            server.server.sendToTCP(playerId, new PlayZoneCoordinates(playZone.getFirstZoneX(),
                    playZone.getFirstZoneY(), playZone.getSecondZoneX(),
                    playZone.getSecondZoneY(), playZone.getThirdZoneX(),
                    playZone.getThirdZoneY()));
        }
    }

    /**
     * Add tick to game.
     */
    public void addTick(boolean startingTick) {
        if (startingTick) {
            staringTicks++;
        } else {
            endingTicks++;
        }
    }

    /**
     * Remove spell from the game.
     *
     * @param spellId ID of the spell that will be removed
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
     * Get dead players.
     *
     * @return map of dead players where key is player ID and value PlayerCharacter
     */
    public List<PlayerCharacter> getDeadPlayers() {
        return deadPlayers;
    }

    /**
     * Set player's action based on what key they pressed.
     *
     * @param keyPress keyPress message
     * @param player player who pressed the key
     */
    public void setPlayerAction(KeyPress keyPress, PlayerCharacter player) {
        if (keyPress.action.equals(KeyPress.Action.DROP) && keyPress.extraField != null) {
            Item droppedItem = player.removeItem(keyPress.extraField);
            // Add item to HashMap. Necessary for world stepping.
            itemsToAdd.put(droppedItem, player);
        }
        if (keyPress.action.equals(KeyPress.Action.INTERACT)) {
            for (Item item : items.values()) {
                if (Objects.equals(item.getCollidingWith(), player)) {
                    player.pickUpItem(item);
                    // Add item to HashMap. Necessary for world stepping.
                    itemsToRemove.put(item, player);
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

        for (Integer playerId : lobby.players) {
            server.connections.put(playerId, gameId);
            PlayerCharacter player = new PlayerCharacter(playerId); // Create character for player
            player.createBody(world); // Create hit box for player
            result.put(player.playerID, player);
        }
        return result;
    }

    /**
     * Damage player and put them into deadPlayers if they have 0 hp
     *
     * @param id player ID
     * @param amount amount of damage done to player
     */
    public void damagePlayer(Integer id, float amount) {
        PlayerCharacter player = gamePlayers.get(id); // Get player

        float newHealth = Math.max(player.health - amount, 0); // Health can not be less than 0
        player.setHealth(newHealth); // 10 damage per hit

        // If player has 0 health, then kill them
        if (player.health == 0) {
            killPlayer(player);

        }
    }

    /**
     * Kill player.
     *
     * @param deadPlayer given player that is killed
     */
    private void killPlayer(PlayerCharacter deadPlayer) {
        deadPlayer.stopHealing(); // Stop player's healing if they died
        playersToRemove.add(deadPlayer); // Put player to removing list
        deadPlayers.add(deadPlayer); // Put player to deadPlayer
    }

    /**
     * Heal player.
     *
     * @param playerId player that is healed.
     * @param potionId potion that the player is head with
     */
    public void healPlayer(Integer playerId, Integer potionId) {
        PlayerCharacter player= gamePlayers.get(playerId); // Get player

        player.startHealing(); // Start player's healing
        player.removeItem(potionId); // Remove potion from player

        HealingPotionUsed message = new HealingPotionUsed(playerId, potionId);

        // Notify the client that player used the potion
        server.server.sendToUDP(playerId, message);
    }

    /**
     * Add item to game aka add dropped item.
     *
     * @param item item that is added
     * @param playerCharacter player character that dropped item if player dropped else null
     */
    public void addItem(Item item, PlayerCharacter playerCharacter) {
        if (playerCharacter != null) { // If player is not null aka player dropped item, then update items position
            item.setXPosition(random.nextFloat(
                    ((playerCharacter.getXPosition() + PlayerCharacter.WIDTH / Constants.PPM / 2) - Constants.ITEM_DROP_RANGE),
                    ((playerCharacter.getXPosition() + PlayerCharacter.WIDTH / Constants.PPM / 2) + Constants.ITEM_DROP_RANGE))
            );
            item.setYPosition(random.nextFloat(
                    (playerCharacter.getYPosition() - Constants.ITEM_DROP_RANGE),
                    (playerCharacter.getYPosition() + Constants.ITEM_DROP_RANGE))
            );
        }
        item.createBody(world); // Create body for item
        item.updateBody(); // Update item's body
        items.put(item.getId(), item); // Put it in the items map

        ItemDropped message = createItemDroppedMessage(item, playerCharacter);
        for (Integer playerId : lobby.players) { // Send message for every player in the lobby
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
    private ItemDropped createItemDroppedMessage(Item item, PlayerCharacter playerCharacter) {
        ItemDropped message;
        // If player in not null aka player dropped item send message with player's ID
        if (playerCharacter != null) {
            message = new ItemDropped(playerCharacter.getPlayerID(), item.getId(), item.getType(),
                    item.getXPosition(), item.getYPosition());
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

        ItemPickedUp message;
        // If player is not null aka player picked up item send message with player's ID
        if (playerCharacter != null) {
            message = new ItemPickedUp(playerCharacter.getPlayerID(), item.getId(), item.getType());
        } else { // If player is null aka game removed item send message without player's ID
            message = new ItemPickedUp(null, item.getId(), item.getType());
        }

        for (Integer playerId : gamePlayers.keySet()) {
            server.server.sendToUDP(playerId, message);
        }
    }

    /**
     * Pick up a coin.
     *
     * @param player player that picked the coin up
     * @param coin coin that is picked up
     */
    public void pickUpCoin(PlayerCharacter player, Item coin) {
        coinsToRemove.add(coin); // Add coin to coinsToRemove list
        player.addCoin(); // Give player a coin

        // Create a message that coin was picked up
        CoinPickedUp message = new CoinPickedUp(player.playerID, coin.getId());

        // Send message to every player in this game
        for (Integer playerId : lobby.players) {
            server.server.sendToUDP(playerId, message);
        }
    }

    /**
     * Drop given amount of coins to given position.
     *
     * @param amount amount of coins to drop
     * @param x x position to drop the coins at
     * @param y y position to drop the coins at
     */
    public void dropCoins(Integer amount, float x, float y) {
        for (int i = 0; i < amount; i++) {
            float newX = random.nextFloat((int) (x - Constants.COIN_DROP_RANGE), (int) (x + Constants.COIN_DROP_RANGE));
            float newY = random.nextFloat((int) (y - Constants.COIN_DROP_RANGE), (int) (y + Constants.COIN_DROP_RANGE));
            Item coin = new Item(ItemTypes.COIN, newX, newY);
            itemsToAdd.put(coin, null);
        }
    }

    /**
     * Drop all items from player's inventory.
     *
     * @param playerCharacter given player character
     */
    public void dropAllItems(PlayerCharacter playerCharacter) {
        for (Item item : playerCharacter.getInventory()) {
            addItem(playerCharacter.removeItem(item.getId()), playerCharacter);
        }
    }

    /**
     * Add mob to the game.
     *
     * @param mob mob that is added
     */
    public void addMob(Mob mob) {
        mobsToAdd.add(mob);
    }

    /**
     * Damage mob.
     *
     * @param id mob's ID who is damaged
     * @param amount amount of damage that is done to mob
     */
    public void damageMob(Integer id, float amount) {
        Mob mob = mobs.get(id); // Get mob

        float newHealth = Math.max(mob.getHealth() - amount, 0); // Health can not be less than 0
        mob.setHealth(newHealth);

        // mob is added to mobsToRemove list in TickRateLoop
    }

    /**
     * Remove mob from the game.
     *
     * @param mob mob that is removed
     */
    public void removeMob(Mob mob) {
        mobsToRemove.add(mob);
    }

    /**
     * PlayZone getter.
     * @return - PlayZone.
     */
    public PlayZone getPlayZone() {
        return playZone;
    }

    /**
     * Updating game values.
     */
    public void update() {
        playZone.updateZone(currentTime);
        world.step(1 / 60f, 6, 2); // Stepping world to update bodies
        currentTime = (int) ((System.currentTimeMillis() - startTime) / 1000);

        // *------------- SPELL REMOVING -------------*
        for (Integer spellToDispel : spellsToDispel) {
            if (spells.containsKey(spellToDispel)) {
                spells.get(spellToDispel).removeSpellBody(world);
                for (Integer playerId : gamePlayers.keySet()) {
                    server.server.sendToUDP(playerId, new SpellDispel(spells.get(spellToDispel).getSpellId()));
                }
                spells.remove(spellToDispel);
            }
        }
        spellsToDispel.clear();

        // *------------- SPELL ADDING -------------*
        for (Spell spellToAdd : spellsToAdd) {
            if(!spells.containsValue(spellToAdd)) {
                spellToAdd.createBody(getWorld());
                spells.put(spellToAdd.getSpellId(), spellToAdd);
            }
        }
        spellsToAdd.clear();

        // *------------- PLAYER REMOVING -------------*
        for (PlayerCharacter deadPlayer : playersToRemove) {
            dropCoins(deadPlayer.getCoins(), deadPlayer.getXPosition(), deadPlayer.getYPosition()); // Drop all coins
            dropAllItems(deadPlayer); // Drop all items from player's inventory
            deadPlayer.removeBody(world);

            // this class integer id will be used to send a one-time message to the client.
        }
        playersToRemove.clear();

        // *------------- MOB REMOVING -------------*
        for (Mob mob : mobsToRemove) {
            dropCoins(5, mob.getXPosition(), mob.getYPosition()); // Drop 5 coins
            mob.removeBody(world);
            mobs.remove(mob.getId());
        }
        mobsToRemove.clear();

        // *------------- MOB ADDING -------------*
        for (Mob mob : mobsToAdd) {
            mob.createBody(world);
            mobs.put(mob.getId(), mob);
        }
        mobsToAdd.clear();

        // *------------- COIN REMOVING -------------*
        for (Item coin : coinsToRemove) {
            coin.removeBody(world);
            items.remove(coin.getId());
        }
        coinsToRemove.clear();

        // *------------- ITEM PICKUP -------------*
        for (Map.Entry<Item, PlayerCharacter> itemPlayerCharacterEntry : itemsToAdd.entrySet()) {
            addItem(itemPlayerCharacterEntry.getKey(), itemPlayerCharacterEntry.getValue());
        }
        itemsToAdd.clear();

        // *------------- ITEM DROP -------------*
        for (Map.Entry<Item, PlayerCharacter> itemPlayerCharacterEntry : itemsToRemove.entrySet()) {
            removeItem(itemPlayerCharacterEntry.getKey(), itemPlayerCharacterEntry.getValue());
        }
        itemsToRemove.clear();
    }

    /**
     * End the current game, let the player's know who won and dispose everything.
     */
    public void endGame() {
        if (gamePlayers.size() - deadPlayers.size() == 1) {
            // *-------------- GETTING WINNER ID -------------*
            Set<Integer> difference = new HashSet<>(gamePlayers.keySet());
            difference.removeAll(deadPlayers.stream().map(PlayerCharacter::getPlayerID).collect(Collectors.toSet()));
            Integer winnerId = difference.iterator().next();

            // Send game over message to everyone who is still in the game
            for (Integer playerId : lobby.players) {
                server.server.sendToTCP(playerId, new GameOver(winnerId));
            }
        } else {
            // *-------------- GETTING WINNER ID -------------*
            Integer winnerId = deadPlayers.getLast().playerID; // Get player's ID who was added to dead list later

            // Send game over message to everyone who is still in the game
            for (Integer playerId : lobby.players) {
                server.server.sendToTCP(playerId, new GameOver(winnerId));
            }
        }
    }
}
