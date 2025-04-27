import java.util.concurrent.atomic.AtomicLong;

public final class FPSCounter {
    private final AtomicLong newFPS = new AtomicLong();
    private final AtomicLong newFPSReferenceTimeMillis = new AtomicLong();
    private final AtomicLong newFrameTimeMillis = new AtomicLong();
    private final AtomicLong oldFPS = new AtomicLong();
    private final AtomicLong oldFrameTimeMillis = new AtomicLong();

    public FPSCounter() {
    }

    public long getFPS() {
        return this.oldFPS.get();
    }

    public long getFrameTimeMillis() {
        return this.oldFrameTimeMillis.get();
    }

    public void update() {
        final long currentTimeMillis = System.currentTimeMillis();

        this.newFPS.incrementAndGet();
        this.newFPSReferenceTimeMillis.compareAndSet(0L, currentTimeMillis);
        this.oldFrameTimeMillis.set(currentTimeMillis - this.newFrameTimeMillis.get());
        this.newFrameTimeMillis.set(currentTimeMillis);

        final long newFPSReferenceTimeMillis = this.newFPSReferenceTimeMillis.get();
        final long newFPSElapsedTimeMillis = currentTimeMillis - newFPSReferenceTimeMillis;

        if(newFPSElapsedTimeMillis >= 1000L) {
            this.oldFPS.set(this.newFPS.get());
            this.newFPS.set(0L);
            this.newFPSReferenceTimeMillis.set(currentTimeMillis);
        }
    }
}
