import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.Random;

public abstract class Gun extends Actor {
    int x;
    int y;
    GreenfootImage nothing = new GreenfootImage(1, 1);
    Random r = new Random();

    Gun(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public abstract void fire();

    public abstract void reload();

    public abstract void act();

    public abstract void show(boolean active);

    public abstract int getAmmo();

    public abstract int getAmmoMax();

    public abstract boolean isCrosshair(int x, int y, int w, int h);
}
