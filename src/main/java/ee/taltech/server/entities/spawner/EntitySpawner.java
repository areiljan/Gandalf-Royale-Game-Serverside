package ee.taltech.server.entities.spawner;

import com.badlogic.gdx.math.MathUtils;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.Game;
import ee.taltech.server.components.ItemTypes;
import ee.taltech.server.entities.Item;
import ee.taltech.server.entities.Mob;
import ee.taltech.server.entities.PlayerCharacter;

import java.io.*;
import java.util.*;

public class EntitySpawner {

    private final Game game;
    private List<int[]> coordinates;

    public EntitySpawner(Game game) {
        this.game = game;
        this.coordinates = new ArrayList<>();

        loadCoordinatesFromCSV(); // Load handpicked coords.

        setPlayerSpawn(); // Move players to the right positions. Needs to be before items spawn.
        spawnEntities(); // Spawn items by probability

    }


    public enum EntityType {
        MOB, ITEM
    }

    /**
     * Spawn entities randomly
     */
    public void spawnEntities() {
        // 2. Spawn items based on probabilities
        for (int[] coord : coordinates) {
            // Skip coordinates where players have already been spawned
            EntityType itemType = getRandomEntityType();
            if (itemType != null) {
                spawnItem(coord, itemType);
            }
        }
    }

    /**
     * Spawn players and remove coords accordingly.
     * This makes the items and players not spawn on top of each other.
     */
    public void setPlayerSpawn() {
        Random random = new Random();
        for (PlayerCharacter player : game.gamePlayers.values()) {
            int randomIndex = random.nextInt(coordinates.size());
            int[] spawnPoint = coordinates.remove(randomIndex);
            player.setSpawn(spawnPoint);
        }
    }

    /**
     * Load the coords into an array.
     */
    private void loadCoordinatesFromCSV() {
        try (InputStream inputStream = getClass().getResourceAsStream("/spawn_points.csv");
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {
            coordinates.addAll(br.lines()
                    .map(line -> line.split(","))
                    .map(values -> Arrays.stream(values).mapToInt(Integer::parseInt).toArray())
                    .peek(row -> row[1] = 299 - row[1]) // Modify the y-coordinate in place
                    .toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @return Type of item, that is going to be spawned.
     */
    private EntityType getRandomEntityType() {
        float randomValue = MathUtils.random();
        float cumulativeProbability = 0.0f;
        for (EntityType type : EntityType.values()) {
            cumulativeProbability += Constants.SPAWN_PROBABILITIES.getOrDefault(type, 0.0f);
            if (randomValue <= cumulativeProbability) {
                return type;
            }
        }
        return null; // No item spawned based on probability
    }

    /**
     * @return Type of item, that is going to be spawned.
     */
    private ItemTypes getRandomItemType() {
        float randomValue = MathUtils.random();
        float cumulativeProbability = 0.0f;
        for (ItemTypes type : ItemTypes.values()) {
            cumulativeProbability += Constants.SPAWN_PROBABILITIES_ITEMS.getOrDefault(type, 0.0f);
            if (randomValue <= cumulativeProbability) {
                return type;
            }
        }
        return null; // No item spawned based on probability
    }


    /**
     * Actually add the items to the game.
     *
     * @param coord of spawn position
     * @param type of item/mob that will be spawned.
     */
    private void spawnItem(int[] coord, EntityType type) {
        switch (type) {
            case MOB:
                Mob mob = new Mob(coord[0], coord[1]);
                game.addMob(mob);
                System.out.println("Mob spawned");
                break;
            case ITEM:
                Item item = new Item(getRandomItemType(), coord[0], coord[1]);
                game.addItem(item, null);
                System.out.println("Spell spawned");
                break;
        }
    }
}
