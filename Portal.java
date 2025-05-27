import java.io.IOException;

public class Portal extends ObjFile {
    private WorldBase to;

    public Portal(float[] pos, float scale, int color, boolean hasTexture, int textureIndex, WorldBase world) throws IOException {
        super(pos, scale, color, "models\\portal.obj", hasTexture, textureIndex);
        this.to = world;
    }

    WorldBase changeWorld() {
        return to;
    }

    @Override
    public boolean getCollision(float[] pos, float radius) {
        return Math.abs(pos[0] - getPos()[0]) < radius + 1 &&
                Math.abs(pos[1] - getPos()[1]) < radius + 3 &&
                Math.abs(pos[2] - getPos()[2]) < radius + 1;
    }
}
