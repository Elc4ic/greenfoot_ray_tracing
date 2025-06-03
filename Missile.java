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
    private float repulsion = 1.5f;

    public Missile(float[] pos, float[] normal, String model, float scale, float speed, int damage, float distanceMax, int penetrationMax, int textureIndex) throws IOException {
        super(pos, scale, model, textureIndex);
        this.speed = speed;
        this.damage = damage;
        this.distanceMax = distanceMax;
        this.penetrationMax = penetrationMax;
        this.normal = normal;
    }

    @Override
    public boolean update() {
        if (distance >= distanceMax || penetration >= penetrationMax) return true;
        float[] move = Vector3.scale(normal, -speed);
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

    public float[] getNormal() {
        return normal;
    }

    public float getRepulsion() {
        return repulsion;
    }

    public void addPenetration() {
        penetration++;
    }
}
