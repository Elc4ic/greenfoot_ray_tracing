import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Triangle {

    public static final int SIZE = 15;

    public static final int V1_X = 0;
    public static final int V1_Y = 1;
    public static final int V1_Z = 2;
    public static final int V1_U = 3;
    public static final int V1_V = 4;
    public static final int V2_X = 5;
    public static final int V2_Y = 6;
    public static final int V2_Z = 7;
    public static final int V2_U = 8;
    public static final int V2_V = 9;
    public static final int V3_X = 10;
    public static final int V3_Y = 11;
    public static final int V3_Z = 12;
    public static final int V3_U = 13;
    public static final int V3_V = 14;

    float[] v1, v2, v3;
    int textureIndex;
    private int objIndex;

    public Triangle(float[] v1, float[] v2, float[] v3, int textureIndex) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.textureIndex = textureIndex;
    }

    public void setObjIndex(int objIndex) {
        this.objIndex = objIndex;
    }

    float[] toFloatArray() {
        return new float[]{v1[0], v1[1], v1[2], v1[3], v1[4], v2[0], v2[1], v2[2], v2[3], v2[4], v3[0], v3[1], v3[2], v3[3], v3[4]};
    }

    Triangle flat(Camera camera) {
        return new Triangle(
                flatVertex(v1, camera),
                flatVertex(v2, camera),
                flatVertex(v3, camera),
                textureIndex);
    }

    float[] flatVertex(float[] v, Camera camera) {
        float dx = v[0] - camera.getPos()[0];
        float dy = v[1] - camera.getPos()[1];
        float dz = v[2] - camera.getPos()[2];

        float xzX = dx * camera.cosY() - dz * camera.sinY();
        float xzZ = dx * camera.sinY() + dz * camera.cosY();

        float yzY = dy * camera.cosX() - xzZ * camera.sinX();
        float z = dy * camera.sinX() + xzZ * camera.cosX();

        float x = xzX * camera.cosZ() - yzY * camera.sinZ();
        float y = xzX * camera.sinZ() + yzY * camera.cosZ();

        if (z <= Const.EPSILON) z = Const.EPSILON;

        float px = (x / z) * camera.fov * Const.WIDTH / Const.HEIGHT;
        float py = (y / z) * camera.fov;

        int screenX = (int) ((px + 1) * Const.WIDTH * 0.5f);
        int screenY = (int) ((1 - py) * Const.HEIGHT * 0.5f);
        screenX = Math.max(0, Math.min(Const.WIDTH - 1, screenX));
        screenY = Math.max(0, Math.min(Const.HEIGHT - 1, screenY));

        return new float[]{screenX, screenY, z, v[3], v[4]};
    }

    Triangle transform(List<WorldObject> objects) {
        System.out.println(objIndex);
        float[] pos = objects.get(objIndex).getPos();
        float[] rotor = objects.get(objIndex).getRotation();
        return new Triangle(
                transformVertex(v1, pos, rotor),
                transformVertex(v2, pos, rotor),
                transformVertex(v3, pos, rotor),
                textureIndex
        );
    }

    float[] transformVertex(float[] v, float[] pos, float[] rotor) {
        float[] nv = new float[]{v[0], v[1], v[2], v[3], v[4]};
//        Vector3.rotate(nv, rotor);
        nv[0] += pos[0];
        nv[1] += pos[1];
        nv[2] += pos[2];
        return nv;
    }

    List<Triangle> clip(float NEAR_PLANE) {
        List<float[]> inside = new ArrayList<>();
        List<float[]> outside = new ArrayList<>();

        if (v1[2] >= NEAR_PLANE) inside.add(v1);
        else outside.add(v1);
        if (v2[2] >= NEAR_PLANE) inside.add(v2);
        else outside.add(v2);
        if (v3[2] >= NEAR_PLANE) inside.add(v3);
        else outside.add(v3);

        if (inside.isEmpty()) {
            return Collections.emptyList();
        } else if (inside.size() == 3) {
            return List.of(this);
        } else if (inside.size() == 1) {
            float[] a = inside.get(0);
            float[] b = interpolate(a, outside.get(0), NEAR_PLANE);
            float[] c = interpolate(a, outside.get(1), NEAR_PLANE);
            return List.of(new Triangle(a, b, c, textureIndex));
        } else {
            float[] a = inside.get(0);
            float[] b = inside.get(1);
            float[] c = interpolate(a, outside.get(0), NEAR_PLANE);
            float[] d = interpolate(b, outside.get(0), NEAR_PLANE);

            return List.of(
                    new Triangle(a, b, c, textureIndex),
                    new Triangle(b, d, c, textureIndex)
            );
        }
    }

    private static float[] interpolate(float[] a, float[] b, float nearZ) {
        float t = (nearZ - a[2]) / (b[2] - a[2]);
        return new float[]{
                a[0] + (b[0] - a[0]) * t,
                a[1] + (b[1] - a[1]) * t,
                nearZ,
                a[3] + (b[3] - a[3]) * t,
                a[4] + (b[4] - a[4]) * t
        };
    }
}
