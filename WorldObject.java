public abstract class WorldObject {
    private float[] rotation = {0, 0, 0};
    private float[] pos = {0, 0, 0};

    public abstract boolean getCollision(float[] pos, float radius);

    public abstract float[] getNormal(float[] point, float r);

    public float[] getRotation() {
        return rotation;
    }

    public float[] getPos() {
        return pos;
    }

    public void setRotation(float[] rot) {
        rotation = rot;
    }

    public float[] getDirection() {
        float[] direction = {0, 0, 1};
//        Vector3.rotateZr(direction, rotation[2]);
        Vector3.rotateYr(direction, rotation[1]);
//        Vector3.rotateXr(direction, rotation[0]);
        return Vector3.normalize(direction);
    }

    public void setPos(float[] pos) {
        rotation = pos;
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
        this.pos = Vector3.add(this.pos, pos);
    }
}
