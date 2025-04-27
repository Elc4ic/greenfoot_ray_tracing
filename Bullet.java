import greenfoot.Color;

public class Bullet extends Sphere {
    private float[] normal;
    private int damage;
    private int penetration = 0;
    private int penetrationMax;
    private float speed;
    private float distanceMax;
    private float distance = 0;
    private boolean bfg = false;

    public Bullet(float[] center, float radius, int color, float[] normal, float speed, int damage, float distanceMax, int penetrationMax) {
        super(center, radius, color);
        this.normal = normal;
        this.speed = speed;
        this.damage = damage;
        this.distanceMax = distanceMax;
        this.penetrationMax = penetrationMax;
    }

    public Bullet(float[] center, float radius, int color, float[] normal, float speed, int damage, float distanceMax, int penetrationMax, boolean bfg) {
        super(center, radius, color);
        this.normal = normal;
        this.speed = speed;
        this.damage = damage;
        this.distanceMax = distanceMax;
        this.penetrationMax = penetrationMax;
        this.bfg = bfg;
    }

    public boolean update() {
        if (distance >= distanceMax || penetration >= penetrationMax) {
            return true;
        }
        float[] move = Vector3.scale(normal,speed);
        distance += Vector3.length(move);
        setCenter(Vector3.add(getPos(),move));
        return false;
    }

    public int getDamage() {
        return damage;
    }

    public void addPenetration() {
        penetration++;
    }

    public boolean isBfg() {
        return bfg;
    }

    public float[] getNormal() {
        return normal;
    }
}
