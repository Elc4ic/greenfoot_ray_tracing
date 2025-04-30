import com.aparapi.Kernel;

public class RayTracerKernel extends Kernel {
    private float[] positions;
    private float[] rotations;
    private int[] sizes;
    private float[] bounds;
    private float[] cache;
    private float[] vertices;
    private float[] texCoords;
    private int[] leafInsides;

    private int[] texture;

    private float[] rays;
    public int[] output;

    public RayTracerKernel(
            float[] positions, float[] rotations, int[] sizes, float[] bounds, float[] cache,
            float[] vertices, float[] texCoords, int[] leafInsides,
            int[] texture, int[] output, float[] rays
    ) {
        this.sizes = sizes;
        this.bounds = bounds;
        this.cache = cache;
        this.leafInsides = leafInsides;
        this.vertices = vertices;
        this.texCoords = texCoords;
        this.texture = texture;
        this.rays = rays;
        this.output = output;
        this.positions = positions;
        this.rotations = rotations;
    }

    @Override
    public void run() {
        int gid = getGlobalId();

        float ox = rays[gid * Ray.RAY_SIZE + Ray.ORIGIN_X];
        float oy = rays[gid * Ray.RAY_SIZE + Ray.ORIGIN_Y];
        float oz = rays[gid * Ray.RAY_SIZE + Ray.ORIGIN_Z];
        float dx = rays[gid * Ray.RAY_SIZE + Ray.DIRECTION_X];
        float dy = rays[gid * Ray.RAY_SIZE + Ray.DIRECTION_Y];
        float dz = rays[gid * Ray.RAY_SIZE + Ray.DIRECTION_Z];

        int col = traceRay(ox, oy, oz, dx, dy, dz);

        output[gid] = col;
    }

