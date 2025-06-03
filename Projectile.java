import java.io.IOException;

public abstract class Projectile extends ObjFile {

    public Projectile(float[] pos, float scale,String filePath, int textureIndex) throws IOException {
        super(pos, new float[]{0, 0, 0}, scale, filePath, textureIndex);
    }

    public abstract boolean update();

    public abstract int getDamage();

    public abstract void destroy(WorldBase worldBase);

}
