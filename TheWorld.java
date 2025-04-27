
import com.aparapi.Kernel;
import com.aparapi.Range;
import greenfoot.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TheWorld extends World {

    private final Interface interface1;
    private final Hero hero = new Hero(new float[]{3, 1.4f, 3}, new float[]{1, 0, 1}, 45);

    private WorldBase worldBase = new WorldBase();

    private final WorldBase hell = new Hell();
    private final WorldBase being = new Being();
//    private final WorldBase maze = new MazeWorld(hero);

    private final FPSCounter fPSCounter = new FPSCounter();
    GreenfootImage frame = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
    Kernel kernel;
    Range range = Range.create(Const.HEIGHT * Const.WIDTH);

    private int[] sizes;
    private float[] bvhBounds;
    private float[] cache;
    private int[] leafInsides;
    private float[] vertices;
    private float[] texCoords;
    private int[] texture;

    private final float[] rays = new float[Const.HEIGHT * Const.WIDTH * Ray.RAY_SIZE];
    private final int[] output = new int[Const.HEIGHT * Const.WIDTH];

    public TheWorld() throws IOException {
        super(Const.SCALED_WIDTH, Const.SCALED_HEIGHT, 1);

        interface1 = new Interface(hero, fPSCounter);
        addObject(interface1, interface1.getImg().getWidth() / 2, Const.SCALED_HEIGHT - interface1.getImg().getHeight() / 2);
//        for (Gun g : hero.arsenal) {
//            addObject(g, g.x, g.y);
//        }

        loadScreen();
        hell.addPortal(being);
        being.addPortal(hell);

        initWorld();
    }

    void initWorld() {
        int n = initBuffersFromObjects(worldBase.getObjects());
        kernel = new RayTracerKernel(
                n, sizes, bvhBounds, cache, vertices, texCoords, leafInsides, texture, output, rays
        );
        kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.GPU);
        kernel.setExplicit(true);
        System.gc();
    }

    public void loadScreen() {
        setBackground(worldBase.getLoadScreen());
    }

    public void act() {
        if (hero.state == 0) {
            lose();
        } else if (hero.state == -1) {
            win();
        } else {
            if (Greenfoot.mouseMoved(null)) {
                MouseInfo mouse = Greenfoot.getMouseInfo();
                int x = mouse.getX();
                int y = mouse.getY();
                int mx = Const.SCALED_WIDTH / 2 - x;
                int my = y - Const.SCALED_HEIGHT / 2;
                hero.rotateXn(mx / 10f);
//                hero.rotateYn(my / 10.0);
            }

            if (Greenfoot.isKeyDown("a")) {
                hero.moveRight();
            }
            if (Greenfoot.isKeyDown("i") && worldBase instanceof MazeWorld world) {
                world.getHeroNormal();
            }
            if (Greenfoot.isKeyDown("9")) {
                loadScreen();
                worldBase = new MazeWorld(hero);
                initWorld();
            }
            if (Greenfoot.isKeyDown("7")) {
                loadScreen();
                worldBase = hell;
                initWorld();
            }
//            if (Greenfoot.isKeyDown("8")) {
//                loadScreen();
//                worldBase = being;
//                initWorld();
//            }
            if (Greenfoot.isKeyDown("d")) {
                hero.moveLeft();
            }
            if (Greenfoot.isKeyDown("w")) {
                hero.moveForward();
            }
            if (Greenfoot.isKeyDown("s")) {
                hero.moveBackward();
            }
            if (Greenfoot.isKeyDown("1")) {
                hero.switchGun(0);
            }
            if (Greenfoot.isKeyDown("2")) {
                hero.switchGun(1);
            }
            if (Greenfoot.isKeyDown("3")) {
                hero.switchGun(2);
            }
            if (Greenfoot.isKeyDown("4")) {
                hero.switchGun(3);
            }

            render();

            worldBase.update();

            if (worldBase.isNeedChangeWorld()) {
//                loadScreen();
                worldBase = worldBase.getNewWorld();
                initWorld();
            }

            hero.updateHero(worldBase);
            hero.winCondition(worldBase.getWinShape());
            interface1.update();
        }
        fPSCounter.update();
    }


    private int initBuffersFromObjects(ArrayList<WorldObject> objects) {
        //init sizes
        int[] sizeNode = new int[ObjFile.SIZES_SIZE];
        int nOfObj = (int) objects.stream().filter(obj -> obj instanceof ObjFile).count();
        sizes = new int[nOfObj * ObjFile.SIZES_SIZE];
        //init other arrays by size
        nOfObj = 0;
        for (WorldObject object : objects) {
            if (object instanceof ObjFile obj) {
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.VERTEX_OFFSET] = sizeNode[ObjFile.VERTEX_OFFSET];
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXT_COORD_OFFSET] = sizeNode[ObjFile.TEXT_COORD_OFFSET];
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET] = sizeNode[ObjFile.LEAFS_OFFSET];
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.FACE_COUNT_OFFSET] = obj.getFaceCount();
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_OFFSET] = sizeNode[ObjFile.TEXTURE_OFFSET];
                sizeNode[ObjFile.VERTEX_OFFSET] += obj.getVertexSize();
                sizeNode[ObjFile.TEXT_COORD_OFFSET] += obj.getTextureCoordSize();
                sizeNode[ObjFile.LEAFS_OFFSET] += obj.getLeafIndicesSize();

                Texture texture = worldBase.textureCollection.getTexture(obj.textureIndex);
                int offset = worldBase.textureCollection.getOffset(obj.textureIndex);
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_WIDTH] = texture.textureWidth;
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_HEIGHT] = texture.textureHeight;
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_OFFSET] = offset;
                nOfObj++;
            }
        }
        bvhBounds = new float[nOfObj * 6];
        cache = new float[sizeNode[ObjFile.LEAFS_OFFSET] / 6 * 13];
        leafInsides = new int[sizeNode[ObjFile.LEAFS_OFFSET]];
        vertices = new float[sizeNode[ObjFile.VERTEX_OFFSET] * 3];
        texCoords = new float[sizeNode[ObjFile.TEXT_COORD_OFFSET] * 2];
        texture = new int[worldBase.textureCollection.getAllTexturesSize()];
        //fill arrays
        nOfObj = 0;
        for (int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof ObjFile obj) {
                float[] bvhb = obj.getBoundsBuff();
                float[] cacheBuff = obj.getCacheBuff();
                int[] leaf = obj.getLeafIndices();
                float[] vertex = obj.getVerticesBuff();
                float[] t_coord = obj.getTextCoordsBuff();

                System.arraycopy(vertex, 0, vertices, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.VERTEX_OFFSET] * 3, vertex.length);
                System.arraycopy(t_coord, 0, texCoords, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXT_COORD_OFFSET] * 2, t_coord.length);
                System.arraycopy(cacheBuff, 0, cache, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET] / 6 * 13, cacheBuff.length);
                System.arraycopy(leaf, 0, leafInsides, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET], leaf.length);
                System.arraycopy(bvhb, 0, bvhBounds, nOfObj * ObjFile.BVH_BOUND_SIZE, bvhb.length);

                nOfObj++;
            }
        }
        List<Texture> textures = worldBase.textureCollection.collection;
        for (int i = 0; i < textures.size(); i++) {
            int[] text = worldBase.textureCollection.getTextureBuff(i);
            System.arraycopy(text, 0, texture, worldBase.textureCollection.getOffset(i), text.length);
        }
        return nOfObj;
    }

    private void render() {
        initRays();
        kernel.put(sizes);
        kernel.put(bvhBounds);
        kernel.put(cache);
        kernel.put(vertices);
        kernel.put(texCoords);
        kernel.put(leafInsides);
        kernel.put(texture);
        kernel.put(rays);
        kernel.execute(range);
        kernel.get(output);
        for (int y = 0; y < Const.HEIGHT; y++) {
            for (int x = 0; x < Const.WIDTH; x++) {
                frame.setColorAt(x, y, ColorOperation.IntToGColor(output[y * Const.WIDTH + x]));
            }
        }
        kernel.dispose();
        frame.scale(Const.SCALED_WIDTH, Const.SCALED_HEIGHT);
        setBackground(frame);
    }

    private void initRays() {
        for (int x = 0; x < Const.WIDTH; x++) {
            for (int y = 0; y < Const.HEIGHT; y++) {
                float u = (x + 0.5f) / Const.WIDTH * 2 - 1;
                float v = 1 - (y + 0.5f) / Const.HEIGHT * 2;
                float[] rayDir = Vector3.normalize(Vector3.add(hero.getNormal(), hero.getOffset(u, v)));
                float[] heroPos = hero.getPos();
                int i = (y * Const.WIDTH + x) * 6;
                rays[i] = heroPos[0];
                rays[i + 1] = heroPos[1];
                rays[i + 2] = heroPos[2];
                rays[i + 3] = rayDir[0];
                rays[i + 4] = rayDir[1];
                rays[i + 5] = rayDir[2];
            }
        }
    }

