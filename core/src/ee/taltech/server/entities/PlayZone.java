package ee.taltech.server.entities;

import java.awt.geom.Point2D;

public class PlayZone {
    private Point2D.Double center; // Center of the circular zone
    private double radius; // Initial radius of the circular zone
    private double shrinkRate; // Rate at which the zone shrinks per second

    public PlayZone() {
        this.radius = 5000; // all calculations currently in pixels
        this.shrinkRate = 10;
    }

    public void shrinkPlayZone(long startTime) {
        radius -= shrinkRate * startTime;
        if (radius < 0) {
            radius = 0;
        }
    }

    public Integer getRadius() {
        return (int) radius;
    }
}
