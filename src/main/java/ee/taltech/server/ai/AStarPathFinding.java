package ee.taltech.server.ai;

import com.esotericsoftware.minlog.Log;
import ee.taltech.server.components.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.*;

public class AStarPathFinding {

    private final int maxX;
    private final int maxY;
    private final int[][] grid;
    private final int[][] neighbours = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private final ExecutorService service;

    /**
     * Construct A star path finding.
     */
    public AStarPathFinding() {
        grid = Grid.grid;
        this.maxX = Constants.MAX_X_NODE;
        this.maxY = Constants.MAX_Y_NODE;
        this.service = Executors.newSingleThreadExecutor();
    }

    /**
     * Put path finding on a different thread.
     */
    public List<Node> findPath(int srcX, int srcY, int dstX, int dstY) {
        try {
            return service.submit(() -> findPathPrivate(srcX, srcY, dstX, dstY)).get();
        } catch (InterruptedException e) {
            Log.info("Path finding got interrupted! Exception: " + e);
            return Collections.emptyList();
        } catch (ExecutionException e) {
            Log.info("Path finding got interrupted! Exception: " + e);
            return Collections.emptyList();
        }
    }

    /**
     * Find path from source to destination.
     *
     * @param srcX source node's x
     * @param srcY source node's y
     * @param dstX destination node's x
     * @param dstY destination node's y
     * @return a path as list of nodes
     */
    private List<Node> findPathPrivate(int srcX, int srcY, int dstX, int dstY) {
        List<Node> path = new ArrayList<>();
        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingInt(Node::getFScore));
        openSet.add(new Node(srcX, srcY, maxY));
        Set<Node> closedSet = new HashSet<>();

        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            if (current.getX() == dstX && current.getY() == dstY) {
                while (current != null) {
                    path.add(current);
                    current = current.getParent();
                }
                path.removeLast();
                return path.reversed();
            }
            closedSet.add(current);
            for (int[] neighbour : neighbours) {
                int x = current.getX() + neighbour[0];
                int y = current.getY() + neighbour[1];
                if ( x < 0 || x >= maxX || y < 0 || y >= maxY || grid[y][x] == 1) {
                    continue;
                }
                Node neighbor = new Node(x, y, maxY);
                int newGScore = current.getGScore() + 1;
                if (closedSet.contains(neighbor)) {
                    continue;
                }
                if (!openSet.contains(neighbor) || newGScore < neighbor.getGScore()) {
                    neighbor.setParent(current);
                    neighbor.setgScore(newGScore);
                    neighbor.updateHScore(dstX, dstY);
                    openSet.add(neighbor);
                }
            }
        }
        return Collections.emptyList();
    }

    /**
     * Close path finding thead.
     */
    public void closePathFindingThread() {
        service.close();
    }
}
