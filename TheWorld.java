
import com.aparapi.Kernel;
import com.aparapi.Range;
import greenfoot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;


public class TheWorld extends World {

    private final Interface interface1;
    private final Camera camera = new Camera(90);
    TextureCollection textures = TextureCollection.getInstance();
    private final Hero hero = new Hero(new float[]{0, 0, 0}, 0.5f, new float[]{0, 0, 0}, camera, 2);
    private WorldBase worldBase;

    private final FPSCounter fPSCounter = new FPSCounter();
    GreenfootImage frame = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
    Kernel rasterizer;
    Kernel transformer;

    private List<Triangle> trianglesList = new ArrayList<>();
    private List<Triangle> projectedTrianglesList = new ArrayList<>();
    private List<Integer> objIndexesList = new ArrayList<>();
    List<Triangle> clippedTriangles = new ArrayList<>();
    List<Integer> clippedIndexes = new ArrayList<>();
    private float[] triangles;
    private int[] objIndexes;
    private float[] positions;
    private float[] rotations;
    private int[] texture;
    private int[] textureSizes;

    private final float[] depth_buffer = new float[Const.PICXELS];
    private final int[] output = new int[Const.HEIGHT * Const.WIDTH];

    public TheWorld() throws IOException {
        super(Const.SCALED_WIDTH, Const.SCALED_HEIGHT, 1);

        Texture mapTexture = new Texture("images\\map.png", "map");
        Texture badanTexture = new Texture("images\\badan.png", "badan");
        textures.addTexture(mapTexture);
        textures.addTexture(badanTexture);

        worldBase = new Being(hero);
        interface1 = new Interface(hero, fPSCounter);
        addObject(interface1, interface1.getImg().getWidth() / 2, Const.SCALED_HEIGHT - interface1.getImg().getHeight() / 2);
        addObject(hero.getTimer(), Const.SCALED_WIDTH / 2, 20);
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
        if (hero.state == 0 || hero.getTimer().isZero()) {
            lose();
        } else if (hero.state == -1) {
            win();
        } else {

            if (worldBase.needUpdateBuffers) {
                initWorld();
            }
            render();
            try {
                worldBase.update();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            hero.updateHero(worldBase);
            interface1.update();
        }
        fPSCounter.update();
    }

    private void initTrianglesFromObjects(ArrayList<WorldObject> objects) {
        trianglesList.clear();
        objIndexesList.clear();
        int nOfObj = 0;
        for (WorldObject object : objects) {
            if (object instanceof ObjFile obj) {
                List<Triangle> t = obj.getTriangles();
                trianglesList.addAll(t);
                for (int i = 0; i < t.size(); i++) {
                    objIndexesList.add(nOfObj);
                }
                nOfObj++;
            }
        }
        rotations = new float[nOfObj * ObjFile.ROTATION_SIZE];
        positions = new float[nOfObj * ObjFile.POS_SIZE];
        texture = textures.getTextureBuff();

    }

    private void render() {
        initRotation(worldBase.getObjects());
        initPosition(worldBase.getObjects());

//        Range rangeT = Range.create(trianglesList.size());
//
//        transformer = new TransformKernel(trianglesList, objIndexesList, positions, rotations);
//        transformer.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.JTP);
//        transformer.execute(rangeT);

        project();
        clipping();
        camera.bindToHero(hero);

        initTriangles(clippedTriangles, clippedIndexes);

        IntStream.range(0, Const.PICXELS).forEach(i -> depth_buffer[i] = Float.MAX_VALUE);
        IntStream.range(0, Const.PICXELS).forEach(i -> output[i] = 0xffffff);

        Range range = Range.create(triangles.length / Triangle.SIZE);

        rasterizer = new RasterizerKernel(
                triangles,
                textureSizes, texture,
                output, depth_buffer,
                camera.fov, Const.WIDTH, Const.HEIGHT
        );

        rasterizer.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.JTP);
        rasterizer.setExplicit(true);

        rasterizer.put(triangles);
        rasterizer.put(objIndexes);
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

        frame.scale(Const.SCALED_WIDTH, Const.SCALED_HEIGHT);
        setBackground(frame);
    }

