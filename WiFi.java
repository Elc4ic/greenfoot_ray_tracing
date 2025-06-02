/// WIFI - бьет по круговой площади
public class WiFi extends Weapon {
    private float radius = 1f;
    private float damage = 5f;
    private long fireInterval = 2 * Const.SECOND;


    public WiFi(Hero hero) {
        super(hero, "models\\wifi.obj", "images\\wifi.png");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        for (WorldObject o :  WorldBase.getInstance().getObjects()) {
            if (o instanceof Enemy enemy && o.getDistance(getHero().getPos()) <= radius) {
                if (o.haveCollision(getHero().getPos(), radius)) {
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
