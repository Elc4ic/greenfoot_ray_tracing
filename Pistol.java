import greenfoot.*;

import java.util.ArrayList;

public class Pistol extends Gun {
    int offsetX = 40;
    int offsetY = 90;
    private int ammo = 6;
    private final int ammoMax = 6;
    GreenfootImage[] animation = new GreenfootImage[8];
    // 0 = idle, 1 = fire, 2 = reload
    private int state = 3;
    private int step = 0;
    private Hero hero;

    private final int[][] crosshair = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 1, 1, 0, 0, 1},
            {1, 0, 0, 1, 1, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},
    };

    public Pistol(Hero hero, int offsetX, int offsetY,double scale) {
        super(offsetX, offsetY);
        this.hero = hero;
        GreenfootImage img;
        step = 0;
        for (int i = 1; i < 6; i++) {
            img = new GreenfootImage("pistol" + i + ".png");
            img.scale((int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
            animation[step] = img;
            step++;
        }
        for (int i = 4; i > 0; i--) {
            if (i == 3) continue;
            img = new GreenfootImage("pistol" + i + ".png");
            img.scale((int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
            animation[step] = img;
            step++;
        }
        step = 0;
    }

    @Override
    public void fire() {
        setImage(animation[step / 2]);
        step += 1;
        if (step >= animation.length * 2) {
            step = 0;
            state = 0;
        }
    }

    @Override
    public void reload() {
        if (ammo >= ammoMax) {
            state = 0;
            return;
        }
        setImage(animation[4]);
        step += 1;
        if (step >= 15) {
            step = 0;
            ammo++;
        }
    }

    @Override
    public void act() {
        if (state == 3) {
            setImage(nothing);
            setLocation(1000, 1000);
            return;
        }
        setLocation(x, y);
        if (Greenfoot.isKeyDown("r") && state != 1){
            step = 0;
            state = 2;
        }
        if (Greenfoot.isKeyDown("space") && state != 1 && ammo > 0) {
            step = 0;
//            objects.add(new Bullet(
//                    Vector3.add(hero.getPos(),hero.getNormal()),
//                    0.04f, ColorOperation.yellow,
//                    Vector3.scale(hero.getNormal(),1),
//                    1, 40, 50,1)
//            );
            ammo -= 1;
            state = 1;
        }
        if (state == 1) {
            fire();
        } else if (state == 2) {
            reload();
        } else {
            setImage(animation[0]);
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
