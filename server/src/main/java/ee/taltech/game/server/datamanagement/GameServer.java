package ee.taltech.game.server.datamanagement;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import ee.taltech.game.server.messages.*;
import ee.taltech.game.server.player.PlayerCharacter;
import ee.taltech.game.server.utilities.Lobby;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class GameServer {
    private int userId;
    private final Server server;
    public Map<Integer, PlayerCharacter> players;

    public Map<Integer, Lobby> lobbies;


    /**
     * Main constructor for the server.
     */
    public GameServer() {
        this.lobbies = new HashMap<>(); // Contains gameIds: lobby
        this.players = new HashMap<>(); // Contains playerId: player
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

    /**
     * Method for listening to clients sent messages/packets.
     * Every incoming packet is acted accordingly and by default thrown an error.
     */
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

            /**
             * If a message is received the method is activated.
             * Every message has its sender connection(ID used mainly) and Data being sent.
             *
             * @param connection Connection with the client.
             * @param incomingData Incoming data from the client.
             */
            @Override
            public void received(Connection connection, Object incomingData) {
                Lobby lobby;
                // Triggers every time data is sent from client to server
                switch (incomingData) {
                    case KeyPress key:
                        PlayerCharacter player = players.get(connection.getID()); // Get the player who sent out the Data.
                        if (player != null) {
                            // Set the direction player should be moving.
                            player.setMovement(key);
                        }
                        break;
                    case LobbyCreation createLobby:
                        Lobby newLobby = new Lobby(createLobby.gameName, createLobby.hostId); // A new lobby is made
                        lobbies.put(newLobby.lobbyId, newLobby); // Lobby is added to the whole lobbies list.
                        server.sendToAllTCP(new LobbyCreation(createLobby.gameName, createLobby.hostId, newLobby.lobbyId));
                        break;
                    case Join joinMessage:
                        // If a player joins a specific lobby shown on the screen.
                        lobby = lobbies.get(joinMessage.gameId); // Get the lobby specified in the message.
                        // Don't add more than 10 players to the lobby.
                        if (lobby.players.size() < 10) {
                            lobby.addPlayer(joinMessage.playerId); // Player is added to the lobby.
                            server.sendToAllTCP(joinMessage);
                        }
                        break;
                    case Leave leaveMessage:
                        // If a player leaves the lobby.
                        lobby = lobbies.get(leaveMessage.gameId); // Get the lobby specified in the message.
                        lobby.removePlayer(leaveMessage.playerId); // Removes the player from the lobby's players list
                        // Check if there are no players left in the lobby.
                        if (lobby.players.isEmpty()) {
                            // Dismantle the lobby
                            lobbies.remove(leaveMessage.gameId); // Removes lobby from the lobbies HashMap.
                            server.sendToAllTCP(new LobbyDismantle(leaveMessage.gameId)); // Send out the removal of a lobby.
                        } else {
                            server.sendToAllTCP(leaveMessage); // Send leave message to everyone connected
                        }
                        break;
                    case GetLobbies ignored:
                        //For every lobby in the HashMap, send out a GetLobbies message.
                        for (Lobby existingLobby : lobbies.values()) {
                            GetLobbies requestedLobby = new GetLobbies(existingLobby.lobbyName, existingLobby.lobbyId, existingLobby.players);
                            server.sendToTCP(connection.getID(), requestedLobby);
                        }
                        break;
                    case FrameworkMessage.KeepAlive ignored:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + incomingData);
                }
            }

            /**
             * Method activates, when a user is disconnected from the server.
             *
             * @param connection Connection with the player, who disconnects.
             */
            @Override
            public void disconnected(Connection connection) {
                // Triggers when client disconnects from the server.
                players.remove(connection.getID()); // Remove player from the HashMap.
                super.disconnected(connection);
            }
        });
    }

    /**
     * Method for creating communication channels with the clients.
     * This should be identical to the client side, else it won't work.
     */
    public void registerKryos() {
        // For registering allowed sendable data objects.
        Kryo kryo = server.getKryo();
        kryo.register(java.util.ArrayList.class);
        kryo.register(Position.class);
        kryo.register(Join.class);
        kryo.register(Leave.class);
        kryo.register(LobbyCreation.class);
        kryo.register(LobbyDismantle.class);
        kryo.register(GetLobbies.class);
        kryo.register(KeyPress.class);
        kryo.register(KeyPress.Direction.class);
        kryo.addDefaultSerializer(KeyPress.Direction.class, DefaultSerializers.EnumSerializer.class);
    }

    public static void main(String[] args) {
        new GameServer();
    }
}
