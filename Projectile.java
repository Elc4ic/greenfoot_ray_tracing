import java.io.IOException;

public abstract class Projectile extends ObjFile {

    public Projectile(float[] pos, float scale, int textureIndex) throws IOException {
        super(pos, new float[]{0, 0, 0}, scale, "models\\orb.obj", textureIndex);
    }

    public abstract boolean update();

    public abstract int getDamage();

    public abstract void destroy(WorldBase worldBase);

}
