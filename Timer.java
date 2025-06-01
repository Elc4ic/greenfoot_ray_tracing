import greenfoot.*;

public class Timer extends Actor {
    private long time = 0;
    private long dT = 0;
    private long dt;
    private long newFrameTimeMillis = System.nanoTime();
    private long oldFrameTimeMillis = newFrameTimeMillis;
    GreenfootImage img;

    public Timer(int tickRate) {
        dt = 1000000000L / tickRate;
    }

    public void act() {
        img = new GreenfootImage(
                "Time: " + time / 1000000000,
                18, Color.RED, Color.BLACK
        );
        setImage(img);
    }

    public boolean update() {
        this.oldFrameTimeMillis = this.newFrameTimeMillis;
        this.newFrameTimeMillis = System.nanoTime();
        long frame = newFrameTimeMillis - oldFrameTimeMillis;
        time = Math.max(time + frame, 0);
        dT += frame;
        if (dT >= dt) {
            dT = 0;
            return true;
        } else {
            return false;
        }
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
