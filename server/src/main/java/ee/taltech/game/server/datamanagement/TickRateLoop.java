package ee.taltech.game.server.datamanagement;

import com.esotericsoftware.kryonet.Server;
import ee.taltech.game.server.messages.Position;
import ee.taltech.game.server.player.PlayerCharacter;
import ee.taltech.game.server.utilities.Game;

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
     * Contains logic, that needs to be updated every tick.
     */
    public void tick() {
        // This function activates according to ticks per second.
        // If 1 TPS, then every second.

        // Update player positions for clients that are in the same game with player
        for (Game game : this.gameServer.games.values()) {
            for (PlayerCharacter player : game.players.values()) {
                player.updatePosition();
                for (Integer playerId : game.players.keySet()) {
                    server.sendToUDP(playerId, new Position(player.playerID, player.xPosition, player.yPosition));
                }
            }
        }
    }

    /**
     * Stop the thread and TPS from running.
     */
    public void stop() {
        running = false;
    }
}
