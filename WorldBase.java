import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class WorldBase {
    private static WorldBase instance;

    private WorldBase(Hero hero) throws IOException {
        this.hero = hero;
        objects.add(hero);

        objects.add(new ObjFile(
                        new float[]{0, 0, 0},
                        new float[]{0, 0, 0},
                        50f,
                        "models\\plane.obj",
                        TextureCollection.getInstance().getIndex("map")
                )
        );
    }

    public static synchronized WorldBase getInstance() {
        return instance;
    }

    public static synchronized WorldBase initInstance(Hero hero) throws IOException {
        if (instance == null) instance = new WorldBase(hero);
        return instance;
    }

    private Hero hero;
    private final float GRAVITY = 0.3f;
    private final ArrayList<WorldObject> objects = new ArrayList<>();
    private final Stack<WorldObject> objectsOnDestroy = new Stack<>();
    private final Stack<WorldObject> objectsOnAdd = new Stack<>();
    private final int maxEnemy = 10;
    private int enemyCounter = 0;
    private Random r = new Random();
    public boolean needUpdateBuffers = false;

    public void update() throws IOException {
        for (WorldObject o : objects) {
            if (o instanceof Hero) continue;
            if (o instanceof Enemy enemy && enemy.update()) enemy.destroy(this);
            if (o instanceof Projectile projectile && projectile.update()) projectile.destroy(this);
            if (r.nextInt(100) == 1) addEnemy();
        }
        while (!objectsOnAdd.empty()) {
            objects.add(objectsOnAdd.pop());
        }
        while (!objectsOnDestroy.empty()) {
            objects.remove(objectsOnDestroy.pop());
        }
    }

    public float getGRAVITY() {
        return GRAVITY;
    }

    public void addEnemy() throws IOException {
        if (enemyCounter >= maxEnemy) return;
        needUpdateBuffers = true;
        objectsOnAdd.add(new Enemy(
                new float[]{hero.getPos()[0] + r.nextInt(60) - 30, 0, hero.getPos()[2] + r.nextInt(60) - 30},
                new float[]{0, 0, 0},
                1f,
                hero,
                TextureCollection.getInstance().getIndex("enemy"))
        );
        enemyCounter++;
    }

    public void deleteObject(WorldObject o) {
        if (o instanceof Enemy) enemyCounter--;
        needUpdateBuffers = true;
        objectsOnDestroy.add(o);
    }

    public void addObject(WorldObject o) {
        if (o instanceof Enemy) enemyCounter++;
        needUpdateBuffers = true;
        objectsOnAdd.add(o);
    }

    public GreenfootImage getLoadScreen() {
        GreenfootImage img = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
        img.setColor(Color.BLACK);
        img.fill();
        GreenfootImage img2 = new GreenfootImage(
                "SILICONE HELL\n aim: keep alive",
                50,
                Color.WHITE,
                Color.BLACK);
        img.drawImage(
                img2,
                img.getWidth() / 2 - img2.getWidth() / 2,
                img.getHeight() / 2 - img2.getHeight() / 2);
        return img;
    }

    ArrayList<WorldObject> getObjects() {
        return objects;
    }

}
