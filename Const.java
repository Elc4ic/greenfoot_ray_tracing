import greenfoot.GreenfootImage;

public final class Const {
    private Const() {
    }

    public static final float EPSILON = 0.0001F;
    public static final float MAXIMUM_DISTANCE = Float.MAX_VALUE;
    public static final float PI = (float) (Math.PI);
    public static final GreenfootImage NOTHING = new GreenfootImage(1, 1);
    public static final long SECOND = 1000000000L;
    public static final long MILLI = 1000000L;

    public static final int WIDTH = 1152;

    public static final int HEIGHT = 720;
//    public static final int WIDTH = 400;
//    public static final int HEIGHT = 300;
    public static final int PICXELS = Const.WIDTH * Const.HEIGHT;
    public static final int TICK_RATE = 20;
    public static final long WIN_TIME = 10 * 60 * 1000000000L;

    public static final int ICON_SIZE = 64;

}
