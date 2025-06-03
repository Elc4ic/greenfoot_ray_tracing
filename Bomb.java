import java.io.IOException;
/// Снаряд бомба - взрывается при достижении 0 кординат по Y и наносит урон всем врагам в радиусе
public class Bomb extends Projectile {
    private int damage;
    private float explodeRadius = 0.5f;

    public Bomb(float[] pos, float scale, int damage,float radius, int textureIndex) throws IOException {
        super(pos, scale,"models\\orb.obj", textureIndex);
        this.damage = damage;
        this.explodeRadius = radius;
    }

    @Override
    public boolean update() {
        return getPos()[1] <= 0;
    }

    @Override
    public int getDamage() {
        return 0;
    }

    @Override
    public void destroy(WorldBase worldBase) {
        for (WorldObject o : worldBase.getObjects()) {
            if (o instanceof Enemy enemy && o.getDistance(getPos()) <= explodeRadius) {
                enemy.applyDamage(damage);
            }
        }
        worldBase.deleteObject(this);
    }
}
