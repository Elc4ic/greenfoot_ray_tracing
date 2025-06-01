import java.io.IOException;

/// WIFI - бьет по круговой площади
public class WiFi extends Weapon {
    private float radius = 1f;
    private float damage = 5f;
    private long fireInterval = 2 * Const.SECOND;


    public WiFi(Hero hero) throws IOException {
        super(
                hero.getPos(), new float[]{0, 0, 0}, 1f,
                "models\\wifi.obj",
                "images\\wifi.png",
                TextureCollection.getInstance().getIndex("wifi"),
                hero
        );
    }

    @Override
    public void fire(WorldBase worldBase) {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        setPos(getHero().getPos());
        for (WorldObject o : worldBase.getObjects()) {
            if (o instanceof Enemy enemy && o.getDistance(getPos()) <= radius) {
                if (o.getCollision(getPos(), radius)) {
                    enemy.applyDamage((int) damage);
                }
            }
        }

        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        lvlUp();
        radius *= 1.1f;
        damage *= 1.3f;
    }
}
