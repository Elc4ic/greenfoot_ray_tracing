import greenfoot.*;

import java.util.concurrent.atomic.AtomicLong;

public class Timer extends Actor {
    private long time = 0 ;
    private long newFrameTimeMillis = System.nanoTime();
    private long oldFrameTimeMillis = newFrameTimeMillis;
    GreenfootImage img;

    public void act() {
        img = new GreenfootImage(
                "Time: " + time / 1000000000,
                18, Color.RED, Color.BLACK
        );
        setImage(img);
        update();
    }

    public boolean update() {
        this.oldFrameTimeMillis = this.newFrameTimeMillis;
        this.newFrameTimeMillis = System.nanoTime();
        long frame = newFrameTimeMillis - oldFrameTimeMillis;
        time = Math.max(time + frame, 0);
        return isZero();
    }

    public void addTime(long time) {
        this.time += time;
    }

    public long getTime() {
        return time;
    }

    public boolean isZero() {
        return time == 0;
    }

}
