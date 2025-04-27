import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjFile extends WorldObject {

    private final List<float[]> vertices = new ArrayList<>();
    private final List<Integer> leafIndices = new ArrayList<>();
    private final List<float[]> texCoords = new ArrayList<>();
    private final List<float[]> cache = new ArrayList<>();
    private final List<float[]> bvhBounds = new ArrayList<>();
    private final List<int[]> bvhNodes = new ArrayList<>();
    List<int[]> faces = new ArrayList<>();

    public static final int BVH_NODE_SIZE = 3;
    public static final int BVH_NODE_IS_LEAF = 0;
    public static final int BVH_NODE_LEFT_OR_FIRST = 1;
    public static final int BVH_NODE_RIGHT_FACE_COUNT = 2;

    public static final int BVH_BOUND_SIZE = 6;
    public static final int BVH_BOUND_MIN_X = 0;
    public static final int BVH_BOUND_MIN_Y = 1;
    public static final int BVH_BOUND_MIN_Z = 2;
    public static final int BVH_BOUND_MAX_X = 3;
    public static final int BVH_BOUND_MAX_Y = 4;
    public static final int BVH_BOUND_MAX_Z = 5;

    public static final int TEXTURE_COORD_SIZE = 2;
    public static final int TEXTURE_COORD_U = 0;
    public static final int TEXTURE_COORD_V = 1;

    public static final int VERTEX_SIZE = 3;
    public static final int VERTEX_X = 0;
    public static final int VERTEX_Y = 1;
    public static final int VERTEX_Z = 2;

    public static final int FACE_SIZE = 7;
    public static final int VERTEX_1_INDEX = 0;
    public static final int VERTEX_2_INDEX = 1;
    public static final int VERTEX_3_INDEX = 2;
    public static final int TEXTURE_COORD_1_INDEX = 3;
    public static final int TEXTURE_COORD_2_INDEX = 4;
    public static final int TEXTURE_COORD_3_INDEX = 5;

    public static final int CACHE_SIZE = 13;
    public static final int EDGE_1_X_INDEX = 0;
    public static final int EDGE_1_Y_INDEX = 0;
    public static final int EDGE_1_Z_INDEX = 0;
    public static final int EDGE_2_X_INDEX = 0;
    public static final int EDGE_2_Y_INDEX = 0;
    public static final int EDGE_2_Z_INDEX = 0;
    public static final int NORMAL_X_INDEX = 0;
    public static final int NORMAL_Y_INDEX = 0;
    public static final int NORMAL_Z_INDEX = 0;
    public static final int D00_INDEX = 0;
    public static final int D01_INDEX = 0;
    public static final int D11_INDEX = 0;
    public static final int DENOMINATOR = 0;

    public static final int SIZES_SIZE = 7;
    public static final int VERTEX_OFFSET = 0;
    public static final int TEXT_COORD_OFFSET = 1;
    public static final int LEAFS_OFFSET = 2;
    public static final int NODES_OFFSET = 3;
    public static final int TEXTURE_OFFSET = 4;
    public static final int TEXTURE_WIDTH = 5;
    public static final int TEXTURE_HEIGHT = 6;

    int[] textureBuff;
    int textureWidth, textureHeight;

    private Box box;
    private float scale;
    private float[] pos;
    private boolean hasTexture;
    private BufferedImage texture;

    public ObjFile(float[] pos, float scale, int color, String filePath, int depth, boolean hasTexture, String texturePath) throws IOException {
        super(color);
        this.pos = pos;
        this.scale = scale;
        this.hasTexture = hasTexture;

        if (hasTexture) {
            this.texture = ImageIO.read(new File(texturePath));
            fillTextureBuff(texture);
        }

        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY, minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith("v ")) {
                String[] parts = line.split("\\s+");
                float x = Float.parseFloat(parts[1]) * scale + pos[0];
                float y = Float.parseFloat(parts[2]) * scale + pos[1];
                float z = Float.parseFloat(parts[3]) * scale + pos[2];
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

        List<Face> faceList = new ArrayList<>();
        for (int[] f : faces) {
            if (f.length == 6) {
                faceList.add(new Face(f[0], f[1], f[2], f[3], f[4], f[5]));
            } else if (f.length == 8) {
                faceList.add(new Face(f[0], f[1], f[2], f[4], f[5], f[6]));
                faceList.add(new Face(f[0], f[2], f[3], f[4], f[6], f[7]));
            }
        }
        box = new Box(
                new float[]{minX, minY, minZ},
                new float[]{maxX, maxY, maxZ}
        );
        box.boxFaces.addAll(faceList);
        box.split(depth, box.boxFaces);
        serialize(box);

        faces.clear();
        faceList.clear();
    }

    public int serialize(Box node) {

        bvhBounds.add(node.min);
        bvhBounds.add(node.max);
        bvhNodes.add(new int[]{0, 0, 0});
        int currentIndex = bvhNodes.size() - 1;

        if (node.leftChild == null && node.rightChild == null) {
            int startFaceIndex = leafIndices.size() / 6;
            for (Face face : node.boxFaces) {
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
            int faceCount = node.boxFaces.size();
            int[] bhvLeaf = new int[]{1, startFaceIndex, faceCount};
            bvhNodes.set(currentIndex, bhvLeaf);
        } else {
            int leftIndex = (node.leftChild == null) ? -1 : serialize(node.leftChild);
            int rightIndex = (node.rightChild == null) ? -1 : serialize(node.rightChild);
            int[] bhvNode = new int[]{0, leftIndex, rightIndex};
            bvhNodes.set(currentIndex, bhvNode);
        }
        return currentIndex;
    }

    public int getVertexSize() {
        return vertices.size();
    }

    public int getTextureSize() {
        return textureBuff.length;
    }

    public int getTextureCoordSize() {
        return texCoords.size();
    }

    public int getLeafIndicesSize() {
        return leafIndices.size();
    }

    public int getBvhNodesSize() {
        return bvhNodes.size();
    }

    private void fillTextureBuff(BufferedImage texture) {
        this.textureWidth = texture.getWidth();
        this.textureHeight = texture.getHeight();
        this.textureBuff = new int[textureWidth * textureHeight];
        for (int i = 0; i < textureHeight; i++) {
            for (int j = 0; j < textureWidth; j++) {
                textureBuff[i * textureWidth + j] = texture.getRGB(j, i);
            }
        }
    }

    public float[] getVerticesBuff() {
        float[] verticesBuff = new float[vertices.size() * 3];
        for (int i = 0; i < vertices.size(); i++) {
            int ii = i * 3;
            verticesBuff[ii] = vertices.get(i)[0];
            verticesBuff[ii + 1] = vertices.get(i)[1];
            verticesBuff[ii + 2] = vertices.get(i)[2];
        }
        return verticesBuff;
    }

    public float[] getTextCoordsBuff() {
        float[] texCoordsBuff = new float[texCoords.size() * 2];
        for (int i = 0; i < texCoords.size(); i++) {
            int ii = i * 2;
            texCoordsBuff[ii] = texCoords.get(i)[0];
            texCoordsBuff[ii + 1] = texCoords.get(i)[1];
        }
        return texCoordsBuff;
    }

    public int[] getLeafIndices() {
        int[] leafIndicesBuff = new int[leafIndices.size()];
        for (int i = 0; i < leafIndices.size(); i++) {
            leafIndicesBuff[i] = leafIndices.get(i);
        }
        return leafIndicesBuff;
    }

    public float[] getBvhBoundsBuff() {
        float[] bvhBoundsBuff = new float[bvhBounds.size() * 3];
        for (int i = 0; i < bvhBounds.size(); i++) {
            int ii = i * 3;
            bvhBoundsBuff[ii] = bvhBounds.get(i)[0];
            bvhBoundsBuff[ii + 1] = bvhBounds.get(i)[1];
            bvhBoundsBuff[ii + 2] = bvhBounds.get(i)[2];
        }
        return bvhBoundsBuff;
    }

    public int[] getBvhNodesBuff() {
        int[] bvhNodesBuff = new int[bvhNodes.size() * ObjFile.BVH_NODE_SIZE];
        for (int i = 0; i < bvhNodes.size(); i++) {
            int ii = i * 3;
            bvhNodesBuff[ii] = bvhNodes.get(i)[0];
            bvhNodesBuff[ii + 1] = bvhNodes.get(i)[1];
            bvhNodesBuff[ii + 2] = bvhNodes.get(i)[2];
            //           System.out.print(bvhNodesBuff[ii] + " " + bvhNodesBuff[ii + 1] + " " + bvhNodesBuff[ii + 2] + " | ");
        }
//        System.out.print("\n");
        return bvhNodesBuff;
    }

    @Override
    public float[] getPos() {
        return pos;
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

        boolean inLeft = false, inRight = false;

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

        public void vSplit(int axis, float distance) {
            if (vertices.get(v0)[axis] <= distance) inLeft = true;
            else inRight = true;
            if (vertices.get(v1)[axis] <= distance) inLeft = true;
            else inRight = true;
            if (vertices.get(v2)[axis] <= distance) inLeft = true;
            else inRight = true;
        }

        public void reset() {
            inLeft = false;
            inRight = false;
        }
    }

    class Box {
        public float[] min;
        public float[] max;
        public Box leftChild;
        public Box rightChild;
        public List<Face> boxFaces;

        public Box(float[] min, float[] max) {
            this.min = min;
            this.max = max;
            this.leftChild = null;
            this.rightChild = null;
            this.boxFaces = new ArrayList<>();
        }

        public void split(int depth, List<Face> facesFromParent) {
            if (depth <= 0) return;

            float[] size = Vector3.subtract(max, min);
            int splitAxis = 0;
            if (size[1] > size[0] && size[1] > size[2]) splitAxis = 1;
            else if (size[2] > size[0] && size[2] > size[1]) splitAxis = 2;

            float splitPos = min[splitAxis] + size[splitAxis] * 0.5f;

            float[] leftMax = Vector3.scale(max, 1);
            leftMax[splitAxis] = splitPos;
            this.leftChild = new Box(min, leftMax);

            float[] rightMin = Vector3.scale(min, 1);
            rightMin[splitAxis] = splitPos;
            this.rightChild = new Box(rightMin, max);

            for (Face face : facesFromParent) {
                face.reset();
                face.vSplit(splitAxis, splitPos);
                if (face.inLeft) leftChild.boxFaces.add(face);
                if (face.inRight) rightChild.boxFaces.add(face);
            }
            if (!leftChild.boxFaces.isEmpty()) leftChild.split(depth - 1, leftChild.boxFaces);
            else leftChild = null;
            if (!rightChild.boxFaces.isEmpty()) rightChild.split(depth - 1, rightChild.boxFaces);
            else rightChild = null;
            facesFromParent.clear();
        }

        public boolean intersect(Ray ray) {
            float tMin = Float.NEGATIVE_INFINITY;
            float tMax = Float.POSITIVE_INFINITY;

            for (int i = 0; i < 3; i++) {
                if (Math.abs(ray.getDirection()[i]) < 0.000001f) {
                    if (ray.getOrigin()[i] < min[i] || ray.getOrigin()[i] > max[i]) {
                        return false;
                    }
                } else {
                    float ood = 1.0f / ray.getDirection()[i];
                    float t1 = (min[i] - ray.getOrigin()[i]) * ood;
                    float t2 = (max[i] - ray.getOrigin()[i]) * ood;

                    if (t1 > t2) {
                        float temp = t1;
                        t1 = t2;
                        t2 = temp;
                    }

                    tMin = Math.max(tMin, t1);
                    tMax = Math.min(tMax, t2);

                    if (tMin > tMax) return false;
                }
            }
            return tMax >= 0;
        }
    }
}

