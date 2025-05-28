import com.aparapi.Kernel;

public class RasterizerKernel extends Kernel {

    private float[] triangles;
    private int[] objIndexes;
    private float[] positions;
    private float[] rotations;
    private float[] cameraPos;
    private float[] cameraRotations;
    private int[] textureSizes;
    private int[] texture;
    private float fov;
    private int width;
    private int height;

    public int[] output;
    float[] depth_buffer;

    public RasterizerKernel(
            float[] triangles, int[] objIndexes, float[] positions, float[] rotations,
            float[] cameraPos, float[] cameraRotations,
            int[] textureSizes, int[] texture,
            int[] output, float[] depth_buffer,
            float fov, int width, int height
    ) {
        this.triangles = triangles;
        this.objIndexes = objIndexes;
        this.positions = positions;
        this.rotations = rotations;
        this.cameraPos = cameraPos;
        this.cameraRotations = cameraRotations;
        this.textureSizes = textureSizes;
        this.texture = texture;
        this.output = output;
        this.depth_buffer = depth_buffer;
        this.fov = fov;
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
        int objI = objIndexes[tI];

        int txt_offset = textureSizes[txtInfo + TextureCollection.TXT_OFFSET];
        int txt_w = textureSizes[txtInfo + TextureCollection.TXT_W];
        int txt_h = textureSizes[txtInfo + TextureCollection.TXT_H];

        float v0xr = triangles[triI + Triangle.V1_X];
        float v0yr = triangles[triI + Triangle.V1_Y];
        float v0zr = triangles[triI + Triangle.V1_Z];
        float tv0u = triangles[triI + Triangle.V1_U];
        float tv0v = triangles[triI + Triangle.V1_V];

        float v1xr = triangles[triI + Triangle.V2_X];
        float v1yr = triangles[triI + Triangle.V2_Y];
        float v1zr = triangles[triI + Triangle.V2_Z];
        float tv1u = triangles[triI + Triangle.V2_U];
        float tv1v = triangles[triI + Triangle.V2_V];

        float v2xr = triangles[triI + Triangle.V3_X];
        float v2yr = triangles[triI + Triangle.V3_Y];
        float v2zr = triangles[triI + Triangle.V3_Z];
        float tv2u = triangles[triI + Triangle.V3_U];
        float tv2v = triangles[triI + Triangle.V3_V];

        float posX = positions[objI * ObjFile.POS_SIZE + ObjFile.POS_X];
        float posY = positions[objI * ObjFile.POS_SIZE + ObjFile.POS_Y];
        float posZ = positions[objI * ObjFile.POS_SIZE + ObjFile.POS_Z];

        float angleX = rotations[objI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X];
        float angleY = rotations[objI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_Y];
        float angleZ = rotations[objI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_Z];

        float v0xM = rotateX(v0xr, v0yr, v0zr, angleY, angleZ) + posX;
        float v1xM = rotateX(v1xr, v1yr, v1zr, angleY, angleZ) + posX;
        float v2xM = rotateX(v2xr, v2yr, v2zr, angleY, angleZ) + posX;
        float v0yM = rotateY(v0xr, v0yr, v0zr, angleX, angleZ) + posY;
        float v1yM = rotateY(v1xr, v1yr, v1zr, angleX, angleZ) + posY;
        float v2yM = rotateY(v2xr, v2yr, v2zr, angleX, angleZ) + posY;
        float v0zM = rotateZ(v0xr, v0yr, v0zr, angleX, angleY) + posZ;
        float v1zM = rotateZ(v1xr, v1yr, v1zr, angleX, angleY) + posZ;
        float v2zM = rotateZ(v2xr, v2yr, v2zr, angleX, angleY) + posZ;

        float sinX = sin(cameraRotations[0]);
        float cosX = cos(cameraRotations[0]);
        float sinY = sin(cameraRotations[1]);
        float cosY = cos(cameraRotations[1]);

        float v0x = projectX(v0xM, v0yM, v0zM, sinX, cosX, sinY, cosY);
        float v0y = projectY(v0xM, v0yM, v0zM, sinX, cosX, sinY, cosY);
        float v0z = projectZ(v0xM, v0yM, v0zM, sinX, cosX, sinY, cosY);
        float v1x = projectX(v1xM, v1yM, v1zM, sinX, cosX, sinY, cosY);
        float v1y = projectY(v1xM, v1yM, v1zM, sinX, cosX, sinY, cosY);
        float v1z = projectZ(v1xM, v1yM, v1zM, sinX, cosX, sinY, cosY);
        float v2x = projectX(v2xM, v2yM, v2zM, sinX, cosX, sinY, cosY);
        float v2y = projectY(v2xM, v2yM, v2zM, sinX, cosX, sinY, cosY);
        float v2z = projectZ(v2xM, v2yM, v2zM, sinX, cosX, sinY, cosY);

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

                        int txtX = Math.min(txt_w - 1, Math.max(0, (int) (u * txt_w)));
                        int txtY = Math.min(txt_h - 1, Math.max(0, (int) (v * txt_h)));

                        output[x + y * width] = texture[txt_offset + txtY * txt_w + txtX];
                    }
                }
            }
        }
    }

    private float projectX(
            float vx, float vy, float vz,
            float sinX, float cosX, float sinY, float cosY
    ) {
        float dx = vx - cameraPos[0];
        float dy = vy - cameraPos[1];
        float dz = vz - cameraPos[2];

        float x = dx * cosY - dz * sinY;
        float xzZ = dx * sinY + dz * cosY;

        float z = dy * sinX + xzZ * cosX;

        if (z <= Const.EPSILON) z = Const.EPSILON;

        float px = (x * fov / z) * Const.WIDTH / Const.HEIGHT;

        int screenX = (int) ((px + 1) * Const.WIDTH * 0.5f);
        screenX = max(0, min(Const.WIDTH - 1, screenX));

        return screenX;
    }

    private float projectY(
            float vx, float vy, float vz,
            float sinX, float cosX, float sinY, float cosY
    ) {
        float dx = vx - cameraPos[0];
        float dy = vy - cameraPos[1];
        float dz = vz - cameraPos[2];

        float xzZ = dx * sinY + dz * cosY;

        float y = dy * cosX - xzZ * sinX;
        float z = dy * sinX + xzZ * cosX;

        if (z <= Const.EPSILON) z = Const.EPSILON;

        float py = y * fov / z;

        int screenY = (int) ((1 - py) * Const.HEIGHT * 0.5f);
        screenY = max(0, min(Const.HEIGHT - 1, screenY));

        return screenY;
    }

    private float projectZ(
            float vx, float vy, float vz,
            float sinX, float cosX, float sinY, float cosY
    ) {
        float dx = vx - cameraPos[0];
        float dy = vy - cameraPos[1];
        float dz = vz - cameraPos[2];

        float xzZ = dx * sinY + dz * cosY;
        float z = dy * sinX + xzZ * cosX;

        if (z <= Const.EPSILON) z = Const.EPSILON;

        return z;
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
