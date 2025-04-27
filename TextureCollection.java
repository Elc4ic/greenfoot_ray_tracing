import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Texture {
    int[] textureBuff;
    int textureWidth, textureHeight;

    Texture(String texturePath) throws IOException {
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
    List<Texture> collection = new ArrayList<>();

    TextureCollection addTexture(Texture texture) {
        collection.add(texture);
        return this;
    }

    int getIndex(Texture texture) {
        return collection.indexOf(texture);
    }

    public int[] getTextureBuff(int index) {
        return collection.get(index).textureBuff;
    }

    public Texture getTexture(int index) {
        return collection.get(index);
    }


    int getOffset(int index) {
        int offset = 0;
        for (int i = 0; i < index; i++) {
            offset += collection.get(i).textureBuff.length;
        }
        return offset;
    }

    int getAllTexturesSize() {
        return getOffset(collection.size());
    }
}
