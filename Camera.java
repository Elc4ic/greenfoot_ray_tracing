
public class Camera {
    private float[] pos = new float[]{0, 0, 0};
    private float[] rotations = new float[]{0, 0, 0};
    float fov;

    public Camera(float fov) {
        this.fov = (float) (1.0 / Math.tan(Math.toRadians(fov * 0.5)));
    }

    public float[] getRotations() {
        return rotations;
    }

    public float[] getPos() {
        return pos;
    }

    public void setPos(float[] pos) {
        this.pos = pos;
    }

    public void setRotations(float[] rotations) {
        this.rotations = rotations;
    }

    public void bindToHero(Hero hero) {
        float[] dir = hero.getDirection();
        float[] heroHorizontalOffset = Vector3.add(hero.getPos(), Vector3.scale(dir, -5));
        float[] cameraRotation = hero.getRotation();
        this.pos = Vector3.add(heroHorizontalOffset, new float[]{0, 5, 0});
        this.rotations = Vector3.add(cameraRotation,new float[]{(float) Math.toRadians(-45),0,0});
    }

    public float sinX() {
        return (float) Math.sin(rotations[0]);
    }

    public float cosX() {
        return (float) Math.cos(rotations[0]);
    }

    public float sinY() {
        return (float) Math.sin(rotations[1]);
    }

    public float cosY() {
        return (float) Math.cos(rotations[1]);
    }

    public float sinZ() {
        return (float) Math.sin(rotations[2]);
    }

    public float cosZ() {
        return (float) Math.cos(rotations[2]);
    }

}
