import java.util.Random;

public class Hero extends Creature {
    int renderDistObjects = 9;
    int renderDistLight = 7;
    float viewportHeight;
    float viewportWidth;
    int shakeX = 0;
    int shakeY = 0;
    int shakeMax = 30;
    Gun[] arsenal;
    int gunInUse;
    int gunInHand;

    public Hero(float[] pos, float[] normal, float fov) {
        super(pos, 1, 2, 1, normal, 255, 0.4f);
        this.arsenal = new Gun[]{
                new Pistol( this, Const.WIDTH - 80, Const.HEIGHT - 170, Const.HEIGHT_SCALE),
                new Shotgun( this, Const.WIDTH / 2 - 10, Const.HEIGHT - 110, Const.HEIGHT_SCALE * 2),
                new Smg( this, Const.WIDTH / 2 - 40, Const.HEIGHT - 120, Const.HEIGHT_SCALE * 2),
                new Bfg( this, Const.WIDTH / 2, Const.HEIGHT - 90, Const.HEIGHT_SCALE * 2)
        };
        this.viewportHeight = 2f * (float) Math.tan(Math.toRadians(fov / 2.0));
        this.viewportWidth = viewportHeight * Const.WIDTH / Const.HEIGHT;
        gunInUse = 0;
        arsenal[gunInUse].show(true);
        gunInHand = 0;
    }

    void updateHero(WorldBase worldBase) {
        shake((int) (speedXZ * 6) + 4);
        updateGun();
        checkCollision(worldBase, true);
        updatePos();
    }

    float[] getOffset(float u, float v) {
        float[] right = Vector3.normalize(Vector3.cross(getNormal(), new float[]{0, 1, 0}));
        float[] up = Vector3.normalize(Vector3.cross(right, getNormal()));
        return Vector3.add(Vector3.scale(right, u * viewportWidth), Vector3.scale(up, v * viewportHeight));
    }

    void shake(int a) {
        Random r = new Random();
        float x = (r.nextFloat() - 0.5f) * Math.max(Math.abs(shakeX), a);
        float y = (r.nextFloat() - 0.5f) * Math.max(Math.abs(shakeY), a);
        shakeX += (int) x;
        shakeY += (int) y;
        shakeX = Math.min(Math.abs(shakeX), shakeMax);
        shakeY = Math.min(Math.abs(shakeY), shakeMax);
    }

    Gun getGun() {
        return arsenal[gunInUse];
    }

    void winCondition(WorldObject cube) {
        if (cube != null && cube.getCollision(getPos(), hitBoxRadius)) {
            state = -1;
        }
    }

    void updateGun() {
        if (gunInUse != gunInHand) {
            arsenal[gunInHand].show(false);
            arsenal[gunInUse].show(true);
            gunInHand = gunInUse;
        }
        arsenal[gunInHand].act();
    }

    boolean needRenderObject(float[] pos) {
        return Vector3.length(Vector3.subtract(getPos(), pos)) < renderDistObjects;
    }

    boolean needRenderLight(float[] pos) {
        return Vector3.length(Vector3.subtract(getPos(), pos)) < renderDistLight;
    }

    void switchGun(int i) {
        gunInUse = i % arsenal.length;
    }

}
