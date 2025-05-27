import greenfoot.Color;

import java.io.IOException;


public class Hell extends WorldBase {

    public Hell(Hero hero) throws IOException {
        super(hero);
        try {
            Texture portalTexture = new Texture("D:\\C_project\\Raytracer\\images\\portal.png");
            Texture flagTexture = new Texture("D:\\C_project\\Raytracer\\images\\flag.png");
            textureCollection.addTexture(portalTexture);
            textureCollection.addTexture(flagTexture);

            getObjects().add(new ObjFile(
                    new float[]{5, 0, 6},
                    0.4f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\flag.obj",
                    true,
                    textureCollection.getIndex(flagTexture)
            ));

            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    10f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\plate.obj",
                    true,
                    textureCollection.getIndex(portalTexture)
            ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
