import java.io.IOException;

/// WIFI - бьет по круговой площади
public class WiFi extends Weapon {
    private float radius = 10f;
    private float damage = 20f;
    private float repulsion = 1.5f;
    private long fireInterval = Const.SECOND;
    Satellite field;

    public WiFi(Hero hero) throws IOException {
        super(hero, "images\\wifi.png", "models\\circle.obj");
        float[] pos = Vector3.add(getHero().getPos(), new float[]{0, 0.5f, 0});
        field = new Satellite(pos, radius, (int) damage, getProjectileModel(),TextureCollection.getInstance().getIndex("wifi_texture"));
        field.setMovementFunction(() -> {});
        WorldBase.getInstance().addObject(field);
    }

    @Override
    public void fire() {
        field.setPos(getHero().getPos());
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        for (WorldObject o : WorldBase.getInstance().getObjects()) {
            if (o instanceof Enemy enemy && o.getDistance(getHero().getPos()) <= radius) {
                if (o.haveCollision(field.getPos(), radius)) {
                    enemy.applyDamage(-(int) damage);
                    enemy.addToPos(Vector3.scale(field.getNormal(enemy.getPos()), repulsion));
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
