import java.io.IOException;
import java.util.Random;

/// Оружие Корпус Mid-Tower - спавнит снаряды, которые падают и взрываются
public class MidTower extends Weapon {
    Random r = new Random();
    private float damage = 30f;
    private float radius = 0.5f;
    private float xOffset = 4f;
    private float zOffset = 4f;
    private int projectileCount = 2;
    private long fireInterval = 4 * Const.SECOND;

    public MidTower(Hero hero) throws IOException {
        super(
                hero.getPos(), new float[]{0, 0, 0}, 1f,
                "models\\midTower.obj",
                "images\\midTower.png",
                TextureCollection.getInstance().getIndex("midTower"),
                hero
        );
    }


    @Override
    public void fire(WorldBase worldBase) {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        for (int i = 0; i < projectileCount; i++) {
            float bombPos[] = new float[]{
                    getPos()[0] + r.nextFloat() * xOffset / 2,
                    4,
                    getPos()[2] + r.nextFloat() * zOffset / 2
            };
            try {
                Bomb pc = new Bomb(
                        bombPos, 1f, (int) damage, radius,
                        TextureCollection.getInstance().getIndex("bomb")
                );
                worldBase.addObject(pc);
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
        radius *= 1.1f;
        damage *= 1.3f;
    }
}
