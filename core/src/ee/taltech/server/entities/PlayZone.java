package ee.taltech.server.entities;

import java.awt.geom.Point2D;

public class PlayZone {
    private int ticks;
    private Point2D.Double center; // Center of the circular zone
    private int timer; // Initial radius of the circular zone
    private double shrinkRate; // Rate at which the zone shrinks per second

    public PlayZone() {
        this.timer = 0; // all calculations currently in pixels
        this.shrinkRate = 1;
        this.ticks = 60;
    }

    public void updateZone(int startTime) {
        timer = startTime;
        System.out.println(timer);
        if (timer < 30) {
            // do nothing
        } else if (timer < 80) {
            // show first zone
        } else if (timer < 200) {
            // implement first zone
            // create body
        } else if (timer < 250) {
            // show second zone
        } else if (timer < 350) {
            // implement second zone
            // create body
        } else if (timer < 450) {
            // show third zone
        } else if (timer < 550) {
            // implement third zone
            // create body
            // final countdown
        } else if (timer < 800) {
            // the entire map turns red
            // create body
        }
    }

    public Integer getTimer() {
        return (int) timer;
    }
}
