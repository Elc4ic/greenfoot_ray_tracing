public class Intersection {
    private float[] point;
    private float distance;
    private float[] normal;
    int color = 100;

    public Intersection(float[] point, float distance, float[] normal, int color) {
        this.point = point;
        this.distance = distance;
        this.normal = normal;
        this.color = color;
    }

    public Intersection(float[] point, float distance, float[] normal) {
        this.point = point;
        this.distance = distance;
        this.normal = normal;
    }

    public float[] getPoint() {
        return point;
    }

    public float getDistance() {
        return distance;
    }

    public float[] getNormal() {
        return normal;
    }

}
