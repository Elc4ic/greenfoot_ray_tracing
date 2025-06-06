import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

public class Virus extends Weapon {
    private final GreenfootImage deathScreen = new GreenfootImage("images\\death_screen.png");
    private long fireInterval = 30 * Const.SECOND;

    public Virus(Hero hero) {
        super(hero, "images\\virus.png", "models\\virus.obj");
    }


    @Override
    public void fire() {
        if (System.nanoTime() - getLastFireTime() < fireInterval) return;

        WorldBase world = WorldBase.getInstance();
        world.setLoadScreen(deathScreen);
        world.needLoadScreen = true;

        for (WorldObject o : world.getObjects()) {
            if (o instanceof Enemy enemy) enemy.destroy(world);
        }

        setLastFireTime(System.nanoTime());
    }

    @Override
    public void upgrade() {
        lvlUp();
        fireInterval -= 2 * Const.SECOND;
    }
}
