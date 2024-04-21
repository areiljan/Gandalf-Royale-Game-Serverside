package ee.taltech.server.entities;

import com.badlogic.gdx.physics.box2d.*;
import ee.taltech.server.ai.AStarPathFinding;
import ee.taltech.server.ai.Grid;
import ee.taltech.server.ai.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class Mob implements Entity {

    private static final float HIT_BOX_RADIUS= 12; // Should be changed
    private static final float TRIGGERING_RANGE_RADIUS = 320; // Should be changed
    private static final int MOB_HEALTH = 50;
    public static final int MOB_DAMAGE = 15;
    private static final int MAX_PATH_RANGE = 300;

    private final Integer id;
    private Body body;
    private float xPosition;
    private float yPosition;
    private int sourceNodeX;
    private int sourceNodeY;
    private Integer health;
    private AStarPathFinding aStar;
    private final List<PlayerCharacter> playersInRange;
    private List<Node> currentPath;
    private Node nextNode;

    private static Integer currentId = 0;
    /**
     * Get new ID for each mob.
     *
     * @return integers starting from 0
     */
    private static Integer getNewId() {
        return currentId++;
    }

    /**
     * Construct Mob.
     *
     * @param x mob's initial x coordinate
     * @param y mob's initial y coordinate
     */
    public Mob(float x, float y) {
        this.id = getNewId();
        this.body = null;
        this.xPosition = x;
        this.yPosition = y;
        this.sourceNodeX = getSourceNodeX();
        this.sourceNodeY = getSourceNodeY();
        this.health = MOB_HEALTH;

        this.aStar = new AStarPathFinding();
        this. playersInRange = new ArrayList<>();
        this.currentPath = new ArrayList<>();
        this.nextNode = null;
    }

    /**
     * Create mob's body with 3 fixtures.
     *
     * @param world world, where bodies are in
     */
    public void createBody(World world) {
        // Create a dynamic body for the mob
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(xPosition, yPosition);
        Body mobBody = world.createBody(bodyDef);

        // *----- HIT BOX -----*
        // Create a fixture for hit box
        CircleShape hitBoxShape = new CircleShape();
        hitBoxShape.setRadius(HIT_BOX_RADIUS);

        // Attach the fixture to the body
        FixtureDef hitBoxFixtureDef = new FixtureDef();
        hitBoxFixtureDef.shape = hitBoxShape;
        // hitBoxFixtureDef.isSensor = true; // Will work if we change movement from teleporting to vectors
        mobBody.createFixture(hitBoxFixtureDef).setUserData(List.of(this, "Hit_Box"));
        hitBoxShape.dispose(); // Clean up

        // *----- TRIGGERING RANGE -----*
        // Create a fixture for triggering range
        CircleShape triggeringShape = new CircleShape();
        triggeringShape.setRadius(TRIGGERING_RANGE_RADIUS);

        // Attach the fixture to the body
        FixtureDef triggeringFixtureDef = new FixtureDef();
        triggeringFixtureDef.shape = triggeringShape;
        // triggeringFixtureDef.isSensor = true; // Will work if we change movement from teleporting to vectors
        mobBody.createFixture(triggeringFixtureDef).setUserData(List.of(this, "Triggering_Range"));
        triggeringShape.dispose(); // Clean up

        body = mobBody;
    }

    /**
     * Remove body.
     */
    public void removeBody(World world) {
        world.destroyBody(body); // Destroy the mob's body
        body = null;
    }

    /**
     * Get mob's ID.
     *
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Get mob's x coordinate.
     *
     * @return xPosition
     */
    public float getXPosition() {
        return xPosition;
    }

    /**
     * Get mob's y coordinate.
     *
     * @return yPosition
     */
    public float getYPosition() {
        return yPosition;
    }

    /**
     * Get mob's health.
     *
     * @return health
     */
    public Integer getHealth() {
        return health;
    }

    /**
     * Set mob's health.
     *
     * @param newHealth new mob's health
     */
    public void setHealth(Integer newHealth) {
        health = newHealth;
    }

    /**
     * Get current/source node's X value.
     *
     * @return node's X value
     */
    private int getSourceNodeX() {
        return (int) Math.floor(xPosition / 8);
    }

    /**
     * Get current/source node's Y value.
     *
     * @return node's Y value
     */
    private int getSourceNodeY() {
        return (int) Math.floor(yPosition / 8);
    }

    /**
     * Add player to players in range list.
     *
     * @param playerCharacter player that is added
     */
    public void addPlayerInRange(PlayerCharacter playerCharacter) {
        playersInRange.add(playerCharacter);
    }

    /**
     * Remove player from players in range list.
     *
     * @param playerCharacter player that is removed
     */
    public void removePlayerInRange(PlayerCharacter playerCharacter) {
        playersInRange.remove(playerCharacter);
    }

    /**
     * Update mob's position and calculate new path if needed.
     */
    public void updatePosition() {
        // Update source node values
        sourceNodeX = getSourceNodeX();
        sourceNodeY = getSourceNodeY();

        if (!playersInRange.isEmpty()) { // Start following player
            PlayerCharacter firstPlayer = playersInRange.getFirst(); // First player that was in range

            // Get the best path to first enemy that was in range
            currentPath = getPathFromAStar(firstPlayer.getXPosition(), firstPlayer.getYPosition());
        }

        if (currentPath.isEmpty() || currentPath.size() >= MAX_PATH_RANGE) { // No player to follow
            currentPath = chooseRandomPath(); // Make a random path our current one
        }

        // Change to a next node if mob has moved enough
        if (nextNode == null || nextNode.getX() == sourceNodeX && nextNode.getY() == sourceNodeY) {
            // Get and remove (pop) first node from the path
            nextNode = currentPath.getFirst();
            currentPath.removeFirst();
        }

        // Move according to the next nodes position
        if (nextNode.getX() < sourceNodeX && nextNode.getY() < sourceNodeY) { // Move diagonally left down
            move("left_down");
        } else if (nextNode.getX() > sourceNodeX && nextNode.getY() < sourceNodeY) { // Move diagonally right down
            move("right_down");
        } else if (nextNode.getX() < sourceNodeX && nextNode.getY() > sourceNodeY) { // Move diagonally left up
            move("left_up");
        } else if (nextNode.getX() > sourceNodeX && nextNode.getY() > sourceNodeY) { // Move diagonally right up
            move("right_up");
        } else if (nextNode.getX() < sourceNodeX && nextNode.getY() == sourceNodeY) { // Move left
            move("left");
        } else if (nextNode.getX() > sourceNodeX && nextNode.getY() == sourceNodeY) { // Move right
            move("right");
        } else if (nextNode.getX() == sourceNodeX && nextNode.getY() < sourceNodeY) { // Move down
            move("down");
        } else if (nextNode.getX() == sourceNodeX && nextNode.getY() > sourceNodeY) { // Move up
            move("up");
        }
    }

    /**
     * Choose random path that is in the triggering range.
     *
     * @return new chosen random path
     */
    private List<Node> chooseRandomPath() {
        Random random = new Random();
        while (true) { // Try random X and Y values until pathing there is possible
            int randomX = random.nextInt((int) (sourceNodeX - TRIGGERING_RANGE_RADIUS / 8),
                    (int) (sourceNodeX + (TRIGGERING_RANGE_RADIUS / 8) + 1));
            int randomY = random.nextInt((int) (sourceNodeY -  TRIGGERING_RANGE_RADIUS / 8),
                    (int) (sourceNodeY +  (TRIGGERING_RANGE_RADIUS / 8) + 1));

            if (Grid.grid[randomY][randomX] == 0 && randomX != sourceNodeX && randomY != sourceNodeY) {
                List<Node> path = aStar.findPath(sourceNodeX, sourceNodeY, randomX, randomY);
                if (!path.isEmpty() && path.size() < MAX_PATH_RANGE) {
                    return path;
                }
            }
        }
    }

    /**
     * Get a path from A* based on destination coordinates.
     *
     * @param destX destination x coordinate
     * @param destY destination y coordinate
     * @return gotten path
     */
    private List<Node> getPathFromAStar(float destX, float destY) {
        int destNodesX = (int) Math.floor(destX / 8);
        int destNodesY = (int) Math.floor(destY / 8);
        // Check that player's coordinates are not in the map
        if (Grid.grid[destNodesY][destNodesX] == 0) {
            return aStar.findPath(sourceNodeX, sourceNodeY, destNodesX, destNodesY);
        }
        return List.of();
    }

    /**
     * Move in given direction.
     *
     * @param direction given direction as string
     */
    private void move(String direction) {
        if (Objects.equals(direction, "left_down")) {
            xPosition -= 2;
            yPosition -= 2;
        } else if (Objects.equals(direction, "right_down")) {
            xPosition += 2;
            yPosition -= 2;
        } else if (Objects.equals(direction, "left_up")) {
            xPosition -= 2;
            yPosition += 2;
        } else if (Objects.equals(direction, "right_up")) {
            xPosition += 2;
            yPosition += 2;
        } else if (Objects.equals(direction, "left")) {
            xPosition -= 2;
        } else if (Objects.equals(direction, "right")) {
            xPosition += 2;
        } else if (Objects.equals(direction, "down")) {
            yPosition -= 2;
        } else if (Objects.equals(direction, "up")) {
            yPosition += 2;
        }
        body.setTransform(xPosition, yPosition, body.getAngle());
    }
}
