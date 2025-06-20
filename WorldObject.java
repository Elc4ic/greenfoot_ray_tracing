public abstract class WorldObject {
    private float[] rotation;
    private float[] position;
    private float collisionR = 0.5f;

    private float gravity = 0.3f;
    private float speedY = 0;
    private boolean onGround = true;

    WorldObject(float[] position, float[] rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public abstract boolean update();

    public void applyGravity() {
        if (speedY == 0) return;
        float[] YOffset = Vector3.scale(new float[]{0, 1, 0}, speedY);

        addToPos(YOffset);

        if (getPos()[1] > 0) {
            speedY -= gravity;
        } else {
            float y = getPos()[1];
            addToPos(new float[]{0, -y, 0});
            onGround = true;
            speedY = 0;
        }
    }

    public boolean haveCollision(float[] pos, float r) {
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

    public void setCollisionR(float collisionR) {
        this.collisionR = collisionR;
    }

    public float[] getPos() {
        return position;
    }

    public void setRotation(float[] rot) {
        rotation = rot;
    }

    public float[] getDirection() {
        float[] direction = {0, 0, 1};
//        Vector3.rotateZr(direction, rotation[2]);
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

    public float getGravity() {
        return gravity;
    }

    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public void setSpeedY(float speedY) {
        this.speedY = speedY;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }
}
