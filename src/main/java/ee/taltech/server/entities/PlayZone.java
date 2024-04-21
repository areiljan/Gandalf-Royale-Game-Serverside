package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.*;

import java.util.List;
import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.random;

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
    private static int FIRST_ZONE_RADIUS = 4447; // in pixels lmao
    private static int SECOND_ZONE_RADIUS = 2420;
    private static int THIRD_ZONE_RADIUS = 945;

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
     * Create PlayZone hit box.
     *
     * @param world world where the fireball body is created
     */
    public void createBody(World world) {
        // Create fireball body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((float) firstZoneX, (float) firstZoneY); // Initial position
        Body body = world.createBody(bodyDef);

        // Create fixture for fireball hit box
        CircleShape shape = new CircleShape();
        shape.setRadius(FIRST_ZONE_RADIUS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef);

        // Dispose shape
        shape.dispose();

        // Set user data to identify fireball
        body.getFixtureList().get(0).setUserData(List.of(this, "Hit_Box"));
        zoneBody = body;
    }

    /**
     * Resize and relocate the PlayZone hit box.
     *
     * @param newSize    new radius for the circle shape
     * @param newPositionX new X coordinate for the center of the circle
     * @param newPositionY new Y coordinate for the center of the circle
     */
    public void resizeAndRelocate(float newSize, float newPositionX, float newPositionY) {
        // Set new position
        zoneBody.setTransform(newPositionX, newPositionY, zoneBody.getAngle());

        // Get the existing fixture
        Fixture fixture = zoneBody.getFixtureList().get(0);

        // Get the existing shape
        CircleShape shape = (CircleShape) fixture.getShape();

        // Change the shape
        shape.setRadius(newSize);
    }

    public void updateZone(int startTime) {
        timer = startTime;
        stage = 0;
        if (timer > 20 && timer < 60) {
            // *---first marker---*
            stage = 1;
        } else if (timer < 80) {
            // *--- first zone---*
            stage = 2;
            // implement first zone
            createBody(world);
        } else if (timer < 100) {
            // *--- second marker---*
            stage = 3;
        } else if (timer < 130) {
            // *--- second zone---*
            stage = 4;
            resizeAndRelocate(SECOND_ZONE_RADIUS, secondZoneX, secondZoneY);
        } else if (timer < 160) {
            // *--- third marker---*
            stage = 5;
        } else if (timer < 180) {
            // *---third zone---*
            stage = 6;
            stage = 4;
            resizeAndRelocate(THIRD_ZONE_RADIUS, thirdZoneX, thirdZoneY);
        } else if (timer < 200) {
            stage = 7;
            // implement third zone
            // create body
            // final countdown
        } else if (timer < 350) {
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
