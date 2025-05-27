
public class Camera {
    private float[] pos = new float[]{-1, 1, 0};
    private float[] rotations = new float[]{0, (float) Math.toRadians(45), 0};
    int renderDistObjects = 9;
    int renderDistLight = 7;
    float viewportHeight;
    float viewportWidth;
    float aspect = Const.WIDTH / Const.HEIGHT;
    float fov;

    public Camera(float fov) {
        this.fov = fov;
        this.viewportHeight = 2f * (float) Math.tan(Math.toRadians(fov / 2.0));
        this.viewportWidth = viewportHeight * Const.WIDTH / Const.HEIGHT;
    }

    public float[] getRotations() {
        return rotations;
    }

    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public void setRotations(float x, float y, float z) {
        rotations[0] += (float) Math.toRadians(x);
        rotations[1] += (float) Math.toRadians(y);
        rotations[2] += (float) Math.toRadians(z);
    }

    float[] getOffset(float[] normal, float u, float v) {
        float[] right = Vector3.normalize(Vector3.cross(normal, new float[]{0, 1, 0}));
        float[] up = Vector3.normalize(Vector3.cross(right, normal));
        return Vector3.add(Vector3.scale(right, u * viewportWidth), Vector3.scale(up, v * viewportHeight));
    }

    boolean needRenderObject(float[] objPos) {
        return Vector3.length(Vector3.subtract(pos, objPos)) < renderDistObjects;
    }

    boolean needRenderLight(float[] objPos) {
        return Vector3.length(Vector3.subtract(pos, objPos)) < renderDistLight;
    }

}
