import java.io.IOException;
/// Снаряд летит в определенном направлении, имеет количество пробитий
public class Missile extends Projectile {
    private int damage;
    private int penetration = 0;
    private int penetrationMax;
    private float speed;
    private float distanceMax;
    private float distance = 0;
    private float[] normal;

    public Missile(float[] pos, float[] normal, float scale, float speed, int damage, float distanceMax, int penetrationMax, int textureIndex) throws IOException {
        super(pos, scale, textureIndex);
        this.speed = speed;
        this.damage = damage;
        this.distanceMax = distanceMax;
        this.penetrationMax = penetrationMax;
        this.normal = normal;
    }

    @Override
    public boolean update() {
        if (distance >= distanceMax || penetration >= penetrationMax) return true;
        float[] move = Vector3.scale(normal, speed);
        distance += Vector3.length(move);
        addToPos(move);
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

    public void addPenetration() {
        penetration++;
    }
}
