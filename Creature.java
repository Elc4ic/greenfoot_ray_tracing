import greenfoot.Color;

public class Creature extends Cube {
    int health = 100;
    int healthMax = 100;
    // 0- die, 1 - alive, 2 - take damage, 3 - take heal
    int state = 1;
    int step = 1;
    float[] normal;
    float speedXZ = 0;
    float speedY = 0;
    float speedMaxXZ = 0.3f;
    float speedMaxY = 3f;
    float rotationSpeed = 1;
    float hitBoxRadius;
    DVector collisionResistV = new DVector();
    private Timer time = new Timer(30000000000L);
    int portalEnter = 0;

    public Creature(float[] pos, float x, float y, float z, float[] normal, int color, float hitBoxRadius) {
        super(pos, x, y, z, color);
        this.normal = Vector3.normalize(normal);
        this.hitBoxRadius = hitBoxRadius;
    }

    public void update(WorldBase world) {
        checkCollision(world, true);
        updatePos();
    }

    void checkCollision(WorldBase world, boolean bulletCol) {
        collisionResistV.clear();
        for (WorldObject o : world.getObjects()) {
            boolean col = o.getCollision(getPos(), hitBoxRadius);
            if (col) {
                if (o instanceof TimeSphere timeSphere) {
                    time.addTime(timeSphere.takeTime());
                    world.deleteObject(timeSphere);
                }
                if (o instanceof Portal portal) {
                    portalEnter = portalEnter < 2 ? ++portalEnter : 2;
                    if (portalEnter == 1) {
                        System.out.println(portalEnter);
                        world.newWorld = portal.changeWorld();
                        world.needChangeWorld = true;
                    }
                }
                if (o instanceof Bullet && bulletCol) {
                    setHealth(-((Bullet) o).getDamage());
                    ((Bullet) o).addPenetration();
                }
//                collisionResistV.setAdsMax(o.getNormal(getPos(), hitBoxRadius));
            }
            if (!col && o instanceof Portal) {
                portalEnter = 0;
            }
        }
    }

    void jump() {
        if (state == 4) return;
        speedY = speedMaxY;
        state = 4;
    }

    void updatePos() {
        if (speedXZ == 0 || state == 0) return;
        setPos(Vector3.add(getPos(), Vector3.resist(Vector3.scale(normal, speedXZ), collisionResistV)));
        if (Math.abs(speedXZ) > speedMaxXZ / 4) speedXZ -= Math.signum(speedXZ) * speedMaxXZ / 4;
        else speedXZ = 0;
    }

    void updateState() {
        if (state == 1 || state == 0) return;
        if ((state == 2 || state == 3) && step < 5) {
            step++;
        } else {
            float h = (float) 255 * health / healthMax;
            setColor((int) h << 8);
            state = 1;
            step = 0;
        }
    }

    void rotateYn(float angle) {
        Vector3.rotateY(normal, angle * rotationSpeed);
        normal = Vector3.normalize(normal);
    }

    void rotateXn(float angle) {
        Vector3.rotateX(normal, angle * rotationSpeed);
        normal = Vector3.normalize(normal);
    }

    void moveForward() {
        speedXZ = speedMaxXZ;
    }

    void moveBackward() {
        speedXZ = -speedMaxXZ;
    }

    void moveLeft() {
        speedXZ = speedMaxXZ;
    }

    void moveRight() {
        speedXZ = speedMaxXZ;
    }


    float[] getNormal() {
        return normal;
    }

    void setNormal(float[] normal) {
        this.normal = normal;
    }

    int getHealth() {
        return health;
    }

    Timer getTimer() {
        return time;
    }

    void setHealth(int damage) {
        this.health += damage;
        if (this.health <= 0) {
            setColor(ColorOperation.GColorToInt(Color.GRAY));
            state = 0;
            return;
        }
        if (damage < 0) {
            state = 2;
            setColor(ColorOperation.GColorToInt(Color.RED));
        } else {
            state = 3;
            setColor(ColorOperation.GColorToInt(Color.BLUE));
        }
    }

    int getHealthMax() {
        return healthMax;
    }

}

class DVector {
    float[] pos_v = new float[]{0, 0, 0};
    float[] neg_v = new float[]{0, 0, 0};

    void clear() {
        Vector3.clear(pos_v);
        Vector3.clear(neg_v);
    }

    void setAdsMax(float[] v) {
        if (v[0] < 0) Vector3.setAdsMaxX(neg_v, v[0]);
        else Vector3.setAdsMaxX(pos_v, v[0]);
        if (v[1] < 0) Vector3.setAdsMaxY(neg_v, v[1]);
        else Vector3.setAdsMaxY(pos_v, v[1]);
        if (v[2] < 0) Vector3.setAdsMaxZ(neg_v, v[2]);
        else Vector3.setAdsMaxZ(pos_v, v[2]);
    }
}
