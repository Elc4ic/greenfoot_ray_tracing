import java.io.IOException;
import java.util.List;
import java.util.Random;

public class PaperPen extends Weapon {
    private int damage = 4;
    private long fireInterval = 5 * Const.SECOND;
    private int timeToLive = 70;
    private int targets = 1;
    private Random rand = new Random();

    public PaperPen(Hero hero) {
        super(hero, "images\\paper.png", "models\\block.obj", "Paper Pen");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < getAS(fireInterval)) return;

        WorldBase worldBase = WorldBase.getInstance();
        List<WorldObject> objs = worldBase.getObjects();
        int i = 0;
        int t = Math.min(getProj(targets), worldBase.getEnemyCounter());
        while (i < t) {
            int index = rand.nextInt(objs.size());

            WorldObject o = objs.get(index);
            if (o instanceof Enemy enemy) {
                enemy.destroy(worldBase);
                try {
                    Satellite summon = new Satellite(
                            getHero(),
                            enemy.getPos(),
                            new float[]{0, 0, 0},
                            0.5f,
                            (int) getDMG(damage),
                            getProjectileModel(),
                            TextureCollection.getInstance().getIndex("orb")
                    );
                    summon.setTimeToLive(timeToLive);
                    summon.setMovementFunction((a, b, c) -> {
                        Enemy enemyToGo = a.getNearestEnemy();
                        if (enemyToGo == null) return b;
                        float[] n = enemyToGo.getNormal(b);
                        float[] toEnemy = Vector3.scale(n, -0.4f);
                        return Vector3.add(b, toEnemy);
                    });
                    worldBase.addObject(summon);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
        }

        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        lvlUp();
        damage += 2;
        targets++;
        fireInterval -= 200 * Const.MILLI;
        timeToLive += 12;
    }
}
