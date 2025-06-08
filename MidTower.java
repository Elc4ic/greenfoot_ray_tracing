import java.io.IOException;
import java.util.Random;

/// Оружие Корпус Mid-Tower - спавнит снаряды, которые падают и взрываются
public class MidTower extends Weapon {
    private float damage = 30f;
    private float radius = 0.5f;
    private float xOffset = 12f;
    private float zOffset = 12f;
    private int projectileCount = 3;
    private long fireInterval = 3 * Const.SECOND;

    public MidTower(Hero hero) {
        super(hero, "images\\midtower.png", "models\\block.obj", "Mid-Tower");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < getAS(fireInterval)) return;

        int ModProjectileCount = getProj(projectileCount);
        for (int i = 0; i < ModProjectileCount; i++) {
            float[] bombPos = new float[]{
                    getHero().getPos()[0] + getR().nextFloat() * xOffset - xOffset / 2,
                    12,
                    getHero().getPos()[2] + getR().nextFloat() * zOffset - zOffset / 2
            };
            try {
                Bomb pc = new Bomb(
                        bombPos, 0.3f, (int) getDMG(damage), radius,
                        getProjectileModel(),
                        TextureCollection.getInstance().getIndex("portal")
                );
                WorldBase.getInstance().addObject(pc);
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
        radius *= 1.2f;
        damage *= 1.4f;
        fireInterval -= 200 * Const.MILLI;
    }
}
