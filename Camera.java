
public class Camera {
    private float[] pos = new float[]{-1, 1, 0};
    private float[] rotations = new float[]{0, (float) Math.toRadians(45), 0};
    float viewportHeight;
    float viewportWidth;
    float fov;

    public Camera(float fov) {
        this.fov = (float) (1.0 / Math.tan(Math.toRadians(fov * 0.5)));
        this.viewportHeight = 2f * (float) Math.tan(Math.toRadians(fov / 2.0));
        this.viewportWidth = viewportHeight * Const.WIDTH / Const.HEIGHT;
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
        float[] heroHorizontalOffset =  Vector3.add(hero.getPos(), Vector3.scale(dir, -3));
        float[] cameraRotation = Vector3.add(hero.getRotation(), new float[]{0, -34, 0});
        this.pos = Vector3.add(heroHorizontalOffset,new float[]{0, 2, 0});
        this.rotations = cameraRotation;
    }

}
