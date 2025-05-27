
public final class Const {
    private Const() {}

    public static final float AMBIENT = 0.1f;
    public static final float TICKRATE = 4;
    public static final float EPSILON = 0.0001F;
    public static final float MAXIMUM_DISTANCE = Float.MAX_VALUE;
    public static final float PI = (float) (Math.PI);

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final int PICXELS = Const.WIDTH * Const.HEIGHT;
    public static final int WIDTH_SCALE = 1;
    public static final int HEIGHT_SCALE = 1;
    public static final int SCALED_WIDTH = Const.WIDTH * Const.WIDTH_SCALE;
    public static final int SCALED_HEIGHT = Const.HEIGHT * Const.HEIGHT_SCALE;
    private final int WIDTH_CENTRE = WIDTH / 2;
    private final int HEIGHT_CENTRE = HEIGHT / 2;

}
