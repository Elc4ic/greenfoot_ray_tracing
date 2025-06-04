import java.io.IOException;

public class OpticFiber extends Weapon {
    private float damage = 25f;
    private int projectileCount = 6;
    private long fireInterval = 1 * Const.SECOND;

    public OpticFiber(Hero hero) {
        super(hero, "images\\opticFiber.png", "models\\laser.obj");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        WorldBase world = WorldBase.getInstance();
        for (int i = 0; i < projectileCount; i++) {
            try {
                float[] posL = Vector3.add(getHero().getPos(), new float[]{0, 0.1f, 0});
                Laser laser = new Laser(posL, new float[]{0, getR().nextFloat() * 2 * Const.PI, 0}, 1f, (int) damage);
                world.addObject(laser);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {

    }
}
