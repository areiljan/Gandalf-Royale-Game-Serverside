package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.*;

import java.util.List;
import java.util.Random;

import static java.lang.Math.sqrt;

public class PlayZone {
    private final World world;
    private Body zoneBody;
    private int firstZoneX;
    private int firstZoneY;
    private int secondZoneX;
    private int secondZoneY;
    private int thirdZoneX;
    private int thirdZoneY;
    private int timer;
    private int stage;
    private static int FIRST_ZONE_RADIUS = 4450; // in pixels lmao
    private static int SECOND_ZONE_RADIUS = 2430;
    private static int THIRD_ZONE_RADIUS = 930;

    public int stage() {
        return stage;
    }

    /**
     * Game PlayZone constructor.
     */
    public PlayZone(World world) {
        this.world = world;
        this.timer = 0; // all calculations currently in pixels
        zoneCoordinateGenerator();
        this.stage = 0;
    }

    /**
     * Generates the coordinates for the zone center points.
     * Currently turning the randomness off to not screw with testing.
     */
    private void zoneCoordinateGenerator () {
        int firstZoneMin = 3400;
        int firstZoneMax = 6000;
        Random random = new Random();
        // Generate a random integer within the specified range
        firstZoneX = random.nextInt(firstZoneMax - firstZoneMin + 1) + firstZoneMin;
        firstZoneY = random.nextInt(firstZoneMax - firstZoneMin + 1) + firstZoneMin;
        int secondZoneXRandomizer = random.nextInt(1500 - (-1500) + 1) + (-1500);
        int secondZoneYRandomizer = random.nextInt(1500 - (-1500) + 1) + (-1500);
        secondZoneX =  firstZoneX + secondZoneXRandomizer;
        secondZoneY = firstZoneY + secondZoneYRandomizer;
        int thirdZoneXRandomizer = random.nextInt(1600 - (-1600) + 1) + (-1600);
        int thirdZoneYRandomizer = random.nextInt(1600 - (-1600) + 1) + (-1600);
        thirdZoneX = secondZoneX + thirdZoneXRandomizer;
        thirdZoneY = secondZoneY + thirdZoneYRandomizer;
    }

    /**
     * Update the zone stages based on the elapsed time.
     * The zone is obviously sped up right now.
     * @param startTime - the time since game start.
     */
    public void updateZone(int startTime) {
        timer = startTime;
        if (timer > 30 && timer < 60) {
            // *---first marker---*
            stage = 1;
        } else if (timer < 120) {
            // *--- first zone---*
            stage = 2;
            // implement first zone
        } else if (timer < 150) {
            // *--- second marker---*
            stage = 3;
        } else if (timer < 210) {
            // *--- second zone---*
            stage = 4;
        } else if (timer < 240) {
            // *--- third marker---*
            stage = 5;
        } else if (timer < 300) {
            // *---third zone---*
            // final countdown
            stage = 6;
        } else if (timer < 500) {
            stage = 7;
            // entire map turns red - not implemented yet.
        }
    }

    /**
     * Are the specified coordinates in the zone right now.
     * @param x - the x coordinate.
     * @param y - the y coordinate.
     * @return - true if in the zone.
     */
    public boolean areCoordinatesInZone(int x, int y) {
        if (stage <= 1) {
            return true;
        } else if (stage <= 3) {
            int distanceFromMidPoint = (int) sqrt(Math.pow(x - firstZoneX, 2) + (Math.pow(firstZoneY - y, 2)));
            return (distanceFromMidPoint < FIRST_ZONE_RADIUS);
        } else if (stage <= 5) {
            int distanceFromMidPoint = (int) sqrt(Math.pow(x - secondZoneX, 2) + (Math.pow(secondZoneY - y, 2)));
            return (distanceFromMidPoint < SECOND_ZONE_RADIUS);
        } else {
            int distanceFromMidPoint = (int) sqrt(Math.pow(x - thirdZoneX, 2) + (Math.pow(thirdZoneY - y, 2)));
            return (distanceFromMidPoint < THIRD_ZONE_RADIUS);
        }
    }

    /**
     * Timer getter.
     * @return - elapsed time since game start.
     */
    public Integer getTimer() {
        return (int) timer;
    }

    /**
     * First zone X getter.
     * @return - first zone X.
     */
    public int getFirstZoneX() {
        return firstZoneX;
    }

    /**
     * First zone Y getter.
     * @return - first zone Y.
     */
    public int getFirstZoneY() {
        return firstZoneY;
    }

    /**
     * Second zone X getter.
     * @return - second zone X.
     */
    public int getSecondZoneX() {
        return secondZoneX;
    }

    /**
     * Second zone Y getter.
     * @return - second zone Y.
     */
    public int getSecondZoneY() {
        return secondZoneY;
    }

    /**
     * Third zone X getter.
     * @return - third zone X.
     */
    public int getThirdZoneX() {
        return thirdZoneX;
    }

    /**
     * Third zone Y getter.
     * @return - third zone Y.
     */
    public int getThirdZoneY() {
        return thirdZoneY;
    }
}
