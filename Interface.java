import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

public class Interface extends Actor {
    Hero hero;
    FPSCounter fpsCounter;
    GreenfootImage img;

    public Interface(Hero hero, FPSCounter fpsCounter) {
        this.hero = hero;
        this.fpsCounter = fpsCounter;
        img = new GreenfootImage("Bullets: " + hero.getGun().getAmmo() + "/" + hero.getGun().getAmmoMax()
                + "\nHealth: " + hero.getHealth() + "/" + hero.getHealthMax()
                + "\nFPS: " + fpsCounter.getFPS()
                + "\nFTM: " + fpsCounter.getFrameTimeMillis(),
                18, Color.RED, Color.BLACK);
    }

    public void update() {
        img = new GreenfootImage(
                "Bullets: " + hero.getGun().getAmmo() + "/" + hero.getGun().getAmmoMax()
                        + "\nHealth: " + hero.getHealth() + "/" + hero.getHealthMax()
                        + "\nFPS: " + fpsCounter.getFPS()
                        + "\nFTM: " + fpsCounter.getFrameTimeMillis(),
                18, Color.RED, Color.BLACK);
        setImage(img);
    }

    public GreenfootImage getImg() {
        return img;
    }
}
