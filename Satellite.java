import java.io.IOException;

/// Снаряд который двигается по определеной траектории и наносит урон, не исчезает
public class Satellite extends Projectile {
    private int damage;
    private MovementFunction movementFunction;
    private Hero hero;
    private float[] rotSpeed = new float[]{0, 0, 0};
    private long framesToLive = 4;
    private long framesAlive = 0;
    private boolean permanent = true;

    public Satellite(Hero hero, float[] pos, float[] rot, float scale, int damage, String model, int textureIndex) throws IOException {
        super(pos, rot, scale, model, textureIndex);
        this.hero = hero;
        this.damage = damage;
    }

    @Override
    public boolean update() {
        if (!permanent) framesAlive++;
        setPos(movementFunction.move(hero, getPos(), getRotation()));
        addToRotation(rotSpeed);
        return framesAlive >= framesToLive;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void destroy(WorldBase worldBase) {
        worldBase.deleteObject(this);
    }

    public void setMovementFunction(MovementFunction movementFunction) {
        this.movementFunction = movementFunction;
    }

    public void setTimeToLive(long framesToLive) {
        this.framesToLive = framesToLive;
        permanent = false;
    }

    public void setRotSpeed(float[] rotSpeed) {
        this.rotSpeed = rotSpeed;
    }

    public float[] getRotSpeed() {
        return rotSpeed;
    }

    public float[] getHeroPos() {
        return hero.getPos();
    }
}

@FunctionalInterface
interface MovementFunction {
    float[] move(Hero hero, float[] pos, float[] rot);
}
