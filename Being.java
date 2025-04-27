import greenfoot.Color;

import java.io.IOException;

public class Being extends WorldBase {
    public Being() {
//        super(hero);
        try {
            Texture wallTexture = new Texture("D:\\C_project\\Raytracer\\images\\wall.png");
            Texture badanTexture = new Texture("D:\\C_project\\Raytracer\\images\\badan.png");
            textureCollection.addTexture(wallTexture);
            textureCollection.addTexture(badanTexture);

            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    4f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\plate.obj",
                    true,
                    textureCollection.getIndex(wallTexture)
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
