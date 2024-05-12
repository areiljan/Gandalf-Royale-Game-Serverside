package ee.taltech.server.components;

import ee.taltech.server.entities.spawner.EntitySpawner;

import java.util.Map;

public class Constants {
    public static final float PPM = 32;

    public static final float PLAYER_MOVEMENT_SPEED = 5;
    public static final float MOB_MOVEMENT_SPEED = 4;

    public static final float STATUS_BAR_WIDTH_PX = 60;

    public static final int FIRST_ZONE_RADIUS = 140;
    public static final int SECOND_ZONE_RADIUS = 76;
    public static final int THIRD_ZONE_RADIUS = 29;

    public static final float BOOK_HIT_BOX_WIDTH = 0.7f;
    public static final float BOOK_HIT_BOX_HEIGHT = 0.7f;
    public static final float COIN_HIT_BOX_WIDTH = 0.2f; // This size is chosen randomly, SHOULD NOT BE FINAL
    public static final float COIN_HIT_BOX_HEIGHT = 0.2f; // This size is chosen randomly, SHOULD NOT BE FINAL
    public static final float POTION_HIT_BOX_WIDTH = 0.5f;
    public static final float POTION_HIT_BOX_HEIGHT = 0.5f;

    public static final float HIT_BOX_RADIUS = 1f; // One cell size

    public static final float TRIGGERING_RANGE_RADIUS = 20f; // Should be changed
    public static final int MOB_HEALTH = 50;
    public static final float MOD_SPIN_ATTACK_DAMAGE = 10;
    public static final int MAX_PATH_RANGE = 300;

    public static final float COIN_DROP_RANGE = 1;
    public static final float ITEM_DROP_RANGE = 0.5f;
    public static final float FIREBALL_SPEED = 6;

    public static final int TICKS_TO_START_GAME = 1000;
    public static final int TICKS_TO_END_GAME = 60;

    public static final float MOB_DMG_PER_TIC = 0.15f;
    public static final float ZONE_DMG_PER_TIC = 0.03f;

    public static final Map<EntitySpawner.ItemType, Float> SPAWN_PROBABILITIES = Map.of(
            EntitySpawner.ItemType.MOB, 0.1f, // Probability of spawning a mob
            EntitySpawner.ItemType.SPELL, 0.8f // Probability of spawning a book
    );

}

