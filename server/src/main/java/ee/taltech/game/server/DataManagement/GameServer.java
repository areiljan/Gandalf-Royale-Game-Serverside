package ee.taltech.game.server.DataManagement;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class GameServer {
    private int userId;
    private final Server server;
    private final Map<Integer, Object> gameObjects;

    private GameServer() {
        this.gameObjects = new HashMap<>();
        this.server = new Server();
        server.start();
        try { // Establishes a connection with ports
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new NoSuchElementException(e);
        }
        Kryo kryo = server.getKryo();
        kryo.register(HashMap.class);
        server.addListener(new Listener() { // Adding a mediator between the client and the server
            @Override
            public void connected(Connection connection) { // Checks the connection one time
                int assignedId = LoginControl.assignId(connection.getID());
                if (assignedId != -1) {
                    userId = assignedId;
                }
                // Assigning an ID, if the lobby is full the ID will not be added to the array of players.
                // The UserID will be -1.
                // It is crucial for this step to be connected with the lobby system.
            }

            @Override
            public void received(Connection connection, Object object) { // Triggers every time data is sent from client to server
                if (object instanceof String) {
                    gameObjects.put(userId, object);
                }
                server.sendToAllUDP(gameObjects);
            }
        });
    }
    public static void main(String[] args) {
        new GameServer();
    }
}


