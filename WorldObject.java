public abstract class WorldObject {
    private float[] rotation;
    private float[] position;
    private final float collisionR = 0.5f;

    WorldObject(float[] position,float[] rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public boolean getCollision(float[] pos, float r) {
        return getDistance(pos) < collisionR + r;
    }

    public float[] getNormal(float[] pos) {
        return Vector3.normalize(Vector3.subtract(pos, position));
    }

    public float getDistance(float[] pos) {
        return Vector3.length(Vector3.subtract(pos, position));
    }

    public float[] getRotation() {
        return rotation;
    }

    public float getCollisionR() {
        return collisionR;
    }

    public float[] getPos() {
        return position;
    }

    public void setRotation(float[] rot) {
        rotation = rot;
    }

    public float[] getDirection() {
        float[] direction = {0, 0, 1};
        Vector3.rotateZr(direction, rotation[2]);
        Vector3.rotateYr(direction, rotation[1]);
        Vector3.rotateXr(direction, rotation[0]);
        return direction;
    }

    public void setPos(float[] position) {
        this.position = position;
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

    public void addToPos(float[] pos) {
        this.position = Vector3.add(this.position, pos);
    }
}
