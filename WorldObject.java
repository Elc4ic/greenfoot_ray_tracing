public abstract class WorldObject {
    private int color;

    Intersection noIntersection() {
        return new Intersection(
                null,
                -1,
                null,
                0);
    }

    public WorldObject(int color) {
        this.color = color;
    }

    public abstract float[] getPos();

    public abstract Intersection getIntersection(Ray ray);

    public abstract boolean getCollision(float[] pos, float radius);

    public abstract float[] getNormal(float[] point, float r);

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
