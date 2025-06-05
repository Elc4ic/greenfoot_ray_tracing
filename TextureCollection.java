import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Texture {
    String key;
    int[] textureBuff;
    int textureWidth, textureHeight;

    Texture(String texturePath, String key) throws IOException {
        this.key = key;
        BufferedImage texture = ImageIO.read(new File(texturePath));
        this.textureWidth = texture.getWidth();
        this.textureHeight = texture.getHeight();
        this.textureBuff = new int[textureWidth * textureHeight];
        for (int i = 0; i < textureHeight; i++) {
            for (int j = 0; j < textureWidth; j++) {
                textureBuff[i * textureWidth + j] = texture.getRGB(j, i);
            }
        }
    }
}

public class TextureCollection {
    private static TextureCollection instance;

    List<Texture> textures;
    int[] textureBuff;
    private Map<String, Integer> nameToIndexMap;

    public static final int INFO_SIZE = 3;
    public static final int TXT_OFFSET = 0;
    public static final int TXT_W = 1;
    public static final int TXT_H = 2;

    private TextureCollection() throws IOException {
        textures = new ArrayList<>();
        nameToIndexMap = new HashMap<>();

        Texture mapTexture = new Texture("images\\map.png", "map");
        Texture bTexture = new Texture("images\\badan.png", "albedo");
        Texture orbTexture = new Texture("images\\orb.png", "orb");
        Texture bulletTexture = new Texture("images\\bullet.png", "bullet");
        Texture enemyTexture = new Texture("images\\enemy.png", "enemy");
        Texture wallTexture = new Texture("images\\wall.png", "wall");
        Texture portalTexture = new Texture("images\\portal.png", "portal");
        Texture wtTexture = new Texture("images\\wifi_texture.png", "wifi_texture");
        Texture dTexture = new Texture("images\\disk_texture.png", "disk_texture");
        Texture expTexture = new Texture("images\\explode_texture.png", "explode");

        addTexture(mapTexture);
        addTexture(bTexture);
        addTexture(orbTexture);
        addTexture(bulletTexture);
        addTexture(enemyTexture);
        addTexture(wallTexture);
        addTexture(portalTexture);
        addTexture(wtTexture);
        addTexture(dTexture);
        addTexture(expTexture);
    }

    public static synchronized TextureCollection getInstance() {
        if (instance == null) {
            try {
                instance = new TextureCollection();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return instance;
    }

    int addTexture(Texture texture) {
        textures.add(texture);
        int index = textures.size() - 1;
        if (texture.key != null) {
            nameToIndexMap.put(texture.key, index);
        }
        return index;
    }

    int getIndex(String key) {
        return nameToIndexMap.get(key);
    }

    public Texture getTexture(int index) {
        return textures.get(index);
    }

    public Texture getTexture(String key) {
        return textures.get(getIndex(key));
    }

    int getOffset(int index) {
        int offset = 0;
        for (int i = 0; i < index; i++) {
            offset += textures.get(i).textureBuff.length;
        }
        return offset;
    }

    int getAllTexturesSize() {
        return getOffset(textures.size());
    }

    int[] getTextureSizes(List<Triangle> triangles) {
        int[] textureSizes = new int[triangles.size() * INFO_SIZE];
        for (int i = 0; i < triangles.size(); i++) {
            Texture texture = getTexture(triangles.get(i).textureIndex);
            textureSizes[i * INFO_SIZE + TXT_OFFSET] = getOffset(triangles.get(i).textureIndex);
            textureSizes[i * INFO_SIZE + TXT_W] = texture.textureWidth;
            textureSizes[i * INFO_SIZE + TXT_H] = texture.textureHeight;
        }
        return textureSizes;
    }

    int[] initTextureBuff() {
        textureBuff = new int[getAllTexturesSize()];
        for (int i = 0; i < textures.size(); i++) {
            int[] text = getTexture(i).textureBuff;
            System.arraycopy(text, 0, textureBuff, getOffset(i), text.length);
        }
        return textureBuff;
    }

    public int[] getTextureBuff() {
        return textureBuff;
    }
}
