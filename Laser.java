import java.io.IOException;

public class Laser extends Flash {

    public Laser(float[] pos, float[] rot, float scale, int damage) throws IOException {
        super(pos, rot, scale, damage, 3, "models\\laser.obj", TextureCollection.getInstance().getIndex("enemy"));
    }

    @Override
    public boolean haveCollision(float[] pos, float r) {
        float[] hypotenuse = Vector3.minus(getPos(), pos);
        float[] direction = getDirection();
        float hypotenuseLength = Vector3.length(hypotenuse);
        float angle = (float) Math.acos(Vector3.dot(direction, hypotenuse) / (Vector3.length(direction) * hypotenuseLength));
        float R = (float) (hypotenuseLength * Math.sin(angle));
        return R < r && angle < Math.PI / 2;
    }
}
