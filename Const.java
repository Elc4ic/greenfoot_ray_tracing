import greenfoot.GreenfootImage;

public final class Const {
    private Const() {
    }

    public static final float EPSILON = 0.0001F;
    public static final float MAXIMUM_DISTANCE = Float.MAX_VALUE;
    public static final float PI = (float) (Math.PI);
    public static final GreenfootImage NOTHING = new GreenfootImage(1, 1);
    public static final long SECOND = 1000000000L;

    public static final int WIDTH = 600;
    public static final int HEIGHT = 400;
    public static final int PICXELS = Const.WIDTH * Const.HEIGHT;
    public static final int TICK_RATE = 20;
    public static final long WIN_TIME = 30 * 60 * 1000000000L;
    private final int WIDTH_CENTRE = WIDTH / 2;
    private final int HEIGHT_CENTRE = HEIGHT / 2;

}
