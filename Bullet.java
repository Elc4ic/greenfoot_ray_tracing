import greenfoot.Color;

import java.io.IOException;

public class Bullet extends ObjFile {
    private float[] normal;
    private int damage;
    private int penetration = 0;
    private int penetrationMax;
    private float speed;
    private float distanceMax;
    private float distance = 0;
    private boolean bfg = false;

    public Bullet(float[] pos, float scale, int color, float[] normal, float speed, int damage, float distanceMax, int penetrationMax, boolean hasTexture, int textureIndex) throws IOException {
        super(pos, scale, color, "models\\orb.obj", hasTexture, textureIndex);
        this.normal = normal;
        this.speed = speed;
        this.damage = damage;
        this.distanceMax = distanceMax;
        this.penetrationMax = penetrationMax;
    }

    public Bullet(float[] pos, float scale, int color, float[] normal, float speed, int damage, float distanceMax, int penetrationMax, boolean hasTexture, int textureIndex, boolean bfg) throws IOException {
        super(pos, scale, color, "models\\orb.obj", hasTexture, textureIndex);
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
        float[] move = Vector3.scale(normal, speed);
        distance += Vector3.length(move);
        addToPos(move);
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
