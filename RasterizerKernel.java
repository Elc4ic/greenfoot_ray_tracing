import com.aparapi.Kernel;

public class RasterizerKernel extends Kernel {
    private int[] sizes;
    private float[] vertices;
    private float[] texCoords;
    private int[] leafInsides;
    private int[] texture;

    public int[] output;
    private int width;
    private int height;

    float[] projectedVertices;
    float[] depth_buffer;

    public RasterizerKernel(
            int[] sizes, float[] vertices, float[] texCoords, int[] leafInsides, float[] projected,
            int[] texture, int[] output, float[] depth_buffer, int width, int height
    ) {
        this.sizes = sizes;
        this.leafInsides = leafInsides;
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.texture = texture;
        this.projectedVertices = projected;
        this.output = output;
        this.depth_buffer = depth_buffer;
        this.width = width;
        this.height = height;
    }

    @Override
    public void run() {
        int gid = getGlobalId();

        int faceCount = sizes[gid * ObjFile.SIZES_SIZE + ObjFile.FACE_COUNT_OFFSET];
        for (int i = 0; i < faceCount; i++) {
            int tI = i * ObjFile.FACE_SIZE;
            rasterizeTriangle(gid, tI);
        }
    }

    void rasterizeTriangle(int oI, int tI) {
        int o_V = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.VERTEX_OFFSET] * ObjFile.VERTEX_SIZE;
        int o_TC = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXT_COORD_OFFSET] * ObjFile.TEXTURE_COORD_SIZE;
        int o_L = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET];
        int o_T = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_OFFSET];

        int v1I = leafInsides[o_L + tI + ObjFile.VERTEX_1_INDEX] * ObjFile.VERTEX_SIZE;
        int v2I = leafInsides[o_L + tI + ObjFile.VERTEX_2_INDEX] * ObjFile.VERTEX_SIZE;
        int v3I = leafInsides[o_L + tI + ObjFile.VERTEX_3_INDEX] * ObjFile.VERTEX_SIZE;

        int tv0I = leafInsides[o_L + tI + ObjFile.TEXTURE_COORD_1_INDEX] * ObjFile.TEXTURE_COORD_SIZE;
        int tv1I = leafInsides[o_L + tI + ObjFile.TEXTURE_COORD_2_INDEX] * ObjFile.TEXTURE_COORD_SIZE;
        int tv2I = leafInsides[o_L + tI + ObjFile.TEXTURE_COORD_3_INDEX] * ObjFile.TEXTURE_COORD_SIZE;

        float v0x = projectedVertices[o_V + v1I + ObjFile.VERTEX_X];
        float v0y = projectedVertices[o_V + v1I + ObjFile.VERTEX_Y];
        float v0z = projectedVertices[o_V + v1I + ObjFile.VERTEX_Z];
        float v1x = projectedVertices[o_V + v2I + ObjFile.VERTEX_X];
        float v1y = projectedVertices[o_V + v2I + ObjFile.VERTEX_Y];
        float v1z = projectedVertices[o_V + v2I + ObjFile.VERTEX_Z];
        float v2x = projectedVertices[o_V + v3I + ObjFile.VERTEX_X];
        float v2y = projectedVertices[o_V + v3I + ObjFile.VERTEX_Y];
        float v2z = projectedVertices[o_V + v3I + ObjFile.VERTEX_Z];

        float tv0u = texCoords[o_TC + tv0I + ObjFile.TEXTURE_COORD_U];
        float tv0v = texCoords[o_TC + tv0I + ObjFile.TEXTURE_COORD_V];
        float tv1u = texCoords[o_TC + tv1I + ObjFile.TEXTURE_COORD_U];
        float tv1v = texCoords[o_TC + tv1I + ObjFile.TEXTURE_COORD_V];
        float tv2u = texCoords[o_TC + tv2I + ObjFile.TEXTURE_COORD_U];
        float tv2v = texCoords[o_TC + tv2I + ObjFile.TEXTURE_COORD_V];

        int minX = max(0, (int) min(v0x, min(v1x, v2x)));
        int maxX = min(width - 1, (int) max(v0x, max(v1x, v2x)));
        int minY = max(0, (int) min(v0y, min(v1y, v2y)));
        int maxY = min(height - 1, (int) max(v0y, max(v1y, v2y)));

        float denom = (v1y - v2y) * (v0x - v2x) + (v2x - v1x) * (v0y - v2y);
        if (abs(denom) < Const.EPSILON) return;

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

                        int w = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_WIDTH];
                        int h = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_HEIGHT];
                        int texX = Math.min(w - 1, Math.max(0, (int) (u * w)));
                        int texY = Math.min(h - 1, Math.max(0, (int) (v * h)));

                        output[x + y * width] = texture[o_T + texY * w + texX];
                    }
                }
            }
        }
    }

    float rotateX(float x, float y, float z, float angleY, float angleZ) {
        float xr = rotateYx(x, z, angleY);
        return rotateZx(xr, y, angleZ);
    }

    float rotateY(float x, float y, float z, float angleX, float angleZ) {
        float yr = rotateXy(y, z, angleX);
        return rotateZy(x, yr, angleZ);
    }

    float rotateZ(float x, float y, float z, float angleX, float angleY) {
        float zr = rotateXz(y, z, angleX);
        return rotateYz(x, zr, angleY);
    }

    float rotateXy(float y, float z, float angle) {
        float coss = cos(angle);
        float sinn = sin(angle);
        return y * coss - z * sinn;
    }

    float rotateXz(float y, float z, float angle) {
        float coss = cos(angle);
        float sinn = sin(angle);
        return y * sinn + z * coss;
    }

    float rotateYx(float x, float z, float angle) {
        float coss = cos(angle);
        float sinn = sin(angle);
        return x * coss - z * sinn;
    }

    float rotateYz(float x, float z, float angle) {
        float coss = cos(angle);
        float sinn = sin(angle);
        return x * sinn + z * coss;
    }

    float rotateZx(float x, float y, float angle) {
        float coss = cos(angle);
        float sinn = sin(angle);
        return x * coss + y * sinn;
    }

    float rotateZy(float x, float y, float angle) {
        float coss = cos(angle);
        float sinn = sin(angle);
        return -x * sinn + y * coss;
    }

}
