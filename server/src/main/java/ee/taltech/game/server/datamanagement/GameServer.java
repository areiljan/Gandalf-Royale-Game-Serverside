package ee.taltech.game.server.datamanagement;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import ee.taltech.game.server.messages.KeyPress;
import ee.taltech.game.server.messages.Position;
import ee.taltech.game.server.player.PlayerCharacter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class GameServer {
    private int userId;
    private final Server server;
    public Map<Integer, PlayerCharacter> players = new HashMap<>();

    private GameServer() {
        this.server = new Server();
        server.start();
        try { // Establishes a connection with ports
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new NoSuchElementException(e);
        }

        TickRateLoop tickRateLoop = new TickRateLoop(server, players); // Create a running TPS loop.
        Thread tickRateThread = new Thread(tickRateLoop); // Run TPS parallel to other processes.
        tickRateThread.start();

        registerKryos(); // Add sendable data structures.
        addListener(); // Listen to incoming data
    }

    public void addListener() {
        server.addListener(new Listener() { // Adding a mediator between the client and the server
            @Override
            public void connected(Connection connection) { // Checks the connection one time
                int assignedId = LoginControl.assignId(connection.getID());
                if (assignedId != -1) {
                    userId = assignedId;
                    PlayerCharacter player = new PlayerCharacter(userId);
                    players.put(connection.getID(), player);

                }
                // Assigning an ID, if the lobby is full the ID will not be added to the array of players.
                // The UserID will be -1.
                // It is crucial for this step to be connected with the lobby system.
            }

            @Override
            public void received(Connection connection, Object object) {
                // Triggers every time data is sent from client to server
                if (object instanceof KeyPress key) {
                    PlayerCharacter player = players.get(connection.getID());
                    if (player != null) {
                        // Set the direction player should be moving.
                        player.setMovement(key);
                    }
                }
            }

            @Override
            public void disconnected(Connection connection) {
                // Triggers when client disconnects from the server.
                players.remove(connection.getID()); // Remove player from the HashMap.
                super.disconnected(connection);
            }
        });
    }

    public void registerKryos() {
        // For registering allowed sendable data objects.
        Kryo kryo = server.getKryo();
        kryo.register(HashMap.class);
        kryo.register(KeyPress.class);
        kryo.register(KeyPress.Direction.class);
        kryo.register(Position.class);
        kryo.addDefaultSerializer(KeyPress.Direction.class, DefaultSerializers.EnumSerializer.class);
    }

    public static void main(String[] args) {
        new GameServer();
    }
}


