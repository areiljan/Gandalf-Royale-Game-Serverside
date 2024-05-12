package ee.taltech.server.world;

import com.badlogic.gdx.physics.box2d.*;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import ee.taltech.server.components.Constants;
import ee.taltech.server.entities.Entity;
import ee.taltech.server.entities.Terrain;
import ee.taltech.server.entities.collision.CollisionBodyTypes;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WorldCollision {

    private final Kryo kryo;
    private boolean collisionsCreated;
    private World world;
    private final int tileHeight;
    private final int tileWidth;
    private ArrayList<MapObjectData> mapObjects;

    /**
     * @param world Box2D world created in GameScreen
     */
    public WorldCollision(World world, Kryo kryo) {
        this.world = world;
        this.kryo = kryo;
        // Change if using different tile sets (measurement: px)
        this.tileHeight = 32;
        this.tileWidth = 32;

        this.mapObjects = loadWorldData(); // Deserialize the objects with kryo.
        lookForObjectTypeAndCreate(mapObjects);
        // Collision creates a flag for loading purposes.
        this.collisionsCreated = false;
    }

    /**
     * Create shapes accordingly from map objects.
     *
     * @param mapObjects         Collision objects read from 'mapdata.bin', which is manually created.
     */
    public void lookForObjectTypeAndCreate(ArrayList<MapObjectData> mapObjects) {
        for (MapObjectData obj : mapObjects) {
            switch (obj.type) {
                case "ellipse":
                    createCircle(obj.x, obj.y, obj.width, obj.tileX, obj.tileY, obj.textureRegionWidth);
                    break;
                case "rectangle":
                    createRectangle(obj.x, obj.y, obj.width, obj.height, obj.tileX, obj.tileY, obj.textureRegionWidth);
                    break;
                case "polygon":
                    createPolygon(obj.vertices, obj.tileX, obj.tileY, obj.textureRegionWidth);
                    break;
                default:
                    throw new IllegalStateException("Unexpected object: " + obj);
            }
        }
        collisionsCreated = true;
        System.out.println("World created");
    }

    /**
     * Creates an Ellipse (Circle) shaped Box2D fixture.
     *
     * @param x                  X coordinate
     * @param y                  Y coordinate
     * @param radius             of Ellipse
     * @param tileX              Number x Tile in the map's x-Axis
     * @param tileY              Number y Tile in the map's y-Axis
     * @param textureRegionWidth Size of the sprite image (If it is 0, that means the cell is flipped).
     */
    private void createCircle(float x, float y, float radius, int tileX, int tileY, float textureRegionWidth) {
        BodyDef bodyDef = getBodyDef(x, y, radius,
                (textureRegionWidth != 0 ? -radius : radius),
                tileX, tileY, textureRegionWidth);
        Body body = world.createBody(bodyDef);
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(radius / 2f / Constants.PPM);

        body.createFixture(circleShape, 0.0f)
                .setUserData(List.of(new Terrain(), CollisionBodyTypes.WORLD_CIRCLE));
        circleShape.dispose(); // Dispose of the shape after use
    }

    /**
     * Creates a Rectangle shaped Box2D object.
     *
     * @param x                  X coordinate
     * @param y                  Y coordinate
     * @param width              of rectangle
     * @param height             of rectangle
     * @param tileX              Number x Tile in the map's x-Axis
     * @param tileY              Number y Tile in the map's y-Axis
     * @param textureRegionWidth Size of the sprite image (If it is 0, that means the cell is flipped).
     */
    private void createRectangle(float x, float y, float width, float height, int tileX, int tileY, float textureRegionWidth) {
        BodyDef bodyDef = getBodyDef(x, y,
                height, (textureRegionWidth != 0 ? -width : width),
                tileX, tileY, textureRegionWidth);
        Body body = world.createBody(bodyDef);
        PolygonShape rectangleShape = new PolygonShape();
        rectangleShape.setAsBox(width / 2f / Constants.PPM, height / 2f / Constants.PPM);
        body.createFixture(rectangleShape, 0.0f)
                .setUserData(List.of(new Terrain(), CollisionBodyTypes.WORLD_RECTANGLE));
        rectangleShape.dispose(); // Dispose of the shape after use
    }

    /**
     * Creates a Polygon shaped Box2D fixture.
     *
     * @param vertices           Corner coordinates of the polygon shape
     * @param tileX              Number x Tile in the map's x-Axis
     * @param tileY              Number y Tile in the map's y-Axis
     * @param textureRegionWidth Size of the sprite image (If it is 0, that means the cell is flipped).
     */
    private void createPolygon(float[] vertices, int tileX, int tileY, float textureRegionWidth) {
        if (vertices.length > 300) {
            System.out.println("Warning: Vertex count really high: " + vertices.length);
        }

        // TextureRegionWidth is not 0, when the cell is flipped.
        if (textureRegionWidth != 0) {
            // Loop through every second vertex, that contain x values.
            for (int i = 0; i < vertices.length; i += 2) {
                // Inverting x-values, because cell is flipped.
                vertices[i] = -vertices[i];
            }
        }

        // Scale vertices using PPM
        float[] scaledVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; i++) {
            scaledVertices[i] = vertices[i] / Constants.PPM;
        }

        // Define the body's location
        BodyDef bodyDef = getBodyDef(0, 0, 0, 0, tileX, tileY, textureRegionWidth);
        Body body = world.createBody(bodyDef);

        // Handle multiple scenarios based on vertex count
        if (vertices.length <= 8) {
            // PolygonShape can have a maximum of 8 vertices.
            PolygonShape polygonShape = new PolygonShape();
            polygonShape.set(scaledVertices);
            body.createFixture(polygonShape, 0.0f)
                    .setUserData(List.of(new Terrain(), CollisionBodyTypes.WORLD_POLYGON));
            polygonShape.dispose(); // Dispose of the shape after use
        } else {
            // Use ChainShape for complex shapes (more than 8 vertices)
            ChainShape chainShape = new ChainShape();
            chainShape.createLoop(scaledVertices);
            body.createFixture(chainShape, 0.0f)
                    .setUserData(List.of(new Terrain(), CollisionBodyTypes.WORLD_CHAIN));
            chainShape.dispose(); // Dispose of the shape after use
        }
    }

    /**
     * Creates BodyDef according to the location of the collision shape.
     *
     * @param x                  Position x of the collision inside the tile
     * @param y                  Position y of the collision inside the tile
     * @param height             Height of the collision
     * @param width              Width of the collision object
     * @param tileX              Number x Tile in the map's x-Axis
     * @param tileY              Number y Tile in the map's y-Axis
     * @param textureRegionWidth Size of the sprite image (If it is 0, that means the cell is flipped).
     * @return BodyDef, meaning body's location
     */
    private BodyDef getBodyDef(float x, float y, float height, float width,
                               int tileX, int tileY, float textureRegionWidth) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        // Calculate offset based on tile coordinates
        float bodyX = (tileX * tileWidth) / Constants.PPM +
                (textureRegionWidth != 0 ? -x / Constants.PPM : x / Constants.PPM)
                + (width / 2f / Constants.PPM) + textureRegionWidth / Constants.PPM;
        float bodyY = (tileY * tileHeight) / Constants.PPM + y / Constants.PPM + (height / 2f / Constants.PPM);

        bodyDef.position.set(bodyX, bodyY);
        bodyDef.fixedRotation = true;

        // The bodies don't act in any physics simulation. They wake up on collision tho.
        bodyDef.allowSleep = true;
        return bodyDef;
    }

    /**
     * @return If the collision creation process has ended.
     */
    public boolean areCollisionsCreated() {
        return collisionsCreated;
    }

    public ArrayList<MapObjectData> loadWorldData() {
        ArrayList<MapObjectData> data = new ArrayList<>();
        try (InputStream inputStream = getClass().getResourceAsStream("/mapdata.bin")) {
            if (inputStream == null) {
                throw new FileNotFoundException("mapdata.bin not found in resources");
            }
            Input input = new Input(inputStream);
            data = kryo.readObject(input, ArrayList.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
