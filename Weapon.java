import greenfoot.GreenfootImage;

import java.io.IOException;
import java.util.Random;

public abstract class Weapon {
    private GreenfootImage icon;
    private int lvl = 0;
    private final int maxLvl = 8;
    private long lastFireTime = 0;
    private String projectileModel;
    private float scale = 1;
    private Hero hero;
    private Random r = new Random();
    private String name;

    public Weapon(Hero hero, String icon, String model, String name) {
        this.icon = new GreenfootImage(icon);
        this.hero = hero;
        this.projectileModel = model;
        this.name = name;
    }

    public abstract void fire();

    public abstract void upgrade();

    public int getLvl() {
        return lvl;
    }

    public void lvlUp() {
        this.lvl++;
    }

    public GreenfootImage getIcon() {
        return icon;
    }

    public void setIcon(GreenfootImage icon) {
        this.icon = icon;
    }

    public int getMaxLvl() {
        return maxLvl;
    }

    public long getLastFireTime() {
        return lastFireTime;
    }

    public void setLastFireTime(long lastFireTime) {
        this.lastFireTime = lastFireTime;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public String getProjectileModel() {
        return projectileModel;
    }

    public Random getR() {
        return r;
    }

    public String getName() { return name; }

    protected float getDMG(float base){
        return base * hero.getDmgMult();
    }
    protected float getAS(float base){
        return base * hero.getAsMult();
    }
    protected int getProj(int base){
        return base + hero.getBonusProj();
    }
}
