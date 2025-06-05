import java.io.IOException;

public class Experience extends ObjFile {
    private int exp = 1;

    public Experience(float[] pos, int exp) throws IOException {
        super(pos, new float[]{0, 0, 0}, 0.3f, "models\\experience.obj", TextureCollection.getInstance().getIndex("experience"));
        this.exp = exp;
    }

    public Experience(float[] pos) throws IOException {
        super(pos, new float[]{0, 0, 0}, 0.3f, "models\\experience.obj", TextureCollection.getInstance().getIndex("experience"));
    }

    public int getExp() {
        return exp;
    }
}
