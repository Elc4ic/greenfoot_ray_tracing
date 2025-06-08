import java.io.IOException;
import java.util.List;

/// интернет кабель RJ45 - стреляет в ближайшего врага пакетами данных
public class RJ45 extends Weapon {
    private float damage = 12f;
    private int projectileCount = 3;
    private int projectileFired = 0;
    private int penetrations = 1;

    private long fireInterval = 2 * Const.SECOND;

    public RJ45(Hero hero) {
        super(hero, "images\\rj45.png", "models\\orb.obj", "RJ45");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < getAS(fireInterval)) return;

        WorldBase world = WorldBase.getInstance();

        try {
            Enemy enemy = getNearestEnemy(world.getObjects());
            if (enemy == null) return;
            float[] n = getHero().getNormal(enemy.getPos());
            Missile pc = new Missile(
                    getHero().getPos(), n, getProjectileModel(), 1f, 1f,
                    (int) getDMG(damage), 90f, penetrations,
                    TextureCollection.getInstance().getIndex("bullet")
            );
            world.addObject(pc);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        int ModProjectileCount = getProj(projectileCount);
        if (projectileFired < ModProjectileCount) {
            projectileFired++;
        } else {
            projectileFired = 0;
            setLastFireTime(System.nanoTime());
        }
    }

    public Enemy getNearestEnemy(List<WorldObject> objs) {
        float minDistance = Float.MAX_VALUE;
        Enemy nearest = null;
        for (WorldObject o : objs) {
            if (o instanceof Enemy enemy) {
                float distance = o.getDistance(getHero().getPos());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = enemy;
                }
            }
        }
        return nearest;
    }

    @Override
    public void upgrade() {
        lvlUp();
        projectileCount++;
        fireInterval -= 200 * Const.MILLI;
    }
}
