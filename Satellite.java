import java.io.IOException;

/// Снаряд который двигается по определеной траектории и наносит урон, не исчезает
public class Satellite extends Projectile {
    private int damage;

    public Satellite(float[] pos, float scale, int damage, int textureIndex) throws IOException {
        super(pos, scale, textureIndex);
        this.damage = damage;
    }

    @Override
    public boolean update() {
        float[] move = new float[]{
                (float) Math.sin(System.currentTimeMillis()),
                0,
                (float) Math.cos(System.currentTimeMillis())
        };
        addToPos(move);
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

}