    private int traceRay(float ox, float oy, float oz, float dx, float dy, float dz) {

        int col = 0xffffff;
        float closest = 3.4028235E38f;

        for (int oI = 0; oI < sizes.length / ObjFile.SIZES_SIZE; oI++) {

            int closestFace = -1;

            if (intersectAABB(oI, ox, oy, oz, dx, dy, dz)) {
                int faceCount = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.FACE_COUNT_OFFSET];
                for (int i = 0; i < faceCount; i++) {
                    int tI = i * ObjFile.FACE_SIZE;
                    float t = intersectTriangle(oI, tI, ox, oy, oz, dx, dy, dz);
                    if (t > 0.001f && t < closest) {
                        closest = t;
                        closestFace = tI;
                    }
                }
            }
            if (closestFace != -1) {
                col = getColorFromTexture(oI, closestFace, ox, oy, oz, dx, dy, dz, closest);
            }

        }
//        add light calculation
        return col;
    }

    private boolean intersectAABB(int oI, float ox, float oy, float oz, float dx, float dy, float dz) {
        float posX = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_X];
        float posY = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_Y];
        float posZ = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_Z];

        int boundIndex = oI * ObjFile.BOUND_SIZE;

        float minX = bounds[boundIndex + ObjFile.BVH_BOUND_MIN_X] + posX;

        float minY = bounds[boundIndex + ObjFile.BVH_BOUND_MIN_Y] + posY;
        float minZ = bounds[boundIndex + ObjFile.BVH_BOUND_MIN_Z] + posZ;
        float maxX = bounds[boundIndex + ObjFile.BVH_BOUND_MAX_X] + posX;
        float maxY = bounds[boundIndex + ObjFile.BVH_BOUND_MAX_Y] + posY;
        float maxZ = bounds[boundIndex + ObjFile.BVH_BOUND_MAX_Z] + posZ;

        minX = rotateXx(minX, minZ, rotations[oI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X]);
        minZ = rotateXz(minX, minZ, rotations[oI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X]);

        maxX = rotateXx(maxX, maxZ, rotations[oI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X]);
        maxZ = rotateXz(maxX, maxZ, rotations[oI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X]);

        float t1 = (minX - ox) / dx;
        float t2 = (maxX - ox) / dx;
        float tmin = min(t1, t2);
        float tmax = max(t1, t2);

        t1 = (minY - oy) / dy;
        t2 = (maxY - oy) / dy;
        tmin = max(tmin, min(t1, t2));
        tmax = min(tmax, max(t1, t2));

        t1 = (minZ - oz) / dz;
        t2 = (maxZ - oz) / dz;
        tmin = max(tmin, min(t1, t2));
        tmax = min(tmax, max(t1, t2));

        return tmax >= max(tmin, 0.0f);
    }

    private float intersectTriangle(int oI, int tI, float ox, float oy, float oz, float dx, float dy, float dz) {
        float t = -1f;

        float posX = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_X];
        float posY = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_Y];
        float posZ = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_Z];

        int off_V = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.VERTEX_OFFSET] * ObjFile.VERTEX_SIZE;
        int off_L = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET];
        int c_I = (off_L + tI) / ObjFile.FACE_SIZE * ObjFile.CACHE_SIZE;

        int v1I = leafInsides[off_L + tI + ObjFile.VERTEX_1_INDEX] * ObjFile.VERTEX_SIZE;

        float v0x = vertices[off_V + v1I + ObjFile.VERTEX_X] + posX;
        float v0y = vertices[off_V + v1I + ObjFile.VERTEX_Y] + posY;
        float v0z = vertices[off_V + v1I + ObjFile.VERTEX_Z] + posZ;

        v0x = rotateXx(v0x, v0z, rotations[oI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X]);
        v0z = rotateXz(v0x, v0z, rotations[oI * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X]);

        float e1x = cache[c_I + ObjFile.EDGE_1_X];
        float e1y = cache[c_I + ObjFile.EDGE_1_Y];
        float e1z = cache[c_I + ObjFile.EDGE_1_Z];
        float e2x = cache[c_I + ObjFile.EDGE_2_X];
        float e2y = cache[c_I + ObjFile.EDGE_2_Y];
        float e2z = cache[c_I + ObjFile.EDGE_2_Z];

        float nx = cache[c_I + ObjFile.NORMAL_X];
        float ny = cache[c_I + ObjFile.NORMAL_Y];
        float nz = cache[c_I + ObjFile.NORMAL_Z];

        if (dx * nx + dy * ny + nz * dz < 0) {
            float px = dy * e2z - dz * e2y;
            float py = dz * e2x - dx * e2z;
            float pz = dx * e2y - dy * e2x;

            float det = e1x * px + e1y * py + e1z * pz;
            if (!(det > -1e-6f && det < 1e-6f)) {
                float invDet = 1f / det;
                float sx = ox - v0x, sy = oy - v0y, sz = oz - v0z;
                float u = (sx * px + sy * py + sz * pz) * invDet;
                if (!(u < 0f || u > 1f)) {
                    float qx = sy * e1z - sz * e1y;
                    float qy = sz * e1x - sx * e1z;
                    float qz = sx * e1y - sy * e1x;

                    float v = (dx * qx + dy * qy + dz * qz) * invDet;
                    if (!(v < 0f || u + v > 1f)) {
                        t = (e2x * qx + e2y * qy + e2z * qz) * invDet;
                    }
                }
            }
        }
        return t <= 0.001f ? -1f : t;
    }

    private int getColorFromTexture(int oI, int tI, float ox, float oy, float oz, float dx, float dy, float dz, float t) {
        float posX = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_X];
        float posY = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_Y];
        float posZ = positions[oI * ObjFile.POS_SIZE + ObjFile.POS_Z];

        int o_V = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.VERTEX_OFFSET] * ObjFile.VERTEX_SIZE;
        int o_TC = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXT_COORD_OFFSET] * ObjFile.TEXTURE_COORD_SIZE;
        int o_L = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET];
        int o_T = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_OFFSET];
        int c_I = (o_L + tI) / ObjFile.FACE_SIZE * ObjFile.CACHE_SIZE;

        int v1I = leafInsides[o_L + tI + ObjFile.VERTEX_1_INDEX] * ObjFile.VERTEX_SIZE;

        int tv0I = leafInsides[o_L + tI + ObjFile.TEXTURE_COORD_1_INDEX] * ObjFile.TEXTURE_COORD_SIZE;
        int tv1I = leafInsides[o_L + tI + ObjFile.TEXTURE_COORD_2_INDEX] * ObjFile.TEXTURE_COORD_SIZE;
        int tv2I = leafInsides[o_L + tI + ObjFile.TEXTURE_COORD_3_INDEX] * ObjFile.TEXTURE_COORD_SIZE;

        float v0x = vertices[o_V + v1I + ObjFile.VERTEX_X] + posX;
        float v0y = vertices[o_V + v1I + ObjFile.VERTEX_Y] + posY;
        float v0z = vertices[o_V + v1I + ObjFile.VERTEX_Z] + posZ;

        v0x = rotateXx(v0x, v0z, rotations[oI * ObjFile.POS_SIZE + ObjFile.POS_X]);
        v0z = rotateXz(v0x, v0z, rotations[oI * ObjFile.POS_SIZE + ObjFile.POS_X]);

        float hitX = ox + t * dx;
        float hitY = oy + t * dy;
        float hitZ = oz + t * dz;

        float e1x = cache[c_I + ObjFile.EDGE_1_X];
        float e1y = cache[c_I + ObjFile.EDGE_1_Y];
        float e1z = cache[c_I + ObjFile.EDGE_1_Z];
        float e2x = cache[c_I + ObjFile.EDGE_2_X];
        float e2y = cache[c_I + ObjFile.EDGE_2_Y];
        float e2z = cache[c_I + ObjFile.EDGE_2_Z];

        float v0px = hitX - v0x, v0py = hitY - v0y, v0pz = hitZ - v0z;

        float d00 = cache[c_I + ObjFile.D00];
        float d01 = cache[c_I + ObjFile.D01];
        float d11 = cache[c_I + ObjFile.D11];
        float d20 = v0px * e1x + v0py * e1y + v0pz * e1z;
        float d21 = v0px * e2x + v0py * e2y + v0pz * e2z;

        float denom = cache[c_I + ObjFile.DENOMINATOR];
        float bv = (d11 * d20 - d01 * d21) / denom;
        float bw = (d00 * d21 - d01 * d20) / denom;
        float bu = 1.0f - bv - bw;
        //u,v,w
        float u = texCoords[o_TC + tv0I + ObjFile.TEXTURE_COORD_U] * bu + texCoords[o_TC + tv1I + ObjFile.TEXTURE_COORD_U] * bv + texCoords[o_TC + tv2I + ObjFile.TEXTURE_COORD_U] * bw;
        float v = texCoords[o_TC + tv0I + ObjFile.TEXTURE_COORD_V] * bu + texCoords[o_TC + tv1I + ObjFile.TEXTURE_COORD_V] * bv + texCoords[o_TC + tv2I + ObjFile.TEXTURE_COORD_V] * bw;

        u = u - floor(u);
        u = (u < 0) ? u + 1f : u;

        v = v - floor(v);
        v = (v < 0) ? v + 1f : v;

        int w = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_WIDTH];
        int h = sizes[oI * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_HEIGHT];
        int x = (int) (u * (w - 1));
        int y = (int) ((1f - v) * (h - 1));
        return texture[o_T + y * w + x];
    }

    float rotateXx(float x, float z, float angle) {
        float cos = cos(toRadians(angle));
        float sin = sin(toRadians(angle));
        return x * cos - z * sin;
    }

    float rotateXz(float x, float z, float angle) {
        float cos = cos(toRadians(angle));
        float sin = sin(toRadians(angle));
        return x * sin + z * cos;
    }

    float rotateYy(float y, float z, float angle) {
        float cos = cos(toRadians(angle));
        float sin = sin(toRadians(angle));
        return y * cos - z * sin;
    }

    float rotateYz(float y, float z, float angle) {
        float cos = cos(toRadians(angle));
        float sin = sin(toRadians(angle));
        return y * sin + z * cos;
    }

    float rotateZx(float x, float y, float angle) {
        float cos = cos(toRadians(angle));
        float sin = sin(toRadians(angle));
        return x * cos + y * sin;
    }

    float rotateZy(float x, float y, float angle) {
        float cos = cos(toRadians(angle));
        float sin = sin(toRadians(angle));
        return x * sin + y * cos; //возможно тут -x или -y
    }

    float length(float[] v) {
        return sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
    }

    void normalize(float[] v, float[] result) {
        float len = length(v);
        result[0] = v[0] / len;
        result[1] = v[1] / len;
        result[2] = v[2] / len;
    }
}

//stack = new int[258];

//int stackIndex = 0;
//int next_o_N_B = (oI + 1 == objects)
//        ? bvhBounds.length / 6-1
//        : sizes[(oI + 1) * ObjFile.SIZES_SIZE + ObjFile.NODES_OFFSET];
//int go_node = next_o_N_B - offset_N_B;
//
//stack[stackIndex++] = 0;
//
//        for (int j = 0; j < go_node; j++) {
//        if (stackIndex > 0) {
//int o_BVH = stack[--stackIndex];

