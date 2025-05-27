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
    private Map<String, Integer> nameToIndexMap;

    private TextureCollection() {
        textures = new ArrayList<>();
        nameToIndexMap = new HashMap<>();
    }

    public static synchronized TextureCollection getInstance() {
        if (instance == null) {
            instance = new TextureCollection();
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
}
