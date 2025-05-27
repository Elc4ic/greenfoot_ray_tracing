import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class Npc extends Creature {
    float agroRadius = 7;
    float attackRadius = 1;
    int damage = 20;
    Random r = new Random();
    Hero hero;

    public Npc(float[] pos, float scale, float[] normal, Hero hero, boolean hasTexture, int textureIndex) throws IOException {
        super(pos, scale, normal, "D:\\C_project\\Raytracer\\models\\block.obj", ColorOperation.green, 0.4f, hasTexture, textureIndex);
        this.hero = hero;
    }

    boolean updateNpc(Stack<WorldObject> ooa, WorldBase world) {
        update(world);
//        attackByRange(hero, ooa);
        return state == 0;
    }

//    void attackByRange(Creature o, Stack<WorldObject> ooa) {
//        float[] n = Vector3.subtract(hero.getPos(), getPos());
//        if (Vector3.length(n) < agroRadius && Math.random() < 0.05) {
//            normal = Vector3.normalize(n);
//            float[] bullet_Dir = Vector3.scale(normal, 1);
//            Vector3.rotateX(bullet_Dir, (r.nextFloat() - 0.5f) * 14);
//            Vector3.rotateY(bullet_Dir, (r.nextFloat() - 0.5f) * 14);
//            Vector3.rotateZ(bullet_Dir, (r.nextFloat() - 0.5f) * 14);
//            ooa.add(new Bullet(
//                    Vector3.add(getPos(), bullet_Dir),
//                    0.08f, ColorOperation.yellow,
//                    bullet_Dir, 0.5f, damage, 20, 1,)
//            );
////                o.setHealth(-damage);
//        } else {
//            randomMove();
//        }
//    }

    void randomMove() {
        rotateXn((r.nextFloat() - 0.5f) * 30);
        speedXZ = r.nextFloat() / 4;
    }
}
