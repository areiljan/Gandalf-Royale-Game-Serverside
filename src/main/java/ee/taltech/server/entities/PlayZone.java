package ee.taltech.server.entities;

import java.awt.geom.Point2D;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

public class PlayZone {
    private int firstZoneX;
    private int firstZoneY;
    private int secondZoneX;
    private int secondZoneY;
    private int thirdZoneX;
    private int thirdZoneY;
    private int timer;
    private int stage;

    public int stage() {
        return stage;
    }

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
        this.stage = 0;
        zoneCoordinateGenerator();
    }

    /**
     * Generates the zoneCoordinates.
     */
    private void zoneCoordinateGenerator () {
        int firstZoneMin = 3085;
        int firstZoneMax = 6515;
        Random random = new Random();
        // Generate a random integer within the specified range
        firstZoneX = random.nextInt(firstZoneMax - firstZoneMin + 1) + firstZoneMax;
        firstZoneY = random.nextInt(firstZoneMax - firstZoneMin + 1) + firstZoneMax;

        int secondZoneXRandomizer = random.nextInt(900 - (-900) + 1) + 900;
        int secondZoneYRandomizer = random.nextInt(900 - (-900) + 1) + 900;
        secondZoneX = firstZoneX + secondZoneXRandomizer;
        secondZoneY = firstZoneY + secondZoneYRandomizer;
        //currently as constant
        thirdZoneX = secondZoneX + 800;
        thirdZoneY = secondZoneY + 800;
    }

    public void updateZone(int startTime) {
        timer = startTime;
        if (timer < 20) {
            stage = 1;
        } else if (timer < 100) {
            stage = 2;
        } else if (timer < 200) {
            stage = 3;
            // implement first zone
            // create body
        } else if (timer < 300) {
            stage = 4;
            // show second zone
        } else if (timer < 400) {
            stage = 5;
            // implement second zone
            // create body
        } else if (timer < 500) {
            stage = 6;
            // show third zone
        } else if (timer < 600) {
            stage = 7;
            // implement third zone
            // create body
            // final countdown
        } else if (timer < 800) {
            stage = 8;
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
