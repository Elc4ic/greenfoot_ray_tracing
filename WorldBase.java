import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class WorldBase {

    private final ArrayList<WorldObject> objects = new ArrayList<>();
    private final Stack<WorldObject> objectsOnDestroy = new Stack<>();
    private final Stack<WorldObject> objectsOnAdd = new Stack<>();
    private final Random r = new Random();
    TextureCollection textureCollection = TextureCollection.getInstance();
    public WorldBase newWorld;
    private int maxTimeSpheres = 150;
    private int maxTimeCounter = 0;
    public boolean needChangeWorld = false;
    public boolean needUpdateBuffers = false;

    public WorldBase(Hero hero) throws IOException {
        objects.add(hero);
        Texture orbTexture = new Texture("images\\orb.png", "orb");
        textureCollection.addTexture(orbTexture);
    }

    public void update() throws IOException {
        for (WorldObject o : objects) {
            if (o instanceof Hero) continue;
//            if (o instanceof Npc npc) {
//                if (npc.updateNpc(objectsOnAdd, this)) objectsOnDestroy.add(o);
//            }
//            if (o instanceof TimeSphere obj) {
//                obj.addToRotation(new float[]{0f, 2, 0});
//            }
            if (o instanceof Bullet) {
                if (((Bullet) o).update()) {
                    objectsOnDestroy.add(o);
                }
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
        if (o instanceof TimeSphere) maxTimeCounter--;
        needUpdateBuffers = true;
        objectsOnDestroy.add(o);
    }

    public GreenfootImage getLoadScreen() {
        GreenfootImage img = new GreenfootImage(Const.SCALED_WIDTH, Const.SCALED_HEIGHT);
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

    Random getR() {
        return r;
    }

}
