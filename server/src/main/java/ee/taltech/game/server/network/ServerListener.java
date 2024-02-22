package ee.taltech.game.server.network;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.FrameworkMessage;
import com.esotericsoftware.kryonet.Listener;
import ee.taltech.game.server.datamanagement.GameServer;
import ee.taltech.game.server.messages.*;
import ee.taltech.game.server.player.PlayerCharacter;
import ee.taltech.game.server.utilities.Game;
import ee.taltech.game.server.utilities.Lobby;

public class ServerListener extends Listener {
    private GameServer game;

    /**
     * @param game GameServer, that holds the main data.
     */
    public ServerListener(GameServer game) {
        this.game = game;
    }

    /**
     * Creates a new Player on connection and adds it to the players list.
     * @param connection Contains info of the connection with client.
     */
    @Override
    public void connected(Connection connection) {
        PlayerCharacter player = new PlayerCharacter(connection.getID());
        game.players.put(connection.getID(), player);
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
        Lobby lobby;
        // Triggers every time data is sent from client to server
        switch (incomingData) {
            case KeyPress key:
                PlayerCharacter player = game.players.get(connection.getID()); // Get the player who sent out the Data.

                if (player != null) {
                    // Set the direction player should be moving.
                    player.setMovement(key);
                }
                break;
            case LobbyCreation createLobby:
                Lobby newLobby = new Lobby(createLobby.gameName, createLobby.hostId); // A new lobby is made
                game.lobbies.put(newLobby.lobbyId, newLobby); // Lobby is added to the whole lobbies list.
                game.server.sendToAllTCP(new LobbyCreation(createLobby.gameName, createLobby.hostId, newLobby.lobbyId));
                break;
            case Join joinMessage:
                // If a player joins a specific lobby shown on the screen.
                lobby = game.lobbies.get(joinMessage.gameId); // Get the lobby specified in the message.
                // Don't add more than 10 players to the lobby.
                if (lobby.players.size() < 10) {
                    lobby.addPlayer(joinMessage.playerId); // Player is added to the lobby.
                    game.server.sendToAllTCP(joinMessage);
                }
                break;
            case Leave leaveMessage:
                // If a player leaves the lobby.
                lobby = game.lobbies.get(leaveMessage.gameId); // Get the lobby specified in the message.
                lobby.removePlayer(leaveMessage.playerId); // Removes the player from the lobby's players list
                // Check if there are no players left in the lobby.
                if (lobby.players.isEmpty()) {
                    // Dismantle the lobby
                    game.lobbies.remove(leaveMessage.gameId); // Removes lobby from the lobbies HashMap.
                    game.server.sendToAllTCP(new LobbyDismantle(leaveMessage.gameId)); // Send out the removal of a lobby.
                } else {
                    game.server.sendToAllTCP(leaveMessage); // Send leave message to everyone connected
                }
                break;
            case GetLobbies ignored:
                //For every lobby in the HashMap, send out a GetLobbies message.
                for (Lobby existingLobby : game.lobbies.values()) {
                    GetLobbies requestedLobby = new GetLobbies(existingLobby.lobbyName, existingLobby.lobbyId, existingLobby.players);
                    game.server.sendToTCP(connection.getID(), requestedLobby);
                }
                break;
            case StartGame startGame:
                lobby = game.lobbies.get(startGame.gameId);
                if (lobby.players.size() > 1) { // If there are more than 1 player in lobby
                    for (Integer playerId : lobby.players) {
                        game.server.sendToTCP(playerId, startGame); // Start game for players
                    }

                    // Create new game instance and add it to games list in GameServer
                    game.games.put(lobby.lobbyId, new Game(game, lobby));
                    game.lobbies.remove(startGame.gameId); // Remove lobby from gameServer lobby's list
                    game.server.sendToAllTCP(new LobbyDismantle(startGame.gameId)); // Remove lobby for clients
                }
                break;
            case FrameworkMessage.KeepAlive ignored:
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + incomingData);
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
        game.players.remove(connection.getID()); // Remove player from the HashMap.
        super.disconnected(connection);
    }
}

