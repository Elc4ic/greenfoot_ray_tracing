import java.io.IOException;
import java.util.Arrays;

public class Creature extends ObjFile {
    static final int STATE_DEAD = 0;
    static final int STATE_ALIVE = 1;
    static final int STATE_DAMAGE = 2;
    static final int STATE_HEAL = 3;

    private int health = 100;
    private final int healthMax = 100;
    int state = STATE_ALIVE;
    private int step = 1;
    private float speedX = 0;
    private float speedZ = 0;
    private float speedMaxXZ = 1;
    private final float speedMaxY = 1.6f;
    private final DVector collisionResistance = new DVector();
    private boolean bulletCollisionEnabled = true;

    public Creature(float[] pos, float[] rot, float scale, String objFile, int textureIndex) throws IOException {
        super(pos, rot, scale, objFile, textureIndex);
    }

    public boolean update() {
        WorldBase world = WorldBase.getInstance();
        checkCollision(world);
        updateHorizontalPosition();
        applyGravity();
        updateState();
        return state == STATE_DEAD;
    }

    void checkCollision(WorldBase world) {
        collisionResistance.clear();

        for (WorldObject o : world.getObjects()) {

            if (o.haveCollision(getPos(), getCollisionR())) {
                handleCollision(o);

//                float[] normal = o.getNormal(getPos(), hitBoxRadius);
//                collisionResistance.setAdsMax(normal);

            }
        }
    }

    private void handleCollision(WorldObject o) {
        if (o instanceof Projectile projectile && bulletCollisionEnabled) {

            if (projectile instanceof Missile missile) {
                applyDamage(-missile.getDamage());
                missile.addPenetration();
                addToPos(Vector3.scale(missile.getNormal(), -missile.getRepulsion()));
            }///лучше перегрузить метод в колизии!? наверное
        }
    }

    private void updateHorizontalPosition() {
        if (speedX == 0 && speedZ == 0 || state == STATE_DEAD) return;

        float[] XOffset = Vector3.resist(Vector3.scale(getDirection(), speedX), collisionResistance);
        float[] direction = getDirection();
        Vector3.rotateY(direction, 90);
        float[] ZOffset = Vector3.resist(Vector3.scale(direction, speedZ), collisionResistance);

        addToPos(XOffset);
        addToPos(ZOffset);

        applyFriction();
    }

    private void applyFriction() {
        if (Math.abs(speedX) > speedMaxXZ / 4) {
            speedX -= Math.signum(speedX) * speedMaxXZ / 4;
        } else {
            speedX = 0;
        }

        if (Math.abs(speedZ) > speedMaxXZ / 4) {
            speedZ -= Math.signum(speedZ) * speedMaxXZ / 4;
        } else {
            speedZ = 0;
        }
    }

    private void updateState() {
        if (state == STATE_ALIVE || state == STATE_DEAD) return;

        if ((state == STATE_DAMAGE || state == STATE_HEAL) && step < 5) {
            step++;
        } else {
            state = STATE_ALIVE;
            step = 0;
        }
    }

    public void jump() {
        if (!isOnGround()) return;
        setSpeedY(speedMaxY);
        setOnGround(false);
    }

    void rotateXn(float angle) {
        addToRotation(new float[]{0, -angle, 0});
    }

    void rotateYn(float angle) {
        addToRotation(new float[]{-angle, 0, 0});
    }

    void moveForward() {
        speedX = speedMaxXZ;
    }

    void moveBackward() {
        speedX = -speedMaxXZ;
    }

    void moveLeft() {
        speedZ = speedMaxXZ;
    }

    void moveRight() {
        speedZ = -speedMaxXZ;
    }


    int getHealth() {
        return health;
    }

    public void applyDamage(int deltaHealth) {
        health += deltaHealth;
        if (health <= 0) {
            state = STATE_DEAD;
            return;
        }
        state = (deltaHealth < 0) ? STATE_DAMAGE : STATE_HEAL;
    }

    int getHealthMax() {
        return healthMax;
    }

    public float getSpeedX() {
        return speedX;
    }

    public float getSpeedZ() {
        return speedZ;
    }

    public void setSpeedX(float speedX) {
        this.speedX = speedX;
    }

    public void setSpeedZ(float speedZ) {
        this.speedZ = speedZ;
    }

    public void setBulletCollisionEnabled(boolean bulletCollisionEnabled) {
        this.bulletCollisionEnabled = bulletCollisionEnabled;
    }
}

class DVector {
    float[] pos_v = new float[]{0, 0, 0};
    float[] neg_v = new float[]{0, 0, 0};

    void clear() {
        Arrays.fill(pos_v, 0);
        Arrays.fill(neg_v, 0);
    }

    void setAdsMax(float[] v) {
        pos_v[0] = Math.max(pos_v[0], Math.max(0, v[0]));
        neg_v[0] = Math.min(neg_v[0], Math.min(0, v[0]));
        pos_v[1] = Math.max(pos_v[1], Math.max(0, v[1]));
        neg_v[1] = Math.min(neg_v[1], Math.min(0, v[1]));
        pos_v[2] = Math.max(pos_v[2], Math.max(0, v[2]));
        neg_v[2] = Math.min(neg_v[2], Math.min(0, v[2]));
    }

    float[] getTotalResistance() {
        return new float[]{
                pos_v[0] + neg_v[0],
                pos_v[1] + neg_v[1],
                pos_v[2] + neg_v[2]
        };
    }
}
