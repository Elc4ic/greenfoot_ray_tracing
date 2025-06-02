
import com.aparapi.Kernel;
import com.aparapi.Range;
import greenfoot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


public class TheWorld extends World {


    private final Camera camera = new Camera(90);
    private final Hero hero = new Hero(new float[]{0, 0, 0}, new float[]{0, 0, 0}, 0.5f, 2);
    private final TextureCollection textureCollection = TextureCollection.getInstance();
    private WorldBase worldBase;

    private final Timer timer = new Timer(Const.TICK_RATE);
    private final FPSCounter fPSCounter = new FPSCounter();
    private final Interface interface1 = new Interface(hero, fPSCounter);
    GreenfootImage frame = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
    Kernel rasterizer;

    private final List<Triangle> trianglesList = new ArrayList<>();
    private final List<Triangle> tfc_trianglesList = new ArrayList<>();

    private float[] triangles;
    private float[] positions;
    private float[] rotations;
    private int[] texture;
    private int[] textureSizes;

    private final float[] depth_buffer = new float[Const.PICXELS];
    private final int[] output = new int[Const.HEIGHT * Const.WIDTH];

    public TheWorld() throws IOException {
        super(Const.WIDTH, Const.HEIGHT, 1);

        Texture mapTexture = new Texture("images\\map.png", "map");
        Texture orbTexture = new Texture("images\\orb.png", "orb");
        Texture bulletTexture = new Texture("images\\bullet.png", "bullet");
        Texture enemyTexture = new Texture("images\\enemy.png", "enemy");
        Texture wallTexture = new Texture("images\\wall.png", "wall");
        Texture portalTexture = new Texture("images\\portal.png", "portal");

        textureCollection.addTexture(mapTexture);
        textureCollection.addTexture(orbTexture);
        textureCollection.addTexture(bulletTexture);
        textureCollection.addTexture(enemyTexture);
        textureCollection.addTexture(wallTexture);
        textureCollection.addTexture(portalTexture);

        worldBase = WorldBase.initInstance(hero);
        addObject(interface1, interface1.getImg().getWidth() / 2, Const.HEIGHT - interface1.getImg().getHeight() / 2);
        addObject(timer, Const.WIDTH / 2, 20);
        loadScreen();
        initWorld();
    }

    void initWorld() {
        initTrianglesFromObjects(worldBase.getObjects());
        System.gc();
    }

    public void loadScreen() {
        setBackground(worldBase.getLoadScreen());
    }

    public void act() {
        if (hero.state == 0) {
            lose();
        } else if (timer.getTime() >= Const.WIN_TIME) {
            win();
        } else {

            if (worldBase.needUpdateBuffers) {
                initWorld();
            }
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
        rotations = new float[nOfObj * ObjFile.ROTATION_SIZE];
        positions = new float[nOfObj * ObjFile.POS_SIZE];
        texture = textureCollection.getTextureBuff();
    }

    private void render() {
        tfc_trianglesList.clear();

        trianglesList.forEach(
                t -> tfc_trianglesList.addAll(t.transform(worldBase.getObjects()).flat(camera).clip(0.01f))
        );

        initTriangles(tfc_trianglesList);

        IntStream.range(0, Const.PICXELS).forEach(i -> depth_buffer[i] = Float.MAX_VALUE);
        IntStream.range(0, Const.PICXELS).forEach(i -> output[i] = 0xffffff);

        Range range = Range.create(triangles.length / Triangle.SIZE);

        rasterizer = new RasterizerKernel(
                triangles,
                textureSizes, texture,
                output, depth_buffer,
                Const.WIDTH, Const.HEIGHT
        );

        rasterizer.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.JTP);
        rasterizer.setExplicit(true);

        rasterizer.put(triangles);
        rasterizer.put(positions);
        rasterizer.put(rotations);
        rasterizer.put(camera.getPos());
        rasterizer.put(camera.getRotations());
        rasterizer.put(textureSizes);
        rasterizer.put(texture);
        rasterizer.put(depth_buffer);

        rasterizer.execute(range);
        rasterizer.get(output);
        for (int y = 0; y < Const.HEIGHT; y++) {
            for (int x = 0; x < Const.WIDTH; x++) {
                frame.setColorAt(x, y, ColorOperation.IntToGColor(output[y * Const.WIDTH + x]));
            }
        }

        rasterizer.dispose();

        setBackground(frame);
    }

    private void initTriangles(List<Triangle> trianglesList) {
        textureSizes = textureCollection.getTextureSizes(trianglesList);
        triangles = new float[trianglesList.size() * Triangle.SIZE];
        for (int i = 0; i < trianglesList.size(); i++) {
            System.arraycopy(trianglesList.get(i).toFloatArray(), 0, triangles, i * Triangle.SIZE, Triangle.SIZE);
        }
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

