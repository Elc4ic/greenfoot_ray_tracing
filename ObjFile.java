import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ObjFile extends WorldObject {
    private final List<Triangle> triangles = new ArrayList<>();
    private final List<float[]> vertices = new ArrayList<>();
    private final List<float[]> texCoords = new ArrayList<>();
    private List<int[]> faces = new ArrayList<>();

    private float scale;

    int textureIndex;

    public static final int POS_SIZE = 3;
    public static final int POS_X = 0;
    public static final int POS_Y = 1;
    public static final int POS_Z = 2;

    public static final int ROTATION_SIZE = 3;
    public static final int ROTATION_X = 0;
    public static final int ROTATION_Y = 1;
    public static final int ROTATION_Z = 2;

    public ObjFile(float[] pos, float scale, String filePath, int textureIndex) throws IOException {
        this.scale = scale;

        setPos(pos);

        this.textureIndex = textureIndex;

        List<String> lines = Files.readAllLines(Paths.get(filePath));
        for (String line : lines) {
            if (line.startsWith("v ")) {
                String[] parts = line.split("\\s+");
                float x = Float.parseFloat(parts[1]) * scale;
                float y = Float.parseFloat(parts[2]) * scale;
                float z = Float.parseFloat(parts[3]) * scale;
                vertices.add(new float[]{x, y, z});

            } else if (line.startsWith("vt ")) {
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
                    if (indices.length > 1 && !indices[1].isEmpty()) {
                        face[(quad ? 4 : 3) + i - 1] = Integer.parseInt(indices[1]) - 1;
                    }
                }

                faces.add(face);
            }
        }
        for (int[] f : faces) {
            if (f.length == 6) {
                float[] xyz0 = vertices.get(f[0]);
                float[] xyz1 = vertices.get(f[1]);
                float[] xyz2 = vertices.get(f[2]);
                float[] uv0 = texCoords.get(f[3]);
                float[] uv1 = texCoords.get(f[4]);
                float[] uv2 = texCoords.get(f[5]);
                float[] v0 = new float[]{xyz0[0], xyz0[1], xyz0[2], uv0[0], uv0[1]};
                float[] v1 = new float[]{xyz1[0], xyz1[1], xyz1[2], uv1[0], uv1[1]};
                float[] v2 = new float[]{xyz2[0], xyz2[1], xyz2[2], uv2[0], uv2[1]};
                triangles.add(new Triangle(v0, v1, v2, textureIndex));
            } else if (f.length == 8) {
                float[] xyz0 = vertices.get(f[0]);
                float[] xyz1 = vertices.get(f[1]);
                float[] xyz2 = vertices.get(f[2]);
                float[] xyz3 = vertices.get(f[3]);
                float[] uv0 = texCoords.get(f[4]);
                float[] uv1 = texCoords.get(f[5]);
                float[] uv2 = texCoords.get(f[6]);
                float[] uv3 = texCoords.get(f[7]);
                float[] v0 = new float[]{xyz0[0], xyz0[1], xyz0[2], uv0[0], uv0[1]};
                float[] v1 = new float[]{xyz1[0], xyz1[1], xyz1[2], uv1[0], uv1[1]};
                float[] v2 = new float[]{xyz2[0], xyz2[1], xyz2[2], uv2[0], uv2[1]};
                float[] v3 = new float[]{xyz3[0], xyz3[1], xyz3[2], uv3[0], uv3[1]};
                triangles.add(new Triangle(v0, v1, v2, textureIndex));
                triangles.add(new Triangle(v0, v2, v3, textureIndex));
            }
        }

        faces.clear();
    }

    public List<Triangle> getTriangles(int index) {
        for (Triangle t : triangles) {
            t.setObjIndex(index);
        }
        return triangles;
    }

    @Override
    public boolean getCollision(float[] pos, float radius) {
        return false;
    }

    @Override
    public float[] getNormal(float[] point, float r) {
        return null;
    }

}

