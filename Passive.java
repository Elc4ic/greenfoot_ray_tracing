import greenfoot.*;

public class Passive extends Actor
{
    private String name;
    private Runnable effect;

    public Passive(String name, Runnable effect) {
        this.name = name;
        this.effect = effect;

        GreenfootImage image = new GreenfootImage(100, 100);
        image.setColor(Color.BLUE);
        image.fill();
        image.setColor(Color.WHITE);
        image.drawString(name, 10, 20);
        setImage(image);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            effect.run();
            getWorld().removeObject(this);
        }
    }

    public Runnable getEffect() {
        return effect;
    }
}
