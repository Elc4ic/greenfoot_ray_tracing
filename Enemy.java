import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class Enemy extends Creature {
    float attackRadius = 1;
    int damage = 20;
    Hero hero;

    public Enemy(float[] pos, float[] rot, float scale, Hero hero, int textureIndex) throws IOException {
        super(pos, rot, scale, "models\\block.obj", textureIndex);
        this.hero = hero;
    }

    @Override
    public void update(WorldBase world) {
        super.update(world);
        attackByRange();
        moveToHero();
    }

    void moveToHero(){
        float[] n = hero.getNormal(getPos());
        addToPos(n);
    }

    void attackByRange() {
        if (hero.getDistance(getPos()) < attackRadius) {
            hero.applyDamage(damage);
        }
    }

}
