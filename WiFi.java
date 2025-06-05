import java.io.IOException;

/// WIFI - бьет по круговой площади
public class WiFi extends Weapon {
    private float radius = 8f;
    private float damage = 16f;
    private float repulsion = 1.5f;
    private long fireInterval = Const.SECOND;
    Satellite field;

    public WiFi(Hero hero) {
        super(hero, "images\\wifi.png", "models\\circle.obj");
    }

    @Override
    public void fire() {

        if(field == null) {
            float[] pos = Vector3.add(getHero().getPos(), new float[]{0, 0.5f, 0});
            try {
                field = new Satellite(getHero(), pos, new float[]{0, 0, 0}, radius, (int) damage, getProjectileModel(), TextureCollection.getInstance().getIndex("wifi_texture"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            field.setMovementFunction((a, b,c) -> a);
            WorldBase.getInstance().addObject(field);
        }

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
        radius *= 1.2f;
        damage *= 1.3f;
    }
}
