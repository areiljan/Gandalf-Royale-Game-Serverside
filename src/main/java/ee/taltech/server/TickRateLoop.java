package ee.taltech.server;

import com.esotericsoftware.kryonet.Server;
import ee.taltech.server.components.Constants;
import ee.taltech.server.components.Lobby;
import ee.taltech.server.entities.*;
import ee.taltech.server.entities.spawner.EntitySpawner;
import ee.taltech.server.network.messages.game.*;

import ee.taltech.server.components.Game;
import ee.taltech.server.network.messages.lobby.Leave;
import ee.taltech.server.network.messages.lobby.LobbyDismantle;

import java.util.Map;

public class TickRateLoop implements Runnable {
    private volatile boolean running = true;
    private Server server;
    private GameServer gameServer;

    /**
     * @param server The whole gameServer instance to access the servers contents.
     */
    public TickRateLoop(Server server, GameServer gameServer) {
        this.server = server;
        this.gameServer = gameServer;
        // Whole list for the players in the list.
    }

    /**
     * The main loop of the TPS method.
     * This loop is run TPS/s.
     */
    public void run() {
        // TPS means ticks per second.
        double tps = 60;
        // Time since last tick.
        double delta = 0;
        // Time since last update
        long lastTime = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            // Logic to implement TPS in a second.
            delta += (now - lastTime) / (1000000000.0 / tps);
            lastTime = now;
            while (delta >= 1) {
                // Tick() to send out a tick calculation.
                tick();
                delta--;
            }
        }
    }

    /**
     * Method is called out every tick (in run() method).
     * Contains logic that needs to be updated every tick.
     */
    public void tick() {
        // *--------------- REMOVE CONNECTION ---------------*
        for (Integer connection : gameServer.connectionsToRemove) {
            gameServer.connections.remove(connection);
        }
        gameServer.connectionsToRemove.clear();

        // *--------------- REMOVE LOBBIES ---------------*
        for (Integer lobbyId : gameServer.lobbiesToRemove) {
            gameServer.lobbies.remove(lobbyId);
        }
        gameServer.lobbiesToRemove.clear();

        // *--------------- REMOVE GAMES ---------------*
        for (Integer gameId : gameServer.gamesToRemove) {
            gameServer.games.remove(gameId);
        }
        gameServer.gamesToRemove.clear();

        // *--------------- REMOVE PLAYERS FROM LOBBIES ---------------*
        for (Map.Entry<Integer, Lobby> lobbyEntry : gameServer.playersToRemoveFromLobbies.entrySet()) {
            Integer playerId = lobbyEntry.getKey();
            Lobby lobby = lobbyEntry.getValue();
            lobby.removePlayer(playerId);

            if (lobby.players.isEmpty()) {
                gameServer.lobbies.remove(lobby.lobbyId);
                server.sendToAllTCP(new LobbyDismantle(lobby.lobbyId));
            } else {
                server.sendToAllTCP(new Leave(lobby.lobbyId, playerId));
            }
        }
        gameServer.playersToRemoveFromLobbies.clear();

        // *--------------- GAME LOOPS ---------------*
        float deltaTime = 1.0f / 60f;
        for (Game game : gameServer.games.values()) {
            // Item spawning to the world
            if (game.getStaringTicks() <= Constants.TICKS_TO_START_GAME) game.addTick(true);
            if (game.getStaringTicks() == Constants.TICKS_TO_START_GAME) { // Trigger only once
                new EntitySpawner(game);
                game.sendPlayZoneCoordinates();
            }

            // End the game
            if (game.gamePlayers.size() - game.getDeadPlayers().size() <= 1) { // If last player is alive
                // Let the game finish it's logic before ending
                if (game.getEndingTicks() < Constants.TICKS_TO_END_GAME) game.addTick(false);
                if (game.getEndingTicks() == Constants.TICKS_TO_END_GAME) {
                    game.endGame(); // End the game
                    gameServer.gamesToRemove.add(game.gameId); // Remove the game from the sever
                    continue; // Skip updating
                }
            }

            for (PlayerCharacter player : game.gamePlayers.values()) {
                if (!game.getDeadPlayers().contains(player)) {
                    player.updatePosition();
                }
                if (player.getCollidingWithMob()) {
                    game.damagePlayer(player.playerID, Constants.MOB_DMG_PER_TIC);
                }
                if (player.mana != 100) {
                    player.regenerateMana();
                }
                if (player.getHealingTicks() > 0) {
                    player.regenerateHealth();
                }
                if (!game.getPlayZone().areCoordinatesInZone((int) player.getXPosition(), (int) player.getYPosition())
                        && !game.getDeadPlayers().contains(player)) {
                    game.damagePlayer(player.getPlayerID(), Constants.ZONE_DMG_PER_TIC);
                }
                for (Integer playerId : game.lobby.players) {
                    server.sendToUDP(playerId, new Position(player.playerID, player.getXPosition(), player.getYPosition()));
                    server.sendToUDP(playerId, new UpdateHealth(player.playerID, (int) player.health));
                    server.sendToUDP(playerId, new UpdateMana(player.playerID, player.mana));
                    server.sendToUDP(playerId, new PlayZoneUpdate(game.getPlayZone().getTimer(), game.getPlayZone().stage()));
                    server.sendToUDP(playerId, new ActionTaken(player.playerID, player.getMouseLeftClick(),
                            game.gamePlayers.get(player.playerID).mouseXPosition,
                            game.gamePlayers.get(player.playerID).mouseYPosition));
                }
            }
            for (Spell spell : game.spells.values()) {
                spell.updatePosition(deltaTime);

                // Remove spells that are out of the world
                if (0 > spell.getSpellXPosition() || spell.getSpellXPosition() > 300
                        || 0 > spell.getSpellYPosition() || spell.getSpellYPosition() > 300) {
                    game.removeSpell(spell.getSpellId());
                }
                for (Integer playerId : game.lobby.players) {
                    server.sendToUDP(playerId, new SpellPosition(spell.getPlayerId(), spell.getSpellId(),
                                spell.getSpellXPosition(), spell.getSpellYPosition(), spell.getType()));
                }
            }
            for (Mob mob : game.mobs.values()) {
                mob.updatePosition();

                if (!game.getPlayZone().areCoordinatesInZone((int) mob.getXPosition(), (int) mob.getYPosition())
                        && mob.getHealth() > 0) {
                    game.damageMob(mob.getId(), Constants.ZONE_DMG_PER_TIC);
                }

                for (Integer playerId : game.lobby.players) {
                    server.sendToUDP(playerId, new MobPosition(mob.getId(), mob.getXPosition(), mob.getYPosition()));
                    server.sendToUDP(playerId, new UpdateMobHealth(mob.getId(), (int) mob.getHealth()));
                }

                if (mob.getHealth() == 0) { // Check if mob is dead
                    game.removeMob(mob);
                }
            }
            game.update();
        }
    }
}
