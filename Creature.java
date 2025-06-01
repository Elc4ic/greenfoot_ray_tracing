import java.io.IOException;
import java.util.Arrays;

public class Creature extends ObjFile {
    private float gravity;
    private static final int STATE_DEAD = 0;
    private static final int STATE_ALIVE = 1;
    private static final int STATE_DAMAGE = 2;
    private static final int STATE_HEAL = 3;

    private int health = 100;
    private final int healthMax = 100;
    int state = STATE_ALIVE;
    private int step = 1;
    private float speedX = 0;
    private float speedZ = 0;
    private float speedY = 0;
    private boolean onGround = true;
    private final float speedMaxXZ = 1f;
    private final float speedMaxY = 1.6f;
    private final DVector collisionResistance = new DVector();

    public Creature(float[] pos, float[] rot, float scale, String objFile, int textureIndex) throws IOException {
        super(pos, rot, scale, objFile, textureIndex);
    }

    public void update(WorldBase world) {
        gravity = world.getGRAVITY();
        checkCollision(world, true);
        updatePosition();
        updateState();
    }

    void checkCollision(WorldBase world, boolean bulletCollisionEnabled) {
        collisionResistance.clear();

        for (WorldObject o : world.getObjects()) {

            if (o.getCollision(getPos(), getCollisionR())) {
                handleCollision(o, world, bulletCollisionEnabled);

//                float[] normal = o.getNormal(getPos(), hitBoxRadius);
//                collisionResistance.setAdsMax(normal);

            }
        }

    }

    private void handleCollision(WorldObject o, WorldBase world, boolean bulletCollisionEnabled) {
        if (o instanceof TimeSphere timeSphere) {
            world.deleteObject(timeSphere);
        } else if (o instanceof Projectile projectile && bulletCollisionEnabled) {
            if(projectile instanceof Missile missile){
                applyDamage(-missile.getDamage());
                missile.addPenetration();
            }///лучше перегрузить метод в колизии!? наверное
        }
    }

    private void updatePosition() {
        if (speedX == 0 && speedY == 0 && speedZ == 0 || state == STATE_DEAD) return;

        float[] XOffset = Vector3.resist(Vector3.scale(getDirection(), speedX), collisionResistance);
        float[] direction = getDirection();
        Vector3.rotateY(direction, 90);
        float[] ZOffset = Vector3.resist(Vector3.scale(direction, speedZ), collisionResistance);
        float[] YOffset = Vector3.scale(new float[]{0, 1, 0}, speedY);

        addToPos(XOffset);
        addToPos(ZOffset);
        addToPos(YOffset);

        applyGravity();
        applyFriction();
    }

    private void applyGravity() {
        if (getPos()[1] > 1f) {
            speedY -= gravity;
        } else {
            float y = getPos()[1];
            addToPos(new float[]{0, -y, 0});
            onGround = true;
            speedY = 0;
        }
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
        if (!onGround) return;
        speedY = speedMaxY;
        onGround = false;
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

    public float getSpeedY() {
        return speedY;
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

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
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
