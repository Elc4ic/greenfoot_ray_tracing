import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjFile extends WorldObject {

    private final List<float[]> vertices = new ArrayList<>();
    private final List<float[]> texCoords = new ArrayList<>();
    private final List<Integer> leafIndices = new ArrayList<>();
    private final List<float[]> cache = new ArrayList<>();
    private final List<float[]> bounds = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>();

    ObjectBuffer objectBuffer;

    private float scale;
    private float[] pos;
    private float[] rotation = {0, 0, 0};

    boolean hasTexture;
    int textureIndex;

    public static final int BOUND_SIZE = 6;
    public static final int BVH_BOUND_MIN_X = 0;
    public static final int BVH_BOUND_MIN_Y = 1;
    public static final int BVH_BOUND_MIN_Z = 2;
    public static final int BVH_BOUND_MAX_X = 3;
    public static final int BVH_BOUND_MAX_Y = 4;
    public static final int BVH_BOUND_MAX_Z = 5;

    public static final int POS_SIZE = 3;
    public static final int POS_X = 0;
    public static final int POS_Y = 1;
    public static final int POS_Z = 2;

    public static final int ROTATION_SIZE = 3;
    public static final int ROTATION_X = 0;
    public static final int ROTATION_Y = 1;
    public static final int ROTATION_Z = 2;

    public static final int TEXTURE_COORD_SIZE = 2;
    public static final int TEXTURE_COORD_U = 0;
    public static final int TEXTURE_COORD_V = 1;

    public static final int VERTEX_SIZE = 3;
    public static final int VERTEX_X = 0;
    public static final int VERTEX_Y = 1;
    public static final int VERTEX_Z = 2;

    public static final int FACE_SIZE = 6;
    public static final int VERTEX_1_INDEX = 0;
    public static final int VERTEX_2_INDEX = 1;
    public static final int VERTEX_3_INDEX = 2;
    public static final int TEXTURE_COORD_1_INDEX = 3;
    public static final int TEXTURE_COORD_2_INDEX = 4;
    public static final int TEXTURE_COORD_3_INDEX = 5;

    public static final int CACHE_SIZE = 13;
    public static final int EDGE_1_X = 0;
    public static final int EDGE_1_Y = 1;
    public static final int EDGE_1_Z = 2;
    public static final int EDGE_2_X = 3;
    public static final int EDGE_2_Y = 4;
    public static final int EDGE_2_Z = 5;
    public static final int NORMAL_X = 6;
    public static final int NORMAL_Y = 7;
    public static final int NORMAL_Z = 8;
    public static final int D00 = 9;
    public static final int D01 = 10;
    public static final int D11 = 11;
    public static final int DENOMINATOR = 12;

    public static final int SIZES_SIZE = 7;
    public static final int VERTEX_OFFSET = 0;
    public static final int TEXT_COORD_OFFSET = 1;
    public static final int LEAFS_OFFSET = 2;
    public static final int FACE_COUNT_OFFSET = 3;
    public static final int TEXTURE_OFFSET = 4;
    public static final int TEXTURE_WIDTH = 5;
    public static final int TEXTURE_HEIGHT = 6;

    public ObjFile(float[] pos, float scale, int color, String filePath, boolean hasTexture, int textureIndex) throws IOException {
        super(color);
        this.pos = pos;
        this.scale = scale;
        this.hasTexture = hasTexture;

        if (hasTexture) {
            this.textureIndex = textureIndex;
        }

        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY, minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith("v ")) {
                String[] parts = line.split("\\s+");
                float x = Float.parseFloat(parts[1]) * scale;
                float y = Float.parseFloat(parts[2]) * scale;
                float z = Float.parseFloat(parts[3]) * scale;
                vertices.add(new float[]{x, y, z});

                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                minZ = Math.min(minZ, z);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
                maxZ = Math.max(maxZ, z);
            } else if (line.startsWith("vt ") && hasTexture) {
                String[] parts = line.split("\\s+");
                float u = Float.parseFloat(parts[1]);
                float v = Float.parseFloat(parts[2]);
                texCoords.add(new float[]{u, v});
            } else if (line.startsWith("f ")) {
                String[] parts = line.split("\\s+");
                boolean quad = parts.length == 5;
                int[] face = new int[(quad ? 4 : 3) * 2];

                for (int i = 1; i < parts.length; i++) {
                    String[] indices = parts[i].split("/");
                    face[i - 1] = Integer.parseInt(indices[0]) - 1;
                    if (hasTexture && indices.length > 1 && !indices[1].isEmpty()) {
                        face[(quad ? 4 : 3) + i - 1] = Integer.parseInt(indices[1]) - 1;
                    }
                }

                faces.add(face);
            }
        }
        bounds.add(new float[]{minX, minY, minZ});
        bounds.add(new float[]{maxX, maxY, maxZ});

        serialize(faces);
        objectBuffer = new ObjectBuffer();
        faces.clear();
    }

    public void serialize(List<int[]> faces) {
        List<Face> faceList = new ArrayList<>();
        for (int[] f : faces) {
            if (f.length == 6) {
                faceList.add(new Face(f[0], f[1], f[2], f[3], f[4], f[5]));
            } else if (f.length == 8) {
                faceList.add(new Face(f[0], f[1], f[2], f[4], f[5], f[6]));
                faceList.add(new Face(f[0], f[2], f[3], f[4], f[6], f[7]));
            }
        }
        for (Face face : faceList) {
            leafIndices.add(face.v0);
            leafIndices.add(face.v1);
            leafIndices.add(face.v2);
            leafIndices.add(face.t0);
            leafIndices.add(face.t1);
            leafIndices.add(face.t2);
            cache.add(new float[]{
                    face.edge0[0], face.edge0[1], face.edge0[2],
                    face.edge1[0], face.edge1[1], face.edge1[2],
                    face.normal[0], face.normal[1], face.normal[2],
                    face.d00, face.d01, face.d11, face.denominator}
            );
        }
    }

    @Override
    public float[] getPos() {
        return pos;
    }

    public float[] getRotation() {
        return rotation;
    }

    public void addToPos(float[] pos) {
        this.pos = Vector3.add(this.pos, pos);
    }

    public void addToRotation(float[] rotor) {
        float radX = (float) Math.toRadians(rotor[0]);
        float radY = (float) Math.toRadians(rotor[1]);
        float radZ = (float) Math.toRadians(rotor[2]);
        float twoPI = (float) (2 * Math.PI);

        rotation[0] = (rotation[0] + radX) % twoPI;
        rotation[1] = (rotation[1] + radY) % twoPI;
        rotation[2] = (rotation[2] + radZ) % twoPI;
    }


    @Override
    public Intersection getIntersection(Ray ray) {
        return noIntersection();
    }

    @Override
    public boolean getCollision(float[] pos, float radius) {
        return false;
    }

    @Override
    public float[] getNormal(float[] point, float r) {
        return null;
    }

    class Face {
        int v0;
        int v1;
        int v2;
        int t0;
        int t1;
        int t2;
        float[] edge0;
        float[] edge1;
        float[] edge2;
        float[] normal;
        float d00;
        float d01;
        float d11;
        float denominator;

        public Face(int v0, int v1, int v2, int t0, int t1, int t2) {
            this.v0 = v0;
            this.v1 = v1;
            this.v2 = v2;
            this.t0 = t0;
            this.t1 = t1;
            this.t2 = t2;
            this.edge0 = Vector3.subtract(vertices.get(v1), vertices.get(v0));
            this.edge1 = Vector3.subtract(vertices.get(v2), vertices.get(v0));
            this.edge2 = Vector3.subtract(vertices.get(v2), vertices.get(v1));
            this.normal = Vector3.normalize(Vector3.cross(edge0, edge1));
            this.d00 = Vector3.dot(edge0, edge0);
            this.d01 = Vector3.dot(edge0, edge1);
            this.d11 = Vector3.dot(edge1, edge1);
            this.denominator = d00 * d11 - d01 * d01;
        }
    }

    class ObjectBuffer {
        float[] verticesBuff;
        float[] texCoordsBuff;
        int[] leafInsidesBuff;
        float[] boundsBuff;
        float[] cacheBuff;
        int[] sizes = new int[SIZES_SIZE];

        private ObjectBuffer() {
            initCacheBuff();
            initBoundsBuff();
            initLeafIndices();
            initVerticesBuff();
            initTextCoordsBuff();
            initSizes();
        }

        private void initSizes() {
            sizes[VERTEX_OFFSET] = vertices.size();
            sizes[TEXT_COORD_OFFSET] = texCoords.size();
            sizes[LEAFS_OFFSET] = leafIndices.size();
            sizes[FACE_COUNT_OFFSET] = leafIndices.size() / 6;
            sizes[TEXTURE_WIDTH] = 0;
            sizes[TEXTURE_HEIGHT] = 0;
            sizes[TEXTURE_OFFSET] = 0;
        }

        private void initCacheBuff() {
            cacheBuff = new float[cache.size() * CACHE_SIZE];
            for (int i = 0; i < cache.size(); i++) {
                int ii = i * CACHE_SIZE;
                System.arraycopy(cache.get(i), 0, cacheBuff, ii, CACHE_SIZE);
            }
        }

        private void initVerticesBuff() {
            verticesBuff = new float[vertices.size() * VERTEX_SIZE];
            for (int i = 0; i < vertices.size(); i++) {
                int ii = i * VERTEX_SIZE;
                System.arraycopy(vertices.get(i), 0, verticesBuff, ii, VERTEX_SIZE);
            }
        }

        private void initTextCoordsBuff() {
            texCoordsBuff = new float[texCoords.size() * TEXTURE_COORD_SIZE];
            for (int i = 0; i < texCoords.size(); i++) {
                int ii = i * TEXTURE_COORD_SIZE;
                System.arraycopy(texCoords.get(i), 0, texCoordsBuff, ii, TEXTURE_COORD_SIZE);
            }
        }

        private void initLeafIndices() {
            leafInsidesBuff = new int[leafIndices.size()];
            for (int i = 0; i < leafIndices.size(); i++) {
                leafInsidesBuff[i] = leafIndices.get(i);
            }
        }

        private void initBoundsBuff() {
            boundsBuff = new float[bounds.size() * 3];
            for (int i = 0; i < bounds.size(); i++) {
                int ii = i * 3;
                System.arraycopy(bounds.get(i), 0, boundsBuff, ii, 3);
            }
        }
    }
}

