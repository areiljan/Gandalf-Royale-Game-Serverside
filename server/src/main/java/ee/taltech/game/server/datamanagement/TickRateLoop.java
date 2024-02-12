package ee.taltech.game.server.datamanagement;

import com.esotericsoftware.kryonet.Server;
import ee.taltech.game.server.messages.Position;
import ee.taltech.game.server.player.PlayerCharacter;

import java.util.HashMap;
import java.util.Map;

public class TickRateLoop implements Runnable {
    private volatile boolean running = true;
    private Server server;
    public Map<Integer, PlayerCharacter> players = new HashMap<>();

    public TickRateLoop(Server server, Map<Integer, PlayerCharacter> players) {
        this.server = server;
        // Whole list for the players in the list.
        this.players = players;
    }

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

    public void tick() {
        // This function activates according to ticks per second.
        // If 1 TPS, then every second.
        for (PlayerCharacter player : this.players.values()) {
            // Every existing player position is being updated.
            player.updatePosition();
            // Send out a Position for the given player.
            server.sendToAllUDP(new Position(player.playerID, player.xPosition, player.yPosition));
        }
    }

    public void stop() {
        // To stop the thread and TPS from running.
        running = false;
    }
}
