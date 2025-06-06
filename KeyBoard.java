import java.io.IOException;

public class KeyBoard extends Weapon {
    private float damage = 45;
    private int wingCount = 2;
    private final long cycleTime = 2 * Const.SECOND;

    private float dy;
    private int wingNow = 0;
    private long fireInterval;

    public KeyBoard(Hero hero) {
        super(hero, "images\\keyboard.png", "models\\key_sword.obj");
        initData();
    }

    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        WorldBase world = WorldBase.getInstance();

        try {
            float[] posL = Vector3.add(getHero().getPos(), new float[]{0, 0.3f, 0});
            Sword sword = new Sword(getHero(), posL, new float[]{0, dy * wingNow, 0}, 0.1f, (int) damage);
            sword.setRotSpeed(new float[]{0, 12, 0});
            sword.setMovementFunction((a, b, c) -> a.getPos());
            world.addObject(sword);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (wingNow < wingCount-1) wingNow++;
        else wingNow = 0;
        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        lvlUp();
        damage *= 1.2f;
        wingCount++;
        initData();
    }

    private void initData() {
        dy = 2 * Const.PI / wingCount;
        fireInterval = cycleTime / wingCount;
    }
}
