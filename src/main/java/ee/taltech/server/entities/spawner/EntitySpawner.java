package ee.taltech.server.entities.spawner;

import com.badlogic.gdx.math.MathUtils;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.Game;
import ee.taltech.server.components.ItemTypes;
import ee.taltech.server.entities.Item;
import ee.taltech.server.entities.Mob;
import ee.taltech.server.entities.PlayerCharacter;
import ee.taltech.server.entities.Spell;

import java.io.*;
import java.util.*;

public class EntitySpawner {

    private final Game game;
    private List<int[]> coordinates;

    public EntitySpawner(Game game) {
        this.game = game;
        this.coordinates = new ArrayList<>();
        loadCoordinatesFromCSV();

        setPlayerSpawn();
        spawnEntities();

    }


    public enum ItemType {
        MOB, PLAYER, SPELL
    }

    public void spawnEntities() {
        // 2. Spawn items based on probabilities
        for (int[] coord : coordinates) {
            // Skip coordinates where players have already been spawned
            ItemType itemType = getRandomItemType();
            if (itemType != null) {
                spawnItem(coord, itemType);
            }
        }
    }

    public void setPlayerSpawn() {
        Random random = new Random();
        for (PlayerCharacter player : game.gamePlayers.values()) {
            int randomIndex = random.nextInt(coordinates.size());
            int[] spawnPoint = coordinates.remove(randomIndex);
            player.setSpawn(spawnPoint);
        }
    }

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


    private ItemType getRandomItemType() {
        float randomValue = MathUtils.random();
        float cumulativeProbability = 0.0f;
        for (ItemType type : ItemType.values()) {
            cumulativeProbability += Constants.SPAWN_PROBABILITIES.getOrDefault(type, 0.0f);
            if (randomValue <= cumulativeProbability) {
                return type;
            }
        }
        return null; // No item spawned based on probability
    }

    private void spawnItem(int[] coord, ItemType type) {
        switch (type) {
            case MOB:
                Mob mob = new Mob(coord[0], coord[1]);
                game.addMob(mob);
                System.out.println("Mob spawned");
                break;
            case SPELL:
                Item item = new Item(ItemTypes.FIREBALL, coord[0], coord[1]);
                game.addItem(item, null);
                System.out.println("Spell spawned");
                break;
        }
    }
}
