import greenfoot.Color;

import java.io.IOException;

/**
 * Write a description of class Hell here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class Hell extends WorldBase {

    public Hell() {
        try {
            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    4f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\plate.obj",
                    0, true,
                    "D:\\C_project\\Raytracer\\images\\portal.png")
            );

            getObjects().add(new ObjFile(
                    new float[]{5, 0, 6},
                    0.4f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\flag.obj",
                    0, true,
                    "D:\\C_project\\Raytracer\\images\\flag.png")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
