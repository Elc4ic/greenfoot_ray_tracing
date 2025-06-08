import java.io.IOException;

public class Flash extends Projectile {
    private int damage;
    private long framesToLive = 3;
    private long framesAlive = 0;

    public Flash(float[] pos, float[] rot, float scale, int damage, long framesToLive, String model, int textureIndex) throws IOException {
        super(pos, rot, scale, model, textureIndex);
        this.damage = damage;
        this.framesToLive = framesToLive;
    }

    public Flash(float[] pos, float scale, int damage, String model, int textureIndex) throws IOException {
        super(pos, new float[]{0, 0, 0}, scale, model, textureIndex);
        this.damage = damage;
    }

    @Override
    public boolean update() {
        if (framesAlive >= framesToLive) {
            return true;
        }
        framesAlive++;
        return false;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void destroy(WorldBase worldBase) {
        worldBase.deleteObject(this);
    }

}
