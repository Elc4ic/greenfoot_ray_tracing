import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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

        enemiesModel.add("models\\enemy1.obj");
        enemiesModel.add("models\\enemy2.obj");
        enemiesModel.add("models\\enemy3.obj");
        enemiesModel.add("models\\enemy4.obj");
        enemiesModel.add("models\\enemy5.obj");

        loadScreen.setColor(Color.BLACK);
        loadScreen.fill();
        GreenfootImage img2 = new GreenfootImage(
                "SILICONE HELL\n aim: keep alive",
                50,
                Color.WHITE,
                Color.BLACK);
        loadScreen.drawImage(
                img2,
                loadScreen.getWidth() / 2 - img2.getWidth() / 2,
                loadScreen.getHeight() / 2 - img2.getHeight() / 2
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
    private final int maxMaxEnemy = 150;
    private int maxEnemy = 2;
    private int enemyCounter = 0;
    private Random r = new Random();
    public boolean needUpdateBuffers = false;
    List<String> enemiesModel = new ArrayList<>();

    public boolean needLoadScreen = false;
    GreenfootImage loadScreen = new GreenfootImage(Const.WIDTH, Const.HEIGHT);

    public void update() throws IOException {
        for (WorldObject o : objects) {
            if (o instanceof Hero) continue;
            if (o instanceof Enemy enemy && enemy.update()) enemy.destroy(this);
            if (o instanceof Projectile projectile && projectile.update()) projectile.destroy(this);
//            if (o instanceof Experience exp) exp.unite(this);
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
        float enemyFactor = r.nextFloat() / 3f + 0.5f;
        String enemyModel = enemiesModel.get(r.nextInt(enemiesModel.size()));
        float angle = r.nextFloat() * Const.PI * 2f;
        float radius = 14f + r.nextFloat() * 20f;

        float x = hero.getPos()[0] + (float) Math.cos(angle) * radius;
        float z = hero.getPos()[2] + (float) Math.sin(angle) * radius;
        objectsOnAdd.add(new Enemy(
                        new float[]{x, 0, z},
                        new float[]{0, 0, 0},
                        enemyFactor,
                        hero,
                        enemyModel,
                        TextureCollection.getInstance().getIndex("enemy"),
                        (int) ((120 + 2 * maxEnemy) * enemyFactor)
                )
        );
        enemyCounter++;
    }

    public void deleteObject(WorldObject o) {
        if (o instanceof Enemy) {
            enemyCounter--;
            if (maxEnemy < maxMaxEnemy) {
                maxEnemy++;
            }
        }
        needUpdateBuffers = true;
        objectsOnDestroy.add(o);
    }

    public void addObject(WorldObject o) {
        if (o instanceof Enemy) enemyCounter++;
        needUpdateBuffers = true;
        objectsOnAdd.add(o);
    }

    public int getEnemyCounter() {
        return enemyCounter;
    }

    public GreenfootImage getLoadScreen() {
        return loadScreen;
    }

    public void setLoadScreen(GreenfootImage loadScreen) {
        this.loadScreen = loadScreen;
    }

    ArrayList<WorldObject> getObjects() {
        return objects;
    }

}
