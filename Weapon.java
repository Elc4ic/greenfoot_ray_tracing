import greenfoot.GreenfootImage;

import java.io.IOException;

public abstract class Weapon extends ObjFile {
    private GreenfootImage icon;
    private int lvl = 0;
    private final int maxLvl = 8;
    private long lastFireTime = 0;
    private Hero hero;

    public Weapon(float[] pos, float[] rot, float scale, String objFile, String icon, int textureIndex,Hero hero) throws IOException {
        super(pos, rot, scale, objFile, textureIndex);
        this.icon = new GreenfootImage(icon);
        this.hero = hero;
    }

    public abstract void fire(WorldBase worldBase);

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
}
