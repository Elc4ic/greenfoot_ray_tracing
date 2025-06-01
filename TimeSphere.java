import java.io.IOException;

public class TimeSphere extends ObjFile {
    private float floatPosY;
    private float changePosY = 0.1f;
    private float MaxCPosY = 0.5f;


    public TimeSphere(float[] pos, float[] rot, float scale, int textureIndex) throws IOException {
        super(pos, rot, scale, "models\\orb.obj", textureIndex);
    }

}
