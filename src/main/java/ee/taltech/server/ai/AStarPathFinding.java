package ee.taltech.server.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class AStarPathFinding {

    private final int maxX;
    private final int maxY;
    private final int[][] grid;
    private final int[][] neighbours = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    public AStarPathFinding() {
        grid = Grid.grid;
        this.maxX = 1200;
        this.maxY = 1200;
    }

    public List<Node> findPath(int srcX, int srcY, int dstX, int dstY) {
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
}
