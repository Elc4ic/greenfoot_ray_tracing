import greenfoot.Color;

public class Sphere extends WorldObject {
    private float[] center;
    private float radius;

    public Sphere(float[] center, float radius, int color) {
        super(color);
        this.center = center;
        this.radius = radius;
    }

    public Intersection getIntersection(Ray ray) {
        float[] v = Vector3.subtract(ray.getOrigin(), center);
        float a = Vector3.dot(ray.getDirection(), v);
        float b = Vector3.length(v);
        float q = a * a - b + radius * radius;
        if (q < 0) {
            return noIntersection();
        } else {
            q = (float) Math.sqrt(q);
            float d1 = -a - q;
            float d2 = -a + q;
            float d3;
            if (d1 > 0 && (d1 < d2 || d2 < 0)) {
                d3 = d1;
            } else if (d2 > 0 && (d2 < d1 || d1 < 0)) {
                d3 = d2;
            } else {
                return noIntersection();
            }
            float[] v2 = Vector3.add(ray.getOrigin(), Vector3.scale(ray.getDirection(), d3));
            return new Intersection(
                    v2,
                    d3,
                    getNormal(v2, 0),
                    getColor());
        }
    }

    @Override
    public boolean getCollision(float[] pos, float r) {
        float len = Vector3.length(Vector3.subtract(pos, center));
        return len < (radius + r) * (radius + r);
    }

    @Override
    public float[] getPos() {
        return center;
    }

    public void setCenter(float[] vector) {
        center = vector;
    }

    public float getRadius() {
        return radius;
    }

    public float[] getNormal(float[] v, float r) {
        return Vector3.normalize(Vector3.subtract(v,center));
    }
}
