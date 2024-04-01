package ee.taltech.server.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.server.GameServer;
import ee.taltech.server.components.SpellTypes;
import ee.taltech.server.entities.Spell;
import ee.taltech.server.network.messages.game.KeyPress;
import ee.taltech.server.network.messages.game.MouseClicks;
import ee.taltech.server.network.messages.lobby.*;
import ee.taltech.server.entities.PlayerCharacter;
import ee.taltech.server.components.Game;
import ee.taltech.server.components.Lobby;

public class ServerListener extends Listener {
    private GameServer server;

    /**
     * @param server GameServer, that holds the main data.
     */
    public ServerListener(GameServer server) {
        this.server = server;
    }

    /**
     * Creates a new Player on connection and adds it to the players list.
     * @param connection Contains info of the connection with client.
     */
    @Override
    public void connected(Connection connection) {
        server.connections.put(connection.getID(), -1); // When player connects they have no game ID yet so -1
    }

    /**
     * If a message is received the method is activated.
     * Every message has its sender connection(ID used mainly) and Data being sent.
     *
     * @param connection   Connection with the client.
     * @param incomingData Incoming data from the client.
     */
    @Override
    public void received(Connection connection, Object incomingData) {
        Class<?> dataClass = incomingData.getClass();
        if (dataClass == KeyPress.class || dataClass == MouseClicks.class) {
            gameMessagesListener(connection, incomingData); // If message is associated with game
        } else if (dataClass == LobbyCreation.class || dataClass == Join.class || dataClass == Leave.class
                || dataClass == GetLobbies.class || dataClass == StartGame.class) {
            lobbyMessagesListener(connection, incomingData); // If message is associated with lobby
        }
    }

    /**
     * Listen and react to game messages.
     *
     * @param connection connection that sent the message
     * @param incomingData message that was sent
     */
    private void gameMessagesListener(Connection connection, Object incomingData) {
        Integer gameId = server.connections.get(connection.getID());
        Game game = server.games.get(gameId);
        PlayerCharacter player = game.gamePlayers.get(connection.getID());
        switch (incomingData) {
            case KeyPress key: // On KeyPress message
                if (player != null) {
                    // Set the direction player should be moving.
                    if (key.action.equals(KeyPress.Action.UP) || key.action.equals(KeyPress.Action.DOWN)
                            || key.action.equals(KeyPress.Action.LEFT) ||  key.action.equals(KeyPress.Action.RIGHT)) {
                        player.setMovement(key);
                    } else {
                        game.setPlayerAction(key, player);
                    }
                }
                break;
            case MouseClicks mouse: // On MouseClicks message
                if (player != null) {
                    // Set the direction player should be moving.
                    player.setMouseControl(mouse.leftMouse, (int) mouse.mouseXPosition, (int) mouse.mouseYPosition, mouse.type);
                    // Add new fireball
                    if (mouse.type != SpellTypes.NOTHING && mouse.leftMouse && player.mana >= 20) {
                        // Add new fireball to the game
                        Spell spell = new Spell(player, mouse.mouseXPosition, mouse.mouseYPosition, game.getWorld(),
                                mouse.type);
                        game.addSpell(spell);
                        // Action cost
                        player.setMana(player.mana - 20);
                    }
                }
                break;
            default: // Ignore everything else
                break;
        }
    }

    /**
     * Listen and react to lobby messages.
     *
     * @param connection connection that sent the message
     * @param incomingData message that was sent
     */
    private void lobbyMessagesListener(Connection connection, Object incomingData) {
        Lobby lobby;
        // Triggers every time data is sent from client to server
        switch (incomingData) {
            case LobbyCreation createLobby:
                Lobby newLobby = new Lobby(createLobby.gameName, createLobby.hostId); // A new lobby is made
                server.lobbies.put(newLobby.lobbyId, newLobby); // Lobby is added to the whole lobbies list.
                server.server.sendToAllTCP(new LobbyCreation(createLobby.gameName, createLobby.hostId, newLobby.lobbyId));
                break;
            case Join joinMessage:
                // If a player joins a specific lobby shown on the screen.
                lobby = server.lobbies.get(joinMessage.gameId); // Get the lobby specified in the message.
                // Don't add more than 10 players to the lobby.
                if (lobby.players.size() < 10) {
                    lobby.addPlayer(joinMessage.playerId); // Player is added to the lobby.
                    server.server.sendToAllTCP(joinMessage);
                }
                break;
            case Leave leaveMessage:
                // If a player leaves the lobby.
                lobby = server.lobbies.get(leaveMessage.gameId); // Get the lobby specified in the message.
                lobby.removePlayer(leaveMessage.playerId); // Removes the player from the lobby's players list
                // Check if there are no players left in the lobby.
                if (lobby.players.isEmpty()) {
                    // Dismantle the lobby
                    server.lobbies.remove(leaveMessage.gameId); // Removes lobby from the lobbies HashMap.
                    server.server.sendToAllTCP(new LobbyDismantle(leaveMessage.gameId)); // Send out the removal of a lobby.
                } else {
                    server.server.sendToAllTCP(leaveMessage); // Send leave message to everyone connected
                }
                break;
            case GetLobbies ignored:
                //For every lobby in the HashMap, send out a GetLobbies message.
                for (Lobby existingLobby : server.lobbies.values()) {
                    GetLobbies requestedLobby = new GetLobbies(existingLobby.lobbyName,
                            existingLobby.lobbyId, existingLobby.players);
                    server.server.sendToTCP(connection.getID(), requestedLobby);
                }
                break;
            case StartGame startGame:
                lobby = server.lobbies.get(startGame.gameId);
                if (lobby.players.size() > 1) { // If there are more than 1 player in lobby
                    for (Integer playerId : lobby.players) {
                        server.server.sendToTCP(playerId, startGame); // Start game for players
                    }

                    // Create new game instance and add it to games list in GameServer
                    server.games.put(lobby.lobbyId, new Game(server, lobby));
                    server.lobbies.remove(startGame.gameId); // Remove lobby from gameServer lobby's list
                    server.server.sendToAllTCP(new LobbyDismantle(startGame.gameId)); // Remove lobby for clients
                }
                break;
            default:
                break;
        }
    }

    /**
     * Removes the player from the players list.
     * Makes the client-server connection disappear from the listener.
     * @param connection Connection with the client.
     */
    @Override
    public void disconnected(Connection connection) {
        // Triggers when client disconnects from the server.
        server.connections.remove(connection.getID()); // Remove player from the HashMap.
        super.disconnected(connection);
    }
}

