import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Disk extends Weapon {
    private float damage = 14f;
    private int projectileCount = 2;
    private List<Satellite> disks = new ArrayList<>();

    public Disk(Hero hero) {
        super(hero, "images\\disk.png", "models\\circle.obj");
    }

    @Override
    public void fire() {
        if (disks.size() < projectileCount) {
            for (Satellite disk : disks) {
                WorldBase.getInstance().deleteObject(disk);
            }
            disks.clear();
            try {
                float delta = Const.PI * 2 / projectileCount;
                for (int i = 0; i < projectileCount; i++) {
                    float dy = i * delta;
                    createDisk(dy);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void upgrade() {
        lvlUp();
        damage *= 1.2f;
        projectileCount++;
    }

    private void createDisk(float dy) throws IOException {
        Satellite disk = new Satellite(
                getHero(), getHero().getPos(), new float[]{0, dy, 0}, 1f, (int) damage,
                getProjectileModel(),
                TextureCollection.getInstance().getIndex("disk_texture")
        );
        disk.setRotSpeed(new float[]{0, 6, 0});
        disk.setMovementFunction((a, b, c) -> {
            float[] unit = new float[]{0, 0, 3};
            Vector3.rotateYr(unit, c[1]);
            return Vector3.add(a, unit);
        });
        disks.add(disk);
        WorldBase.getInstance().addObject(disk);
    }

}
