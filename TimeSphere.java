import java.io.IOException;

public class TimeSphere extends ObjFile {
    private long plusTime;
    private float floatPosY;
    private float changePosY = 0.1f;
    private float MaxCPosY = 0.5f;


    public TimeSphere(float[] pos, float scale, int color, boolean hasTexture, int textureIndex, long plusTime) throws IOException {
        super(pos, scale, color, "models\\orb.obj", hasTexture, textureIndex);
        this.plusTime = plusTime;
    }

    public long takeTime() {
        return plusTime;
    }

    @Override
    public boolean getCollision(float[] pos, float r) {
        float len = Vector3.length(Vector3.subtract(pos, getPos()));
        return len < (0.5f + r) * (0.5f + r);
    }
}
