package ee.taltech.server;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.DefaultSerializers;
import com.esotericsoftware.kryonet.Server;
import ee.taltech.server.network.ServerListener;
import ee.taltech.server.network.messages.game.*;
import ee.taltech.server.network.messages.lobby.*;
import ee.taltech.server.components.Game;
import ee.taltech.server.components.Lobby;
import ee.taltech.server.components.SpellTypes;

import java.io.IOException;
import java.util.*;

public class GameServer {
    public final Server server;
    public Map<Integer, Integer> connections;
    public Map<Integer, Lobby> lobbies;
    public Map<Integer, Game> games;


    /**
     * Main constructor for the server.
     */
    public GameServer() {
        this.lobbies = new HashMap<>(); // Contains gameIds: lobby
        this.connections = new HashMap<>(); // Contains playerId: gameId
        this.games = new HashMap<>(); // Contains gameIds: game
        this.server = new Server();

        server.start();
        try { // Establishes a connection with ports
            server.bind(8080, 8081);
        } catch (IOException e) {
            throw new NoSuchElementException(e);
        }

        TickRateLoop tickRateLoop = new TickRateLoop(server, this); // Create a running TPS loop.
        Thread tickRateThread = new Thread(tickRateLoop); // Run TPS parallel to other processes.
        tickRateThread.start();

        registerKryos(); // Add sendable data structures.
        server.addListener(new ServerListener(this)); // Creates a new listener, to listen to messages and connections.
    }

    /**
     * Method for creating communication channels with the clients.
     * This should be identical to the client side, else it won't work.
     */
    public void registerKryos() {
        // For registering allowed sendable data objects.
        Kryo kryo = server.getKryo();
        kryo.register(java.util.ArrayList.class);
        kryo.register(PlayZoneCoordinates.class);
        kryo.register(Position.class);
        kryo.register(ActionTaken.class);
        kryo.register(Join.class);
        kryo.register(Leave.class);
        kryo.register(LobbyCreation.class);
        kryo.register(LobbyDismantle.class);
        kryo.register(GetLobbies.class);
        kryo.register(StartGame.class);
        kryo.register(PlayZoneUpdate.class);
        kryo.register(KeyPress.class);
        kryo.register(SpellTypes.class);
        kryo.register(MouseClicks.class);
        kryo.register(KeyPress.Action.class);
        kryo.register(Position.class);
        kryo.register(SpellPosition.class);
        kryo.register(SpellDispel.class);
        kryo.register(UpdateHealth.class);
        kryo.register(KilledPlayer.class);
        kryo.register(UpdateMana.class);
        kryo.register(ItemPickedUp.class);
        kryo.register(ItemDropped.class);
        kryo.addDefaultSerializer(KeyPress.Action.class, DefaultSerializers.EnumSerializer.class);
        kryo.addDefaultSerializer(SpellTypes.class, DefaultSerializers.EnumSerializer.class);
    }

    /**
     * Start server.
     *
     * @param args empty
     */
    public static void main(String[] args) {
        new GameServer();
    }
}
