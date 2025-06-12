import java.io.IOException;
import java.util.Random;

public class Enemy extends Creature {
    float attackRadius = 1;
    int damage = 4;
    Hero hero;
    Random r = new Random();

    public Enemy(float[] pos, float[] rot, float scale, Hero hero, String model, int textureIndex, int health) throws IOException {
        super(pos, rot, scale, model, textureIndex, health);
        this.hero = hero;
        setCollisionR(scale);
    }

    @Override
    public boolean update() {
        attackByRange();
        moveToHero();
        super.update();
        return getState() == STATE_DEAD;
    }

    void moveToHero() {
        float[] n = Vector3.scale(hero.getNormal(getPos()), -0.2f);
        addToPos(n);
    }

    void attackByRange() {
        if (hero.getDistance(getPos()) < attackRadius) {
            hero.changeHealth(-damage);
        }
    }

    void destroy(WorldBase worldBase) {
        try {
            if (r.nextInt(200) != 1) {
                Experience exp = new Experience(getPos(), 1);
                worldBase.addObject(exp);
            } else {
                AidKid aidKid = new AidKid(getPos());
                worldBase.addObject(aidKid);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        worldBase.deleteObject(this);
    }

}
