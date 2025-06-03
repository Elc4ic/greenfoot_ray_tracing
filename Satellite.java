import java.io.IOException;

/// Снаряд который двигается по определеной траектории и наносит урон, не исчезает
public class Satellite extends Projectile {
    private int damage;
    private MovementFunction movementFunction;

    public Satellite(float[] pos, float scale, int damage,String model, int textureIndex) throws IOException {
        super(pos, scale, model,textureIndex);
        this.damage = damage;
    }

    @Override
    public boolean update() {
        movementFunction.move();
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

}

@FunctionalInterface
interface MovementFunction {
    void move();
}
