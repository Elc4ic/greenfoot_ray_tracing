import greenfoot.Color;
import greenfoot.GreenfootImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class WorldBase {
    private final ArrayList<WorldObject> objects = new ArrayList<>();
    private final ArrayList<Light> lights = new ArrayList<>();
    private final Stack<WorldObject> objectsOnDestroy = new Stack<>();
    private final Stack<WorldObject> objectsOnAdd = new Stack<>();
    private final Random r = new Random();
    private WorldObject winShape;
    public TextureCollection textureCollection = new TextureCollection();
    public WorldBase newWorld;
    public boolean needChangeWorld = false;

//    public WorldBase(Hero hero) {
//        this.hero = hero;
//    }

    public void update() {
        for (WorldObject o : objects) {
            if (o instanceof Npc) {
                if (((Npc) o).updateNpc(objectsOnAdd, this)) objectsOnDestroy.add(o);
            }
//            if (o instanceof ObjFile) {
//                ((ObjFile) o).rotateX(10);
//            }
            if (o instanceof Bullet) {
                if (((Bullet) o).update()) {
                    if (((Bullet) o).isBfg()) {
                        for (int i = 0; i < 100; i++) {
                            float[] n = Vector3.normalize(new float[]{r.nextFloat() - 0.5f, r.nextFloat() - 0.5f, r.nextFloat() - 0.5f});
                            objectsOnAdd.add(new Bullet(
                                    o.getPos(),
                                    0.3f, ColorOperation.green,
                                    n, 0.4f, 80, 3, 1)
                            );
                        }
                    }
                    objectsOnDestroy.add(o);
                }
            }
        }
        while (!objectsOnAdd.empty()) {
            objects.add(objectsOnAdd.pop());
        }
        while (!objectsOnDestroy.empty()) {
            objects.remove(objectsOnDestroy.pop());
        }
    }

    public void addPortal(WorldBase world) throws IOException {
        Texture portalTexture = new Texture("D:\\C_project\\Raytracer\\images\\portal.png");
        textureCollection.addTexture(portalTexture);

        objects.add(new Portal(
                new float[]{7, 0, 3},
                2f,
                ColorOperation.GColorToInt(new Color(130, 130, 130)),
                "D:\\C_project\\Raytracer\\models\\portal.obj",
                true,
                textureCollection.getIndex(portalTexture),
                world)
        );
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

    ArrayList<Light> getLights() {
        return lights;
    }

    WorldObject getWinShape() {
        return winShape;
    }

    boolean isNeedChangeWorld() {
        return needChangeWorld;
    }

    WorldBase getNewWorld() {
        needChangeWorld = false;
        return newWorld;
    }

    Random getR() {
        return r;
    }

    void setWinShape(WorldObject winShape) {
        this.winShape = winShape;
    }
}