//    private void render() {
//        int wc = w / 2;
//        int hc = h / 2;
//        int col;
//        GreenfootImage frame = new GreenfootImage(w, h);
//        for (int x = 0; x < w; x++) {
//            for (int y = 0; y < h; y++) {
//                if (hero.getGun().isCrosshair(x, y, wc, hc)) {
//                    col = ColorOperation.red;
//                } else {
//
//                    col = trace(ray);
//                }
//                frame.setColorAt(x, h - y - 1, ColorOperation.IntToGColor(col));
//            }
//        }
//        frame.scale(ww, wh);
//        setBackground(frame);
//    }

//    private Intersection testRay(Ray ray) {
//        Intersection inter = new Intersection(new float[]{0, 0, 0}, -1, new float[]{0, 0, 0}, 0);
//        for (WorldObject o : objects) {
//            if (!(o instanceof Plane) && !hero.needRenderObject(o.getPos())) continue;
//            Intersection inter2 = o.getIntersection(ray);
//            float d1 = inter.getDistance();
//            float d2 = inter2.getDistance();
//            if (d2 > 0 && (d1 < 0 || d1 > d2)) {
//                inter = inter2;
//            }
//        }
//        return inter;
//    }
//
//    private int trace(Ray ray) {
//        Intersection inter = testRay(ray);
//        if (inter.getDistance() < 0) {
//            return (int) (inter.color * Const.AMBIENT);
//        }
//        int res = 0;
//
//        for (Light light : lights) {
//            if (light.needLight(hero)) {
//                res = ColorOperation.addColor(res, light.useLight(inter, Const.AMBIENT));
//            }
//        }
//        return res;
//    }

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

