import java.io.IOException;

public class AidKid extends ObjFile {
    private int heal = 30;

    public AidKid(float[] pos) throws IOException {
        super(pos, new float[]{0, 0, 0}, 0.5f, "models\\block.obj", TextureCollection.getInstance().getIndex("heal"));
        setCollisionR(2f);
    }

    public int getHeal() {
        return heal;
    }
}
