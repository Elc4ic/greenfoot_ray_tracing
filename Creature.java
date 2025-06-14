import java.io.IOException;
import java.util.Arrays;

public class Creature extends ObjFile {
    static final int STATE_DEAD = 0;
    static final int STATE_ALIVE = 1;
    static final int STATE_DAMAGE = 2;
    static final int STATE_HEAL = 3;
    static final int STATE_UPGRADE = 4;

    private int health = 100;
    private int healthMax = 100;

    private int state = STATE_ALIVE;
    private int step = 1;
    private float speedX = 0;
    private float speedZ = 0;
    private float speedMaxXZ = 0.7f;
    private final float speedMaxY = 1.7f;
    private boolean bulletCollisionEnabled = true;

    public Creature(float[] pos, float[] rot, float scale, String objFile, int textureIndex) throws IOException {
        super(pos, rot, scale, objFile, textureIndex);
    }

    public Creature(float[] pos, float[] rot, float scale, String objFile, int textureIndex, int health) throws IOException {
        super(pos, rot, scale, objFile, textureIndex);
        this.healthMax = health;
        this.health = health;
    }

    public boolean update() {
        WorldBase world = WorldBase.getInstance();
        updateHorizontalPosition();
        applyGravity();
        updateState();
        checkCollision(world);
        return state == STATE_DEAD;
    }

    void checkCollision(WorldBase world) {
        for (WorldObject o : world.getObjects()) {
            if (o.haveCollision(getPos(), getCollisionR())) {
                doOnCollision(o, world);
            }
        }
    }

    private void doOnCollision(WorldObject o, WorldBase world) {
        if (this instanceof Hero hero) {
            if (o instanceof Experience exp) {
                hero.addExp(exp.getExp());
                world.deleteObject(exp);
            }
            if (o instanceof AidKid aidKid) {
                hero.changeHealth(aidKid.getHeal());
                world.deleteObject(aidKid);
            }
        } else if (o instanceof Projectile projectile && bulletCollisionEnabled) {
            changeHealth(-projectile.getDamage());
            if (projectile instanceof Missile missile) {
                missile.addPenetration();
                addToPos(missile.getRepulsion());
            } else {
                addToPos(Vector3.scale(getDirection(), -1));
            }
        } else if (this instanceof Enemy && o instanceof Enemy) {
            float[] dif = Vector3.subtract(getPos(), o.getPos());
            float len = Vector3.length(dif);
            if (len < getCollisionR() * 2) {
                addToPos(Vector3.scale(dif, 1 - len / getCollisionR() / 2));
            }
        }
    }

    private void updateHorizontalPosition() {
        if (speedX == 0 && speedZ == 0 || state == STATE_DEAD) return;

        float[] XOffset = Vector3.scale(getDirection(), speedX);
        float[] direction = getDirection();
        Vector3.rotateY(direction, 90);
        float[] ZOffset = Vector3.scale(direction, speedZ);

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

    void rotateYn(float angle) {
        addToRotation(new float[]{0, -angle, 0});
    }

    void rotateXn(float angle) {
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

    public void changeHealth(int deltaHealth) {
        health += deltaHealth;
        if (health <= 0) {
            state = STATE_DEAD;
            return;
        }
        if (health > healthMax) health = healthMax;
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

    public void setState(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
