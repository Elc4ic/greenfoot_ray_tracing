import greenfoot.Color;

import java.io.IOException;
import java.util.Arrays;

public class Creature extends ObjFile {
    private static final float GRAVITY = 0.02f;
    private static final int STATE_DEAD = 0;
    private static final int STATE_ALIVE = 1;
    private static final int STATE_DAMAGE = 2;
    private static final int STATE_HEAL = 3;

    private int health = 100;
    private final int healthMax = 100;
    int state = STATE_ALIVE;
    private int step = 1;
    float speedXZ = 0;
    private float speedY = 0;
    private boolean onGround = true;
    private final float speedMaxXZ = 1f;
    private final float speedMaxY = 1.6f;
    final float hitBoxRadius;
    private final DVector collisionResistance = new DVector();
    private final Timer timer = new Timer();

    public Creature(float[] pos, float scale, float[] rotation, String objFile, float hitBoxRadius, int textureIndex) throws IOException {
        super(pos, scale, objFile, textureIndex);
        this.hitBoxRadius = hitBoxRadius;
        setRotation(rotation);
    }

    public void updateCreature(WorldBase world) {
        checkCollision(world, true);
        updatePosition();
        updateState();
    }

    void checkCollision(WorldBase world, boolean bulletCollisionEnabled) {
        collisionResistance.clear();

        for (WorldObject o : world.getObjects()) {

            if (o.getCollision(getPos(), hitBoxRadius)) {
                handleCollision(o, world, bulletCollisionEnabled);

//                float[] normal = o.getNormal(getPos(), hitBoxRadius);
//                collisionResistance.setAdsMax(normal);

            }
        }

    }

    private void handleCollision(WorldObject o, WorldBase world, boolean bulletCollisionEnabled) {
        if (o instanceof TimeSphere timeSphere) {
            timer.addTime(timeSphere.takeTime());
            world.deleteObject(timeSphere);
        } else if (o instanceof Bullet bullet && bulletCollisionEnabled) {
            applyDamage(-bullet.getDamage());
            bullet.addPenetration();
        }
    }

    private void updatePosition() {
        if (speedXZ == 0 && speedY == 0 || state == STATE_DEAD) return;

        float[] horizontalOffset = Vector3.resist(Vector3.scale(getDirection(), speedXZ), collisionResistance);
        float[] verticalOffset = Vector3.scale(new float[]{0, 1, 0}, speedY);

        addToPos(horizontalOffset);
        addToPos(verticalOffset);

        applyGravity();
        applyFriction();
    }

    private void applyGravity() {
        if (getPos()[1] > 1f) {
            speedY -= GRAVITY;
        } else {
            float y = getPos()[1];
            addToPos(new float[]{0, -y + 1f, 0});
            onGround = true;
            speedY = 0;
        }
    }

    private void applyFriction() {
        if (Math.abs(speedXZ) > speedMaxXZ / 4) {
            speedXZ -= Math.signum(speedXZ) * speedMaxXZ / 4;
        } else {
            speedXZ = 0;
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

    void moveForward() {
        speedXZ = speedMaxXZ;
    }

    void moveBackward() {
        speedXZ = -speedMaxXZ;
    }

    void moveLeft() {

    }

    void moveRight() {
    }


    int getHealth() {
        return health;
    }

    Timer getTimer() {
        return timer;
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
