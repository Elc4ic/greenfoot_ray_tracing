import java.io.IOException;
import java.util.List;

/// интернет кабель RJ45 - стреляет в ближайшего врага пакетами данных
public class RJ45 extends Weapon {
    private float damage = 12f;
    private int projectileCount = 1;
    private int penetrations = 1;

    private long fireInterval = 2 * Const.SECOND;
    private long interval = 400 * Const.MILLI;

    public RJ45(Hero hero) {
        super(hero, "images\\rj45.png", "models\\orb.obj");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        WorldBase world = WorldBase.getInstance();
        for (int i = 0; i < projectileCount; i++) {
            try {
                Enemy enemy = getNearestEnemy(world.getObjects());
                if (enemy == null) return;
                float[] n = getHero().getNormal(enemy.getPos());
                Missile pc = new Missile(
                        getHero().getPos(), n, getProjectileModel(), 1f, 1f,
                        (int) damage, 90f, penetrations,
                        TextureCollection.getInstance().getIndex("bullet")
                );
                world.addObject(pc);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        setLastFireTime(System.nanoTime());
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
