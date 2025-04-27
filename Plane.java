public class Plane extends WorldObject {
    private float[] pos;
    private float[] normal;

    public Plane(float[] pos, float[] normal, int color) {
        super(color);
        this.pos = pos;
        this.normal = normal;
    }

    public Intersection getIntersection(Ray ray) {
        float d = Vector3.dot(ray.getDirection(), normal);
        if (d == 0) {
            return new Intersection(
                    new float[]{0, 0, 0},
                    -1,
                    new float[]{0, 0, 0},
                    getColor());
        } else {
            d = Vector3.dot(Vector3.subtract(pos, ray.getOrigin()), normal) / d;
            return new Intersection(
                    Vector3.add(ray.getOrigin(), Vector3.scale(ray.getDirection(), d)),
                    d,
                    normal,
                    getColor());
        }
    }

    @Override
    public boolean getCollision(float[] pos, float radius) {
        return false;
    }

    @Override
    public float[] getNormal(float[] point, float r) {
        return normal;
    }

    public float[] getPos() {
        return pos;
    }

    public float[] getNormal() {
        return normal;
    }
}
