import greenfoot.Color;

import java.io.IOException;

public class Being extends WorldBase {
    public Being(Hero hero) throws IOException {
        super(hero);
        try {
            Texture mapTexture = new Texture("D:\\C_project\\Raytracer\\images\\map.png");
            Texture badanTexture = new Texture("D:\\C_project\\Raytracer\\images\\badan.png");
            textureCollection.addTexture(mapTexture);
            textureCollection.addTexture(badanTexture);

            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    10f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\map.obj",
                    true,
                    textureCollection.getIndex(mapTexture)
            ));
            getObjects().add(new ObjFile(
                    new float[]{5, 0, 6},
                    0.016f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\albedo.obj",
                    true,
                    textureCollection.getIndex(badanTexture)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
