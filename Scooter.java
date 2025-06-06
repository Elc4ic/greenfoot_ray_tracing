import java.io.IOException;

public class Scooter extends Weapon {
    private long fireInterval = 20 * Const.SECOND;
    private float speed = 0.6f;
    private float damage = 50f;

    public Scooter(Hero hero) {
        super(hero, "images\\scooter.png", "models\\scooter.obj");
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        WorldBase world = WorldBase.getInstance();
        try {
            Satellite scooter = new Satellite(getHero(), getHero().getPos(), getHero().getRotation(), 0.08f, (int) damage, getProjectileModel(), TextureCollection.getInstance().getIndex("portal"));
            scooter.setMovementFunction((a, b, c) -> {
                a.addToPos(Vector3.scale(a.getDirection(), speed));
                return a.getPos();
            });
            scooter.setCollisionR(3f);
            scooter.setTimeToLive(70);
            world.addObject(scooter);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        speed += 0.1f;
        damage *= 1.2f;
        fireInterval -= 2 * Const.SECOND;
    }
}
