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


    Triangle transform(List<WorldObject> objects) {
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
        Vector3.rotate(nv, rotor);
        nv[0] += pos[0];
        nv[1] += pos[1];
        nv[2] += pos[2];
        return nv;
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

        z = Math.max(0.1f, z);

        float x = xzX * camera.cosZ() - yzY * camera.sinZ();
        float y = xzX * camera.sinZ() + yzY * camera.cosZ();

        float invZ = 1f / z;

        float px = (x * invZ) * camera.fov;
        float py = (y * invZ) * camera.fov;

        int screenX = Math.round((px + 1f) * 0.5f * Const.WIDTH);
        int screenY = Math.round((1f - py) * 0.5f * Const.HEIGHT);

        return new float[]{screenX, screenY, z, v[3], v[4]};
    }

    List<Triangle> clip() {
        float NEAR_PLANE = Const.EPSILON;
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
        float za = a[2];
        float zb = b[2];
        float t = (nearZ - za) / (zb - za);

        float invZa = 1f / za;
        float invZb = 1f / zb;
        float invZ_interp = invZa + (invZb - invZa) * t;

        float ua = a[3];
        float ub = b[3];
        float va = a[4];
        float vb = b[4];

        float u_interp = ((ua / za) + t * ((ub / zb) - (ua / za))) / invZ_interp;
        float v_interp = ((va / za) + t * ((vb / zb) - (va / za))) / invZ_interp;

        float x = a[0] + (b[0] - a[0]) * t;
        float y = a[1] + (b[1] - a[1]) * t;

        return new float[]{x, y, nearZ, u_interp, v_interp};
    }

    public void rasterize(float[] depth_buffer, int[] output) {
        Texture txt = TextureCollection.getInstance().getTexture(textureIndex);
        int[] texture = txt.textureBuff;
        int txt_w = txt.textureWidth;
        int txt_h = txt.textureHeight;

        float v0x = v1[0];
        float v0y = v1[1];
        float v0z = v1[2];
        float tv0u = v1[3];
        float tv0v = v1[4];

        float v1x = v2[0];
        float v1y = v2[1];
        float v1z = v2[2];
        float tv1u = v2[3];
        float tv1v = v2[4];

        float v2x = v3[0];
        float v2y = v3[1];
        float v2z = v3[2];
        float tv2u = v3[3];
        float tv2v = v3[4];

        int minX = (int) Math.max(0, Math.min(v0x, Math.min(v1x, v2x)));
        int maxX = (int) Math.min(Const.WIDTH - 1, Math.max(v0x, Math.max(v1x, v2x)));
        int minY = (int) Math.max(0, Math.min(v0y, Math.min(v1y, v2y)));
        int maxY = (int) Math.min(Const.HEIGHT - 1, Math.max(v0y, Math.max(v1y, v2y)));

        float denom = (v1y - v2y) * (v0x - v2x) + (v2x - v1x) * (v0y - v2y);

        if (Math.abs(denom) > Const.EPSILON) {

            float u0 = tv0u / v0z;
            float u1 = tv1u / v1z;
            float u2 = tv2u / v2z;

            float v0_ = tv0v / v0z;
            float v1_ = tv1v / v1z;
            float v2_ = tv2v / v2z;

            float oneOverZ0 = 1f / v0z;
            float oneOverZ1 = 1f / v1z;
            float oneOverZ2 = 1f / v2z;

            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {

                    float bu = ((v1y - v2y) * (x - v2x) + (v2x - v1x) * (y - v2y)) / denom;
                    float bv = ((v2y - v0y) * (x - v2x) + (v0x - v2x) * (y - v2y)) / denom;
                    float bw = 1f - bu - bv;

                    if (bu >= 0 && bv >= 0 && bw >= 0) {
                        float z = bu * v0z + bv * v1z + bw * v2z;
                        if (z < depth_buffer[x + y * Const.WIDTH]) {
                            depth_buffer[x + y * Const.WIDTH] = z;

                            float oneOverZ = bu * oneOverZ0 + bv * oneOverZ1 + bw * oneOverZ2;
                            float u = (bu * u0 + bv * u1 + bw * u2) / oneOverZ;
                            float v = (bu * v0_ + bv * v1_ + bw * v2_) / oneOverZ;

                            int txtX = Math.min(txt_w - 1, Math.max(0, (int) (u * txt_w)));
                            int txtY = Math.min(txt_h - 1, Math.max(0, (int) (v * txt_h)));

                            int color = texture[txtY * txt_w + txtX];
                            if ((color & 0xFF000000) != 0) {
                                output[x + y * Const.WIDTH] = color;
                            }
                        }
                    }
                }
            }
        }

    }
}
