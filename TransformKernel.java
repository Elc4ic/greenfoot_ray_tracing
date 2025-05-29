import com.aparapi.Kernel;

import java.util.List;

public class TransformKernel extends Kernel {

    private List<Triangle> triangles;
    private List<Integer> objIndexes;
    private float[] positions;
    private float[] rotations;

    public TransformKernel(
            List<Triangle> triangles,
            List<Integer> objI,
            float[] positions,
            float[] rotations
    ) {
        this.triangles = triangles;
        this.objIndexes = objI;
        this.positions = positions;
        this.rotations = rotations;
    }

    @Override
    public void run() {
        int triI = getGlobalId();
        int objI = objIndexes.get(triI);
        Triangle t = triangles.get(triI);

        float posX = positions[objI * ObjFile.POS_SIZE + ObjFile.POS_X];
        float posY = positions[objI * ObjFile.POS_SIZE + ObjFile.POS_Y];
        float posZ = positions[objI * ObjFile.POS_SIZE + ObjFile.POS_Z];

        float angleX = rotations[objI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X];
        float angleY = rotations[objI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_Y];
        float angleZ = rotations[objI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_Z];

        float v0xr = t.v1[0];
        float v0yr = t.v1[1];
        float v0zr = t.v1[2];
        float v1xr = t.v2[0];
        float v1yr = t.v2[1];
        float v1zr = t.v2[2];
        float v2xr = t.v3[0];
        float v2yr = t.v3[1];
        float v2zr = t.v3[2];

        float v0xM = rotateX(v0xr, v0yr, v0zr, angleY, angleZ) + posX;
        float v1xM = rotateX(v1xr, v1yr, v1zr, angleY, angleZ) + posX;
        float v2xM = rotateX(v2xr, v2yr, v2zr, angleY, angleZ) + posX;
        float v0yM = rotateY(v0xr, v0yr, v0zr, angleX, angleZ) + posY;
        float v1yM = rotateY(v1xr, v1yr, v1zr, angleX, angleZ) + posY;
        float v2yM = rotateY(v2xr, v2yr, v2zr, angleX, angleZ) + posY;
        float v0zM = rotateZ(v0xr, v0yr, v0zr, angleX, angleY) + posZ;
        float v1zM = rotateZ(v1xr, v1yr, v1zr, angleX, angleY) + posZ;
        float v2zM = rotateZ(v2xr, v2yr, v2zr, angleX, angleY) + posZ;

        t.v1[0] = v0xM;
        t.v1[1] = v0yM;
        t.v1[2] = v0zM;
        t.v2[0] = v1xM;
        t.v2[1] = v1yM;
        t.v2[2] = v1zM;
        t.v3[0] = v2xM;
        t.v3[1] = v2yM;
        t.v3[2] = v2zM;
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
