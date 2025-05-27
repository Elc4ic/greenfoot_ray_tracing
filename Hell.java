import greenfoot.Color;

import java.io.IOException;


public class Hell extends WorldBase {

    public Hell(Hero hero) throws IOException {
        super(hero);
        try {
            Texture portalTexture = new Texture("images\\portal.png");
            Texture flagTexture = new Texture("images\\flag.png");
            textureCollection.addTexture(portalTexture);
            textureCollection.addTexture(flagTexture);

            getObjects().add(new ObjFile(
                    new float[]{5, 0, 6},
                    0.4f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "models\\flag.obj",
                    true,
                    textureCollection.getIndex(flagTexture)
            ));

            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    10f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "models\\plate.obj",
                    true,
                    textureCollection.getIndex(portalTexture)
            ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
