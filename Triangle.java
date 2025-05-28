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

    public Triangle(float[] v1, float[] v2, float[] v3, int textureIndex) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.textureIndex = textureIndex;
    }

    float[] toFloatArray() {
        return new float[]{v1[0], v1[1], v1[2], v1[3], v1[4], v2[0], v2[1], v2[2], v2[3], v2[4], v3[0], v3[1], v3[2], v3[3], v3[4]};
    }
    
}
