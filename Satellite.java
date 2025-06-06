import java.io.IOException;

/// Снаряд который двигается по определеной траектории и наносит урон, не исчезает
public class Satellite extends Projectile {
    private int damage;
    private MovementFunction movementFunction;
    private Hero hero;
    private float[] rotSpeed = new float[]{0, 0, 0};

    public Satellite(Hero hero, float[] pos, float[] rot, float scale, int damage, String model, int textureIndex) throws IOException {
        super(pos, rot, scale, model, textureIndex);
        this.hero = hero;
        this.damage = damage;
    }

    @Override
    public boolean update() {
        setPos(movementFunction.move(hero.getPos(), getPos(), getRotation()));
        addToRotation(rotSpeed);
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

    public void setMovementFunction(MovementFunction movementFunction) {
        this.movementFunction = movementFunction;
    }

    public float[] doMovementFunction(float[] heroPos, float[] pos, float[] rotSpeed) {
        return movementFunction.move(heroPos, pos, rotSpeed);
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
    float[] move(float[] heroPos, float[] pos, float[] rot);
}
