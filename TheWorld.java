
import com.aparapi.Kernel;
import com.aparapi.Range;
import greenfoot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;


public class TheWorld extends World {

    private final Camera camera = new Camera(90);
    private final Hero hero = new Hero(new float[]{0, 0, 0}, new float[]{0, 0, 0}, 0.5f, 2);
    private WorldBase worldBase;
    private Random rand = new Random();

    private final Timer timer = new Timer(Const.TICK_RATE);
    private final FPSCounter fPSCounter = new FPSCounter();
    private final Interface interface1 = new Interface(hero, fPSCounter);
    private final Inventory inventory = new Inventory(hero);
    private final Weapon[] weaponsPool = {
            new RJ45(hero),
            new WiFi(hero),
            new MidTower(hero),
            new OpticFiber(hero),
            new Disk(hero),
            new KeyBoard(hero),
            new PaperPen(hero),
            new Scooter(hero),
            new Virus(hero)
    };
    private final ChoisePlate cp1 = new ChoisePlate(hero);
    private final ChoisePlate cp2 = new ChoisePlate(hero);
    private final ChoisePlate cp3 = new ChoisePlate(hero);
    private final ChoiseMenu choiseMenu = new ChoiseMenu(cp1, cp2, cp3);


    GreenfootImage frame = new GreenfootImage(Const.WIDTH, Const.HEIGHT);

    private final List<Triangle> trianglesList = new ArrayList<>();
    private final List<Triangle> tfc_trianglesList = new ArrayList<>();

    private final float[] depth_buffer = new float[Const.PICXELS];
    private final int[] output = new int[Const.HEIGHT * Const.WIDTH];

    public TheWorld() throws IOException {
        super(Const.WIDTH, Const.HEIGHT, 1);

        worldBase = WorldBase.initInstance(hero);
        addObject(interface1, interface1.getImg().getWidth() / 2, Const.HEIGHT - interface1.getImg().getHeight() / 2);
        addObject(inventory, 100, 40);
        addObject(timer, Const.WIDTH / 2, 20);
        addObject(choiseMenu, Const.WIDTH / 2, Const.HEIGHT / 2);
        addObject(cp1, Const.WIDTH / 2, Const.HEIGHT / 2);
        addObject(cp2, Const.WIDTH / 2 - 76, Const.HEIGHT / 2);
        addObject(cp3, Const.WIDTH / 2 + 76, Const.HEIGHT / 2);

        hero.addWeapon(weaponsPool[rand.nextInt(weaponsPool.length)]);

        setBackground(worldBase.getLoadScreen());
        initWorld();
    }

    void initWorld() {
        initTrianglesFromObjects(worldBase.getObjects());
        System.gc();
    }

    public void act() {
        if (hero.getState() == Creature.STATE_DEAD) {
            lose();
        } else if (timer.getTime() >= Const.WIN_TIME) {
            win();
        } else if (hero.getState() == Creature.STATE_UPGRADE) {
            choiseMenu.showMenu(weaponsPool);
            if (choiseMenu.isWeaponSelected()) {
                choiseMenu.hide();
                hero.setState(Creature.STATE_ALIVE);
            }
        } else {

            if (worldBase.needUpdateBuffers) {
                initWorld();
            }
            if (worldBase.needLoadScreen) {
                setBackground(worldBase.getLoadScreen());
                Greenfoot.delay(400);
                worldBase.needLoadScreen = false;
            } else {
                render();

                if (timer.update()) {
                    try {
                        worldBase.update();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    hero.updateHero();
                    camera.bindToHero(hero);
                    interface1.update();
                    inventory.update();
                }
            }
        }
        fPSCounter.update();
    }

    private void initTrianglesFromObjects(ArrayList<WorldObject> objects) {
        trianglesList.clear();
        int nOfObj = 0;
        for (WorldObject object : objects) {
            if (object instanceof ObjFile obj) {
                List<Triangle> t = obj.getTriangles(nOfObj);
                trianglesList.addAll(t);
                nOfObj++;
            }
        }
    }

    private void render() {
        IntStream.range(0, Const.PICXELS).forEach(i -> {
            depth_buffer[i] = Float.MAX_VALUE;
            output[i] = 0xffffffff;
        });
        tfc_trianglesList.clear();
        trianglesList.forEach(
                t -> tfc_trianglesList.addAll(t.transform(worldBase.getObjects()).flat(camera).clip())
        );
        tfc_trianglesList.forEach(t -> t.rasterize(depth_buffer, output));

        for (int y = 0; y < Const.HEIGHT; y++) {
            for (int x = 0; x < Const.WIDTH; x++) {
                frame.setColorAt(x, y, ColorOperation.IntToGColor(output[y * Const.WIDTH + x]));
            }
        }

        setBackground(frame);
    }

    void lose() {
        GreenfootImage img = new GreenfootImage(
                getWidth(),
                getHeight());
        GreenfootImage img2 = new GreenfootImage(
                "YOU DIED",
                50,
                Color.RED,
                Color.BLACK);
        img.drawImage(
                img2,
                img.getWidth() / 2 - img2.getWidth() / 2,
                img.getHeight() / 2 - img2.getHeight() / 2);
        setBackground(img);
    }

    void win() {
        GreenfootImage img = new GreenfootImage(
                getWidth(),
                getHeight());
        GreenfootImage img2 = new GreenfootImage(
                "YOU WIN",
                50,
                Color.GREEN,
                Color.BLACK);
        img.drawImage(
                img2,
                img.getWidth() / 2 - img2.getWidth() / 2,
                img.getHeight() / 2 - img2.getHeight() / 2);
        setBackground(img);
    }

}


class ColorOperation {
    static final int yellow = ColorOperation.GColorToInt(Color.YELLOW);
    static final int red = ColorOperation.GColorToInt(Color.RED);
    static final int gray = ColorOperation.GColorToInt(Color.GRAY);
    static final int green = ColorOperation.GColorToInt(Color.GREEN);

    public static int getRed(int c) {
        return (c >> 16) & 0xFF;
    }

    public static int getGreen(int c) {
        return (c >> 8) & 0xFF;
    }

    public static int getBlue(int c) {
        return c & 0xFF;
    }

    public static int mulColor(int c, float d) {
        int r = (int) (getRed(c) * d);
        int g = (int) (getGreen(c) * d);
        int b = (int) (getBlue(c) * d);
        return r << 16 | g << 8 | b;
    }


    public static int addColor(int c, int c1) {
        int r = getRed(c) + getRed(c1);
        int g = getGreen(c) + getGreen(c1);
        int b = getBlue(c) + getGreen(c1);
        return r << 16 | g << 8 | b;
    }

    public static int mulColor(int c, float[] v) {
        int r = (int) (getRed(c) * v[0]);
        int g = (int) (getGreen(c) * v[1]);
        int b = (int) (getBlue(c) * v[2]);
        return r << 16 | g << 8 | b;
    }

    public static int GColorToInt(Color color) {
        return color.getRed() << 16 | color.getGreen() << 8 | color.getBlue();
    }

    public static Color IntToGColor(int rgb) {
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }
}

