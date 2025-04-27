import greenfoot.Color;

public class Cube extends WorldObject {
    private float[] pos;
    private float edgeX;
    private float edgeY;
    private float edgeZ;


    public Cube(float[] pos, float edge, int color) {
        super(color);
        this.pos = pos;
        this.edgeX = edge;
        this.edgeY = edge;
        this.edgeZ = edge;
    }

    public Cube(float[] pos, float x, float y, float z, int color) {
        super(color);
        this.pos = pos;
        this.edgeX = x;
        this.edgeY = y;
        this.edgeZ = z;
    }

    public Intersection getIntersection(Ray ray) {
        float halfSizeX = edgeX / 2;
        float halfSizeY = edgeY / 2;
        float halfSizeZ = edgeZ / 2;
        float minX = pos[0] - halfSizeX;
        float maxX = pos[0] + halfSizeX;
        float minY = pos[1] - halfSizeY;
        float maxY = pos[1] + halfSizeY;
        float minZ = pos[2] - halfSizeZ;
        float maxZ = pos[2] + halfSizeZ;

        float[] rayOrigin = ray.getOrigin();
        float[] rayDirection = ray.getDirection();

        float tMinX = (minX - rayOrigin[0]) / rayDirection[0];
        float tMaxX = (maxX - rayOrigin[0]) / rayDirection[0];
        if (tMinX > tMaxX) {
            float temp = tMinX;
            tMinX = tMaxX;
            tMaxX = temp;
        }

        float tMinY = (minY - rayOrigin[1]) / rayDirection[1];
        float tMaxY = (maxY - rayOrigin[1]) / rayDirection[1];
        if (tMinY > tMaxY) {
            float temp = tMinY;
            tMinY = tMaxY;
            tMaxY = temp;
        }

        float tMinZ = (minZ - rayOrigin[2]) / rayDirection[2];
        float tMaxZ = (maxZ - rayOrigin[2]) / rayDirection[2];
        if (tMinZ > tMaxZ) {
            float temp = tMinZ;
            tMinZ = tMaxZ;
            tMaxZ = temp;
        }

        float tEnter = Math.max(Math.max(tMinX, tMinY), tMinZ);
        float tExit = Math.min(Math.min(tMaxX, tMaxY), tMaxZ);

        if (tEnter > tExit || tExit < 0) {
            return new Intersection(
                    null,
                    -1,
                    null,
                    getColor());
        }
        float t = (tEnter > 0) ? tEnter : tExit;
        float[] intersectionPoint = Vector3.add(rayOrigin, Vector3.scale(rayDirection, t));
        float[] normal = getNormal(intersectionPoint, 0.00001f);
        return new Intersection(
                intersectionPoint,
                t,
                normal,
                getColor());
    }

    @Override
    public boolean getCollision(float[] pos, float r) {
        return Math.abs(pos[0] - this.pos[0]) < r + edgeX / 2 &&
                Math.abs(pos[1] - this.pos[1]) < r + edgeY / 2 &&
                Math.abs(pos[2] - this.pos[2]) < r + edgeZ / 2;
    }

    @Override
    public float[] getNormal(float[] point, float r) {
        float halfSizeX = edgeX / 2;
        float halfSizeY = edgeY / 2;
        float halfSizeZ = edgeZ / 2;


        float[] n = new float[]{0, 0, 0};
        if (Math.abs(point[0] - (pos[0] - halfSizeX)) < r) {
            n[0] = -1;
        } else if (Math.abs(point[0] - (pos[0] + halfSizeX)) < r) {
            n[0] = 1;
        }
        if (Math.abs(point[1] - (pos[1] - halfSizeY)) < r) {
            n[1] = -1;
        } else if (Math.abs(point[1] - (pos[1] + halfSizeY)) < r) {
            n[1] = 1;
        }
        if (Math.abs(point[2] - (pos[2] - halfSizeZ)) < r) {
            n[2] = -1;
        } else if (Math.abs(point[2] - (pos[2] + halfSizeZ)) < r) {
            n[2] = 1;
        }
        return n;
    }

    @Override
    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public float getEdgeX() {
        return edgeX;
    }

    public float getEdgeY() {
        return edgeY;
    }

    public float getEdgeZ() {
        return edgeZ;
    }

}
