package ee.taltech.server.entities;

import java.awt.geom.Point2D;

public class PlayZone {
    private int ticks;
    private Point2D.Double center; // Center of the circular zone
    private double radius; // Initial radius of the circular zone
    private double shrinkRate; // Rate at which the zone shrinks per second

    public PlayZone() {
        this.radius = 5000; // all calculations currently in pixels
        this.shrinkRate = 1;
        this.ticks = 60;
    }

    public void shrinkPlayZone(long startTime) {
        // 60 ticks in a second
        if (ticks < 60) {
            ticks += 1;
        } else {
            ticks = 0;
            radius -= shrinkRate;
            if (radius < 0) {
                radius = 0;
            }
        }
    }

    public Integer getRadius() {
        return (int) radius;
    }
}
