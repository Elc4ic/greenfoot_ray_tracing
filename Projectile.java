import java.io.IOException;

public abstract class Projectile extends ObjFile {

    public Projectile(float[] pos, float[] rot, float scale, String filePath, int textureIndex) throws IOException {
        super(pos, rot, scale, filePath, textureIndex);
    }

    public abstract boolean update();

    public abstract int getDamage();

    public abstract void destroy(WorldBase worldBase);

}
