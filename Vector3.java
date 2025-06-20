
public class Vector3 {

    public static float dot(float[] v, float[] v2) {
        return v[0] * v2[0] + v[1] * v2[1] + v[2] * v2[2];
    }

    public static float[] cross(float[] v, float[] v2) {
        return new float[]{
                v[1] * v2[2] - v[2] * v2[1],
                v[2] * v2[0] - v[0] * v2[2],
                v[0] * v2[1] - v[1] * v2[0]};
    }

    public static float length(float[] v) {
        return (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    static void rotateX(float[] v, float angle) {
        rotateXr(v, (float) Math.toRadians(angle));
    }

    static void rotateY(float[] v, float angle) {
        rotateYr(v, (float) Math.toRadians(angle));
    }

    static void rotateZ(float[] v, float angle) {
        rotateZr(v, (float) Math.toRadians(angle));
    }

    static void rotateXr(float[] v, float rad) {
        float y = v[1];
        float z = v[2];
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        v[1] = y * cos - z * sin;
        v[2] = y * sin + z * cos;
    }

    static void rotateYr(float[] v, float rad) {
        float x = v[0];
        float z = v[2];
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        v[0] = x * cos + z * sin;
        v[2] = -x * sin + z * cos;
    }

    static void rotateZr(float[] v, float rad) {
        float x = v[1];
        float y = v[2];
        float cos = (float) Math.cos(rad);
        float sin = (float) Math.sin(rad);
        v[0] = x * cos - y * sin;
        v[1] = x * sin + y * cos;
    }

    static void rotate(float[] v, float[] rotation) {
//        Vector3.rotateZr(v, rotation[2]);
        Vector3.rotateYr(v, rotation[1]);
        Vector3.rotateXr(v, rotation[0]);
    }

    public static float[] normalize(float[] v) {
        float magnitude = length(v);
        return new float[]{v[0] / magnitude, v[1] / magnitude, v[2] / magnitude};
    }

    static void clear(float[] v) {
        v[0] = 0;
        v[1] = 0;
        v[2] = 0;
    }

    static void setAdsMaxX(float[] v, float xx) {
        v[0] = Math.abs(v[0]) > Math.abs(xx) ? v[0] : xx;
    }

    static void setAdsMaxY(float[] v, float yy) {
        v[1] = Math.abs(v[1]) > Math.abs(yy) ? v[1] : yy;
    }

    static void setAdsMaxZ(float[] v, float zz) {
        v[2] = Math.abs(v[2]) > Math.abs(zz) ? v[2] : zz;
    }

    public static float[] add(float[] v, float[] v2) {
        return new float[]{v[0] + v2[0], v[1] + v2[1], v[2] + v2[2]};
    }

    public static float[] minus(float[] v, float[] v2) {
        return new float[]{v[0] - v2[0], v[1] - v2[1], v[2] - v2[2]};
    }

    public static float[] subtract(float[] v, float[] v2) {
        return new float[]{v[0] - v2[0], v[1] - v2[1], v[2] - v2[2]};
    }

    public static float[] scale(float[] v, float sf) {
        return new float[]{v[0] * sf, v[1] * sf, v[2] * sf};
    }

}






























