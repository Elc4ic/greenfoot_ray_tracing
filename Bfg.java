import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Stack;

public class Bfg extends Gun {
    private final int ammoMax = 12;
    private int ammo = ammoMax;
    GreenfootImage[] fireAnimation = new GreenfootImage[8];
    // 0 = idle, 1 = fire, 2 = reload, 3 = hide
    private int state = 3;
    private int step = 0;
    private float scaleImage = 1;
    private Hero hero;

    private final int[][] crosshair = {
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 1, 0, 0, 1, 0, 0},
            {0, 1, 0, 0, 0, 0, 1, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
    };

    public Bfg(Hero hero, int offsetX, int offsetY, float scale) {
        super(offsetX, offsetY);
        this.hero = hero;

        fireAnimation[0] = new GreenfootImage("bfg1.png");
        fireAnimation[1] = new GreenfootImage("splash.png");
        fireAnimation[2] = new GreenfootImage("splash2.png");
        fireAnimation[3] = new GreenfootImage("bfg3.png");
        fireAnimation[4] = new GreenfootImage("bfg2.png");
        fireAnimation[5] = new GreenfootImage("bfg2.png");
        fireAnimation[6] = new GreenfootImage("bfg1.png");
        fireAnimation[7] = new GreenfootImage("bfg1.png");

        for (GreenfootImage img : fireAnimation) {
            img.scale((int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
        }

    }

    @Override
    public void fire() {
        setImage(fireAnimation[step]);
        step += 1;
        if (step >= fireAnimation.length) {
            step = 0;
            state = 0;
        }
    }

    @Override
    public void reload() {
        step = 0;
        state = 0;
    }

    @Override
    public void act() {
        if (state == 3) {
            setImage(nothing);
            setLocation(1000, 1000);
            return;
        }
        setLocation(x, y);
        if (Greenfoot.isKeyDown("r") && state != 1) {
            step = 0;
            state = 2;
        }
        if (Greenfoot.isKeyDown("space") && state != 1 && ammo > 0) {
            step = 0;
            float[] n = Vector3.scale(hero.getDirection(), 1);
//            objects.add(new Bullet(
//                    Vector3.add(hero.getPos(), hero.getNormal()),
//                    0.5f, ColorOperation.green,
//                    n, 0.4f, 100, 5, 1, true)
//            );
            ammo -= 1;
            state = 1;
        }
        if (state == 1) {
            fire();
        } else if (state == 2) {
            reload();
        } else {
            setImage(fireAnimation[0]);
        }
    }

    @Override
    public void show(boolean active) {
        if (active) state = 0;
        else state = 3;
    }

    @Override
    public int getAmmo() {
        return ammo;
    }

    @Override
    public int getAmmoMax() {
        return ammoMax;
    }

    @Override
    public boolean isCrosshair(int x, int y, int wc, int hc) {
        int crossX = x > wc ? x - wc : wc - x - 1;
        int crossY = y > hc ? y - hc : hc - y - 1;
        return crossX < crosshair.length / 2 &&
                crossY < crosshair.length / 2 &&
                crosshair[x - wc + crosshair.length / 2][y - hc + crosshair.length / 2] == 1;
    }
}
