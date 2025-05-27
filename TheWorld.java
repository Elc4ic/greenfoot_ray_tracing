
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
    private final Hero hero = new Hero(new float[]{3, 1, 3}, 0.5f, new float[]{1, 0, 1}, camera, false, 1);
    TextureCollection textures = TextureCollection.getInstance();
    private WorldBase worldBase;

    private final FPSCounter fPSCounter = new FPSCounter();
    GreenfootImage frame = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
    Kernel kernel;


    private float[] positions;
    private float[] rotations;
    private int[] sizes;
    private float[] bounds;
    private float[] cache;
    private int[] leafInsides;
    private float[] vertices;
    private float[] texCoords;
    private int[] texture;
    private float[] projected;

    private final float[] depth_buffer = new float[Const.PICXELS];
    private final float[] rays = new float[Const.HEIGHT * Const.WIDTH * Ray.RAY_SIZE];
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
        initStaticDataFromObjects(worldBase.getObjects());
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

            if (Greenfoot.isKeyDown("j")) {
                camera.setRotations(0, 0.1f, 0);
            }
            if (Greenfoot.isKeyDown("l")) {
                camera.setRotations(0, -0.1f, 0);
            }
            if (Greenfoot.isKeyDown("i")) {
                camera.setRotations(0.1f, 0, 0);
            }
            if (Greenfoot.isKeyDown("k")) {
                camera.setRotations(-0.1f, 0, 0);
            }

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
            camera.setPos(hero.getPos());
        }
        fPSCounter.update();
    }

    private void initStaticDataFromObjects(ArrayList<WorldObject> objects) {
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
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.FACE_COUNT_OFFSET] = obj.objectBuffer.sizes[ObjFile.FACE_COUNT_OFFSET];
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_OFFSET] = sizeNode[ObjFile.TEXTURE_OFFSET];
                sizeNode[ObjFile.VERTEX_OFFSET] += obj.objectBuffer.verticesBuff.length;
                sizeNode[ObjFile.TEXT_COORD_OFFSET] += obj.objectBuffer.texCoordsBuff.length;
                sizeNode[ObjFile.LEAFS_OFFSET] += obj.objectBuffer.leafInsidesBuff.length;

                Texture texture = worldBase.textureCollection.getTexture(obj.textureIndex);
                int offset = worldBase.textureCollection.getOffset(obj.textureIndex);
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_WIDTH] = texture.textureWidth;
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_HEIGHT] = texture.textureHeight;
                sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXTURE_OFFSET] = offset;
                nOfObj++;
            }
        }
        rotations = new float[nOfObj * ObjFile.ROTATION_SIZE];
        positions = new float[nOfObj * ObjFile.POS_SIZE];
        bounds = new float[nOfObj * ObjFile.BOUND_SIZE];
        cache = new float[sizeNode[ObjFile.LEAFS_OFFSET] / 6 * 13];
        leafInsides = new int[sizeNode[ObjFile.LEAFS_OFFSET]];
        vertices = new float[sizeNode[ObjFile.VERTEX_OFFSET] * 3];
        projected = new float[sizeNode[ObjFile.VERTEX_OFFSET] * 3];
        texCoords = new float[sizeNode[ObjFile.TEXT_COORD_OFFSET] * 2];
        texture = new int[worldBase.textureCollection.getAllTexturesSize()];
        //fill arrays
        nOfObj = 0;
        for (WorldObject object : objects) {
            if (object instanceof ObjFile obj) {
                float[] bvhb = obj.objectBuffer.boundsBuff;
                float[] cacheBuff = obj.objectBuffer.cacheBuff;
                int[] leaf = obj.objectBuffer.leafInsidesBuff;
                float[] vertex = obj.objectBuffer.verticesBuff;
                float[] t_coord = obj.objectBuffer.texCoordsBuff;

                System.arraycopy(vertex, 0, vertices, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.VERTEX_OFFSET] * 3, vertex.length);
                System.arraycopy(t_coord, 0, texCoords, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.TEXT_COORD_OFFSET] * 2, t_coord.length);
                System.arraycopy(cacheBuff, 0, cache, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET] / 6 * 13, cacheBuff.length);
                System.arraycopy(leaf, 0, leafInsides, sizes[nOfObj * ObjFile.SIZES_SIZE + ObjFile.LEAFS_OFFSET], leaf.length);
                System.arraycopy(bvhb, 0, bounds, nOfObj * ObjFile.BOUND_SIZE, bvhb.length);

                nOfObj++;
            }
        }
        List<Texture> textures = worldBase.textureCollection.textures;
        for (int i = 0; i < textures.size(); i++) {
            int[] text = worldBase.textureCollection.getTexture(i).textureBuff;
            System.arraycopy(text, 0, texture, worldBase.textureCollection.getOffset(i), text.length);
        }
    }

    private void render() {
        initRotation(worldBase.getObjects());
        initPosition(worldBase.getObjects());
        projectVertices();

        IntStream.range(0, Const.PICXELS).forEach(i -> depth_buffer[i] = Float.MAX_VALUE);
        IntStream.range(0, Const.PICXELS).forEach(i -> output[i] = 0xffffff);

        int objectsCount = sizes.length / ObjFile.SIZES_SIZE;
//        int triangleCount = IntStream.range(0, objectsCount).map(i -> sizes[i * ObjFile.SIZES_SIZE + ObjFile.FACE_COUNT_OFFSET]).sum();

        Range range = Range.create(objectsCount);

        kernel = new RasterizerKernel(
                sizes, vertices, texCoords, leafInsides, projected, texture, output, depth_buffer, Const.WIDTH, Const.HEIGHT
        );
        kernel.setExecutionModeWithoutFallback(Kernel.EXECUTION_MODE.JTP);
        kernel.setExplicit(true);

        kernel.put(sizes);
        kernel.put(vertices);
        kernel.put(texCoords);
        kernel.put(leafInsides);
        kernel.put(projected);
        kernel.put(texture);
        kernel.put(depth_buffer);

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

    private void projectVertices() {
        float sinX = (float) Math.sin(camera.getRotations()[0]);
        float cosX = (float) Math.cos(camera.getRotations()[0]);

        float sinY = (float) Math.sin(camera.getRotations()[1]);
        float cosY = (float) Math.cos(camera.getRotations()[1]);

        for (int i = 0; i < vertices.length / ObjFile.VERTEX_SIZE; i++) {
            float vx = vertices[i * ObjFile.VERTEX_SIZE + ObjFile.VERTEX_X];
            float vy = vertices[i * ObjFile.VERTEX_SIZE + ObjFile.VERTEX_Y];
            float vz = vertices[i * ObjFile.VERTEX_SIZE + ObjFile.VERTEX_Z];

            float dx = vx - camera.getPos()[0];
            float dy = vy - camera.getPos()[1];
            float dz = vz - camera.getPos()[2];

            float x = dx * cosY - dz * sinY;
            float xzZ = dx * sinY + dz * cosY;

            float y = dy * cosX - xzZ * sinX;
            float z = dy * sinX + xzZ * cosX;

            if (z <= Const.EPSILON) z = Const.EPSILON;

            float px = (x / z) * camera.fov * camera.aspect;
            float py = (y / z) * camera.fov;

            int screenX = (int) ((px + 1) * Const.WIDTH * 0.5f);
            int screenY = (int) ((1 - py) * Const.HEIGHT * 0.5f);
            screenX = Math.max(0, Math.min(Const.WIDTH - 1, screenX));
            screenY = Math.max(0, Math.min(Const.HEIGHT - 1, screenY));

            projected[i * ObjFile.VERTEX_SIZE + ObjFile.VERTEX_X] = screenX;
            projected[i * ObjFile.VERTEX_SIZE + ObjFile.VERTEX_Y] = screenY;
            projected[i * ObjFile.VERTEX_SIZE + ObjFile.VERTEX_Z] = z;
        }
    }



    //            float rotX = rotations[i * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_X];
//            float rotY = rotations[i * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_Y];
//            float rotZ = rotations[i * ObjFile.ROTATION_SIZE + ObjFile.ROTATION_Z];
//
//            float posX = positions[i * ObjFile.POS_SIZE + ObjFile.POS_X];
//            float posY = positions[i * ObjFile.POS_SIZE + ObjFile.POS_Y];
//            float posZ = positions[i * ObjFile.POS_SIZE + ObjFile.POS_Z];
//
//            vx += posX;
//            vy += posY;
//            vz += posZ;

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

