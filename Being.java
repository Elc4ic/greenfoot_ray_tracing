import greenfoot.Color;

import java.io.IOException;

public class Being extends WorldBase 
{
    public Being(){
//        super(hero);
        try {
            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    4f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\plate.obj",
                    0, true,
                    "D:\\C_project\\Raytracer\\images\\wall.png")
            );
            getObjects().add(new ObjFile(
                    new float[]{5, 0, 6},
                    0.016f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "D:\\C_project\\Raytracer\\models\\albedo.obj",
                    8, true,
                    "D:\\C_project\\Raytracer\\images\\badan.png")
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    
}
