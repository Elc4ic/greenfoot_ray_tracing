import java.io.IOException;
import java.util.Random;
import java.util.Stack;

public class Enemy extends Creature {
    float attackRadius = 1;
    int damage = 4;
    Hero hero;

    public Enemy(float[] pos, float[] rot, float scale, Hero hero, int textureIndex) throws IOException {
        super(pos, rot, scale, "models\\block.obj", textureIndex);
        this.hero = hero;
    }

    @Override
    public boolean update() {
        attackByRange();
        moveToHero();
        super.update();
        return false;
    }

    void moveToHero(){
        float[] n = Vector3.scale(hero.getNormal(getPos()), -0.2f);
        addToPos(n);
    }

    void attackByRange() {
        if (hero.getDistance(getPos()) < attackRadius) {
            hero.applyDamage(-damage);
        }
    }

}
