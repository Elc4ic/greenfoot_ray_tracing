import java.util.List;
import java.util.Random;

public class PaperPen extends Weapon {
    private long fireInterval = 5 * Const.SECOND;
    private int targets = 1;
    private Random rand = new Random();

    public PaperPen(Hero hero) {
        super(hero, "images\\paper.png", "models\\none.obj", "Paper Pen");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < getAS(fireInterval)) return;

        WorldBase worldBase = WorldBase.getInstance();
        List<WorldObject> objs = worldBase.getObjects();
        int i = 0;
        int t = Math.min(targets, worldBase.getEnemyCounter());
        while (i < t) {
            int index = rand.nextInt(objs.size());

            WorldObject o = objs.get(index);
            if (o instanceof Enemy enemy) {
                enemy.destroy(worldBase);
                i++;
            }
        }

        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        lvlUp();
        fireInterval -= 300 * Const.MILLI;
    }
}