    private void initRotation(ArrayList<WorldObject> objects) {
        int nOfObj = 0;
        for (WorldObject object : objects) {
            if (object instanceof ObjFile obj) {
                float[] rotor = obj.getRotation();
                System.arraycopy(rotor, 0, rotations, nOfObj * ObjFile.POS_SIZE, rotor.length);
                nOfObj++;
            }
        }
    }

    private void initPosition(ArrayList<WorldObject> objects) {
        int nOfObj = 0;
        for (WorldObject object : objects) {
            if (object instanceof ObjFile obj) {
                float[] pos = obj.getPos();
                System.arraycopy(pos, 0, positions, nOfObj * ObjFile.POS_SIZE, pos.length);
                nOfObj++;
            }
        }
    }

    private void project() {
        projectedTrianglesList.clear();

        for (Triangle t : trianglesList) {
            projectedTrianglesList.add(t.flat(camera));
        }
    }

    private void clipping() {
        clippedTriangles.clear();
        clippedIndexes.clear();

        for (int j = 0; j < projectedTrianglesList.size(); j++) {
            List<Triangle> clipped = clipTriangle(projectedTrianglesList.get(j));
            if (clipped.isEmpty()) continue;
            clippedTriangles.addAll(clipped);
            for (int i = 0; i < clipped.size(); i++) {
                clippedIndexes.add(objIndexesList.get(j));
            }
        }
    }

    private void initTriangles(List<Triangle> trianglesList, List<Integer> objIndexesList) {
        textureSizes = textures.getTextureSizes(trianglesList);
        triangles = new float[trianglesList.size() * Triangle.SIZE];
        objIndexes = new int[objIndexesList.size()];
        for (int i = 0; i < trianglesList.size(); i++) {
            System.arraycopy(trianglesList.get(i).toFloatArray(), 0, triangles, i * Triangle.SIZE, Triangle.SIZE);
            objIndexes[i] = objIndexesList.get(i);
        }
    }

    private static final float NEAR_PLANE = 0.1f;

    private static float[] interpolate(float[] a, float[] b, float nearZ) {
        float t = (nearZ - a[2]) / (b[2] - a[2]);
        return new float[]{
                a[0] + (b[0] - a[0]) * t,
                a[1] + (b[1] - a[1]) * t,
                nearZ,
                a[3] + (b[3] - a[3]) * t,
                a[4] + (b[4] - a[4]) * t
        };
    }

    private static List<Triangle> clipTriangle(Triangle triangle) {
        List<float[]> inside = new ArrayList<>();
        List<float[]> outside = new ArrayList<>();

        int textureIndex = triangle.textureIndex;
        float[] v1 = triangle.v1;
        float[] v2 = triangle.v2;
        float[] v3 = triangle.v3;

        if (triangle.v1[2] >= NEAR_PLANE) inside.add(v1);
        else outside.add(v1);
        if (triangle.v2[2] >= NEAR_PLANE) inside.add(v2);
        else outside.add(v2);
        if (triangle.v3[2] >= NEAR_PLANE) inside.add(v3);
        else outside.add(v3);

        if (inside.isEmpty()) {
            return Collections.emptyList();
        } else if (inside.size() == 3) {
            return List.of(triangle);
        } else if (inside.size() == 1) {
            float[] a = inside.get(0);
            float[] b = interpolate(a, outside.get(0), NEAR_PLANE);
            float[] c = interpolate(a, outside.get(1), NEAR_PLANE);
            return List.of(new Triangle(a, b, c, textureIndex));
        } else {
            float[] a = inside.get(0);
            float[] b = inside.get(1);
            float[] c = interpolate(a, outside.get(0), NEAR_PLANE);
            float[] d = interpolate(b, outside.get(0), NEAR_PLANE);

            return List.of(
                    new Triangle(a, b, c, textureIndex),
                    new Triangle(b, d, c, textureIndex)
            );
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

