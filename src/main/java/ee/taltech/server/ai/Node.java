package ee.taltech.server.ai;

public class Node {
    private final int x;
    private final int y;
    private final int maxY;
    private int gScore;
    private int hScore;
    private Node parent;

    /**
     * Construct Node.
     *
     * @param x node's x value
     * @param y node's y value
     */
    public Node(int x, int y, int maxY) {
        this.x = x;
        this.y = y;
        this.maxY = maxY;
        this.gScore = 0;
        this.hScore = 0;
        this.parent = null;
    }

    /**
     * Get node's x value.
     *
     * @return x
     */
    public int getX() {
        return x;
    }

    /**
     * Get node's y value.
     *
     * @return y
     */
    public int getY() {
        return y;
    }

    /**
     * Get node's g score.
     *
     * @return gScore
     */
    public int getGScore() {
        return gScore;
    }

    /**
     * Get node's parent node.
     *
     * @return parent
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Set node's new g score.
     *
     * @param gScore new g score
     */
    public void setgScore(int gScore) {
        this.gScore = gScore;
    }

    /**
     * Set node's new parent.
     *
     * @param parent new parent
     */
    public void setParent(Node parent) {
        this.parent = parent;
    }

    /**
     * Update node's h score.
     *
     * @param dstX destination x
     * @param dstY destination y
     */
    void updateHScore(int dstX, int dstY) {
        this.hScore = Math.abs(x - dstX) + Math.abs(y - dstY);
    }

    /**
     * Get node's f score.
     *
     * @return f score aka g score + h score
     */
    int getFScore() {
        return this.gScore + this.hScore;
    }

    /**
     * Check if nodes are equal.
     *
     * @param o node to be checked
     * @return true, if they are equal else false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Node node)) return false;
        return x == node.x && y == node.y;
    }

    /**
     * Set hash code for comparing nodes.
     *
     * @return int of a hash code
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(x + (y * maxY));
    }
}
