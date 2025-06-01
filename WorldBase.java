import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class WorldBase {

    private final float GRAVITY = 0.3f;
    private final ArrayList<WorldObject> objects = new ArrayList<>();
    private final Stack<WorldObject> objectsOnDestroy = new Stack<>();
    private final Stack<WorldObject> objectsOnAdd = new Stack<>();
    private int maxObjects = 150;
    private int objectsCounter = 0;
    public boolean needUpdateBuffers = false;

    public WorldBase(Hero hero) throws IOException {
        objects.add(hero);
    }

    public void update() throws IOException {
        for (WorldObject o : objects) {
            if (o instanceof Hero) continue;
//            if (o instanceof Npc npc) {
//                if (npc.updateNpc(objectsOnAdd, this)) objectsOnDestroy.add(o);
//            }
            if (o instanceof Projectile projectile) {
                if (projectile.update()) objectsOnDestroy.add(o);
            }
//            if (r.nextInt(10) == 1) addTimeSphere();
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

    //    public void addTimeSphere() throws IOException {
//        if (maxTimeCounter >= maxTimeSpheres) return;
//        needUpdateBuffers = true;
//        objectsOnAdd.add(new TimeSphere(
//                new float[]{r.nextInt(100) - 50, 1, r.nextInt(100) - 50},
//                2f,
//                ColorOperation.GColorToInt(new Color(130, 130, 130)),
//                true,
//                textureCollection.getIndex("orb"),
//                25000000000L)
//        );
//        maxTimeCounter++;
//    }

    public void deleteObject(WorldObject o) {
        objectsCounter--;
        needUpdateBuffers = true;
        objectsOnDestroy.add(o);
    }

    public void addObject(WorldObject o) {
        objectsCounter++;
        needUpdateBuffers = true;
        objectsOnAdd.add(o);
    }

    public GreenfootImage getLoadScreen() {
        GreenfootImage img = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
        img.setColor(Color.BLACK);
        img.fill();
        GreenfootImage img2 = new GreenfootImage(
                "move: WASD\n aim: find win shape",
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
