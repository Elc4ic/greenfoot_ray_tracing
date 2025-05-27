import greenfoot.Color;

import java.io.IOException;

public class Being extends WorldBase {

    public Being(Hero hero) throws IOException {
        super(hero);
        try {
            getObjects().add(new ObjFile(
                    new float[]{3, 0, 6},
                    10f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "models\\plate.obj",
                    true,
                    textureCollection.getIndex("map")
            ));
            getObjects().add(new ObjFile(
                    new float[]{5, 0, 6},
                    0.016f,
                    ColorOperation.GColorToInt(new Color(130, 130, 130)),
                    "models\\albedo.obj",
                    true,
                    textureCollection.getIndex("badan")
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
