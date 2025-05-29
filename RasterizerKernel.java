import com.aparapi.Kernel;

public class RasterizerKernel extends Kernel {

    private float[] triangles;
    private int[] textureSizes;
    private int[] texture;
    private int width;
    private int height;

    public int[] output;
    float[] depth_buffer;

    public RasterizerKernel(
            float[] triangles,
            int[] textureSizes, int[] texture,
            int[] output, float[] depth_buffer,
            int width, int height
    ) {
        this.triangles = triangles;
        this.textureSizes = textureSizes;
        this.texture = texture;
        this.output = output;
        this.depth_buffer = depth_buffer;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        int gid = getGlobalId();
        rasterizeTriangle(gid);
    }

    void rasterizeTriangle(int tI) {
        int triI = tI * Triangle.SIZE;
        int txtInfo = tI * TextureCollection.INFO_SIZE;

        int txt_offset = textureSizes[txtInfo + TextureCollection.TXT_OFFSET];
        int txt_w = textureSizes[txtInfo + TextureCollection.TXT_W];
        int txt_h = textureSizes[txtInfo + TextureCollection.TXT_H];

        float v0x = triangles[triI + Triangle.V1_X];
        float v0y = triangles[triI + Triangle.V1_Y];
        float v0z = triangles[triI + Triangle.V1_Z];
        float tv0u = triangles[triI + Triangle.V1_U];
        float tv0v = triangles[triI + Triangle.V1_V];

        float v1x = triangles[triI + Triangle.V2_X];
        float v1y = triangles[triI + Triangle.V2_Y];
        float v1z = triangles[triI + Triangle.V2_Z];
        float tv1u = triangles[triI + Triangle.V2_U];
        float tv1v = triangles[triI + Triangle.V2_V];

        float v2x = triangles[triI + Triangle.V3_X];
        float v2y = triangles[triI + Triangle.V3_Y];
        float v2z = triangles[triI + Triangle.V3_Z];
        float tv2u = triangles[triI + Triangle.V3_U];
        float tv2v = triangles[triI + Triangle.V3_V];

        int minX = (int) max(0, min(v0x, min(v1x, v2x)));
        int maxX = (int) min(width - 1, max(v0x, max(v1x, v2x)));
        int minY = (int) max(0, min(v0y, min(v1y, v2y)));
        int maxY = (int) min(height - 1, max(v0y, max(v1y, v2y)));

        float denom = (v1y - v2y) * (v0x - v2x) + (v2x - v1x) * (v0y - v2y);
        if (abs(denom) > Const.EPSILON) {
            for (int y = minY; y <= maxY; y++) {
                for (int x = minX; x <= maxX; x++) {
                    float bu = ((v1y - v2y) * (x - v2x) + (v2x - v1x) * (y - v2y)) / denom;
                    float bv = ((v2y - v0y) * (x - v2x) + (v0x - v2x) * (y - v2y)) / denom;
                    float bw = 1 - bu - bv;

                    if (bu > 0 && bv > 0 && bw > 0) {
                        float z = bu * v0z + bv * v1z + bw * v2z;
                        if (z < depth_buffer[x + y * width]) {
                            depth_buffer[x + y * width] = z;

                            float u = tv0u * bu + tv1u * bv + tv2u * bw;
                            float v = tv0v * bu + tv1v * bv + tv2v * bw;

                            int txtX = Math.min(txt_w - 1, Math.max(0, (int) (u * txt_w)));
                            int txtY = Math.min(txt_h - 1, Math.max(0, (int) (v * txt_h)));

                            output[x + y * width] = texture[txt_offset + txtY * txt_w + txtX];
                        }
                    }
                }
            }
        }
    }

}
