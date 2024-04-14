package ee.taltech.server.entities;

import java.awt.geom.Point2D;
import java.util.Random;

public class PlayZone {
    private int firstZoneX;
    private int firstZoneY;
    private int secondZoneX;
    private int secondZoneY;
    private int thirdZoneX;
    private int thirdZoneY;
    private int timer;

    /**
     * Game PlayZone constructor.
     */
    public PlayZone() {
        this.timer = 0; // all calculations currently in pixels
        this.firstZoneX = 0;
        this.firstZoneY = 0;
        this.secondZoneX = 0;
        this.secondZoneY = 0;
        this.thirdZoneX = 0;
        this.thirdZoneY = 0;
        zoneCoordinateGenerator();
    }

    /**
     * Generates the zoneCoordinates.
     */
    private void zoneCoordinateGenerator () {
        int firstZoneMin = -2000;
        int firstZoneMax = 300;
        Random random = new Random();
        // Generate a random integer within the specified range
        firstZoneX = random.nextInt(firstZoneMax - firstZoneMin + 1) + firstZoneMax;
        firstZoneY = random.nextInt(firstZoneMax - firstZoneMin + 1) + firstZoneMax;
        // leaving as constants for now
        secondZoneX = firstZoneX + 800;
        secondZoneY = firstZoneY + 800;
        thirdZoneX = secondZoneX + 800;
        thirdZoneY = secondZoneY + 800;
    }

    /**
     * Updates the zone based on time.
     * @param startTime - startTime in seconds.
     */
    public void updateZone(int startTime) {
        timer = startTime;
        System.out.println(timer);
        if (timer < 30) {
            // do nothing
        } else if (timer < 80) {
            // show first zone
        } else if (timer < 200) {
            // implement first zone
            // create body
        } else if (timer < 250) {
            // show second zone
        } else if (timer < 350) {
            // implement second zone
            // create body
        } else if (timer < 450) {
            // show third zone
        } else if (timer < 550) {
            // implement third zone
            // create body
            // final countdown
        } else if (timer < 800) {
            // the entire map turns red
            // create body
        }
    }

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
