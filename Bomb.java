import java.io.IOException;

/// Снаряд бомба - взрывается при достижении 0 кординат по Y и наносит урон всем врагам в радиусе
public class Bomb extends Projectile {
    private int damage;
    private float explodeRadius;

    public Bomb(float[] pos, float scale, int damage, float radius, String model, int textureIndex) throws IOException {
        super(pos, new float[]{0, 0, 0}, scale, model, textureIndex);
        this.damage = damage;
        this.explodeRadius = radius;
        setOnGround(false);
    }

    @Override
    public boolean update() {
        applyGravity();
        setSpeedY(-0.6f);
        return getPos()[1] <= 0;
    }

    @Override
    public int getDamage() {
        return damage;
    }

    @Override
    public void destroy(WorldBase worldBase) {
        try {
            Flash explode = new Flash(
                    getPos(), explodeRadius, damage,
                    "models\\explode.obj",
                    TextureCollection.getInstance().getIndex("explode")
            );
            explode.setCollisionR(explodeRadius);
            worldBase.addObject(explode);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        worldBase.deleteObject(this);
    }
}
