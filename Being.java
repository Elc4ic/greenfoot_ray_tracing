import greenfoot.Color;

import java.io.IOException;

public class Being extends WorldBase {

    public Being(Hero hero) throws IOException {
        super(hero);
        try {
            getObjects().add(new ObjFile(
                    new float[]{0, 0, 0},
                    50f,
                    "models\\plane.obj",
                    textureCollection.getIndex("map")
            ));
            getObjects().add(new ObjFile(
                    new float[]{0, 0, 0},
                    3f,
                    "models\\enemy.obj",
                    textureCollection.getIndex("orb")
            ));
//            getObjects().add(new ObjFile(
//                    new float[]{5, 0, 5},
//                    0.016f,
//                    "models\\albedo.obj",
//                    textureCollection.getIndex("badan")
//            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
