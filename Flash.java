import java.io.IOException;

public class Flash extends Projectile {
    private int damage;
    private long timeToLive = Const.SECOND / 2;
    private long timeAlive = 0;
    private long oldTime;

    public Flash(float[] pos, float scale, int damage, int textureIndex) throws IOException {
        super(pos, scale, textureIndex);
        this.damage = damage;
    }

    @Override
    public boolean update() {
        if (timeAlive >= timeToLive) {
            return true;
        }
        timeAlive += System.nanoTime() - oldTime;
        oldTime = System.nanoTime();
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
