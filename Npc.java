import greenfoot.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

public class Npc extends Creature {
    float agroRadius = 7;
    float attackRadius = 1;
    int damage = 20;
    Random r = new Random();
    Hero hero;

    public Npc(float[] pos, float[] normal, Hero hero) {
        super(pos, 1, 2.6f, 1, normal, ColorOperation.green, 0.5f);
        this.hero = hero;
    }

    boolean updateNpc(Stack<WorldObject> ooa,WorldBase world) {
        checkCollision(world, true);
        updateState();
        attackByRange(hero, ooa);
        updatePos();
        return state == 0;
    }

    void attackByRange(Creature o, Stack<WorldObject> ooa) {
        float[] n = Vector3.subtract(hero.getPos(), getPos());
        if (Vector3.length(n) < agroRadius && Math.random() < 0.05) {
            normal = Vector3.normalize(n);
            float[] bullet_Dir = Vector3.scale(normal, 1);
            Vector3.rotateX(bullet_Dir, (r.nextFloat() - 0.5f) * 14);
            Vector3.rotateY(bullet_Dir, (r.nextFloat() - 0.5f) * 14);
            Vector3.rotateZ(bullet_Dir, (r.nextFloat() - 0.5f) * 14);
            ooa.add(new Bullet(
                    Vector3.add(getPos(), bullet_Dir),
                    0.08f, ColorOperation.yellow,
                    bullet_Dir, 0.5f, damage, 20, 1)
            );
//                o.setHealth(-damage);
        } else {
            randomMove();
        }
    }

    void randomMove() {
        rotateXn((r.nextFloat() - 0.5f) * 30);
        moveSpeed = r.nextFloat() / 4;
    }
}
