import java.io.IOException;

public class Sword extends Satellite {
    private long framesToLive = 4;
    private long framesAlive = 0;
    private float swordLen = 4f;

    public Sword(Hero hero, float[] pos, float[] rot, float scale, int damage) throws IOException {
        super(hero, pos, rot, scale, damage, "models\\key_sword.obj", TextureCollection.getInstance().getIndex("keyboard"));
    }

    @Override
    public boolean update() {
        if (framesAlive >= framesToLive) return true;
        framesAlive++;
        return super.update();
    }

    @Override
    public boolean haveCollision(float[] pos, float r) {
        float[] hypotenuse = Vector3.minus(getPos(),pos );
        float hypotenuseLength = Vector3.length(hypotenuse);
        if (swordLen > hypotenuseLength) return false;
        float[] direction = getDirection();
        float angle = (float) Math.acos(Vector3.dot(direction, hypotenuse) / (Vector3.length(direction) * hypotenuseLength));
        float R = (float) (hypotenuseLength * Math.sin(angle));
        return R < r && angle < Math.PI / 6;
    }
}
