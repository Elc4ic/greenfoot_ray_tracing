
public class Ray {
    private final float[] origin;
    private final float[]  direction;

    public static final int RAY_SIZE = 6;
    public static final int ORIGIN_X = 0;
    public static final int ORIGIN_Y = 1;
    public static final int ORIGIN_Z = 2;
    public static final int DIRECTION_X = 3;
    public static final int DIRECTION_Y = 4;
    public static final int DIRECTION_Z = 5;


    public Ray(float[] origin, float[]  direction) {
        this.origin = origin;
        this.direction = direction;
    }
    
    public float[]  getOrigin() {
        return origin;
    }
    
    public float[]  getDirection() {
        return direction;
    }
}
