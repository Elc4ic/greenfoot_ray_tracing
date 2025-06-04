import java.io.IOException;

public class Laser extends Flash {

    public Laser(float[] pos, float[] rot, float scale, int damage) throws IOException {
        super(pos, rot, scale, damage, 3, "models\\laser.obj", TextureCollection.getInstance().getIndex("enemy"));
    }

    ///чет углы неправильно считает
    @Override
    public boolean haveCollision(float[] pos, float r) {
        float[] hypotenuse = Vector3.minus(pos, getPos());
        float hypotenuseLength = Vector3.length(hypotenuse);
        double angle = (float) Math.acos(Vector3.dot(getDirection(), hypotenuse) / Vector3.length(hypotenuse));
        float R = (float) (hypotenuseLength * Math.sin(angle));
        System.out.println(Math.toDegrees(angle));
        return R < r;
    }
}
