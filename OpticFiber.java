import java.io.IOException;

public class OpticFiber extends Weapon {
    private float damage = 20f;
    private int projectileCount = 1;
    private long fireInterval = 2 * Const.SECOND;

    public OpticFiber(Hero hero) {
        super(hero, "images\\opticFiber.png", "models\\laser.obj", "OpticFiber");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < getAS(fireInterval)) return;

        WorldBase world = WorldBase.getInstance();

        int ModProjectileCount = getProj(projectileCount);
        for (int i = 0; i < ModProjectileCount; i++) {
            try {
                float[] posL = Vector3.add(getHero().getPos(), new float[]{0, 0.1f, 0});
                Laser laser = new Laser(posL, new float[]{0, getR().nextFloat() * 2 * Const.PI, 0}, 1f, (int) getDMG(damage));
                world.addObject(laser);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        lvlUp();
        projectileCount++;
        damage *= 1.2f;
        fireInterval -= 100 * Const.MILLI;
    }
}
