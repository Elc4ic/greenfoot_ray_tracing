import greenfoot.Color;
import greenfoot.Greenfoot;
import greenfoot.GreenfootImage;

import java.util.ArrayList;

public class Shotgun extends Gun {
    private int ammo = 2;
    private final int ammoMax = 2;
    GreenfootImage[] fireAnimation = new GreenfootImage[4];
    GreenfootImage[] reloadAnimation = new GreenfootImage[15];
    // 0 = idle, 1 = fire, 2 = reload, 3 = hide
    private int state = 3;
    private int step = 0;
    private float scaleImage = 1;
    private Hero hero;

    private final int[][] crosshair = {
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {1, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},
    };

    public Shotgun( Hero hero, int offsetX, int offsetY, float scale) {
        super(offsetX, offsetY);
        this.hero = hero;

        fireAnimation[0] = new GreenfootImage("shotgun1.png");
        fireAnimation[1] = new GreenfootImage("shotgun2.png");
        fireAnimation[2] = new GreenfootImage("shotgun3.png");
        fireAnimation[3] = new GreenfootImage("shotgun1.png");

        reloadAnimation[0] = new GreenfootImage("shotgun1.png");
        reloadAnimation[1] = new GreenfootImage("shotgun_r1.png");
        reloadAnimation[2] = new GreenfootImage("shotgun_r2.png");
        reloadAnimation[3] = new GreenfootImage("shotgun_r3.png");
        reloadAnimation[4] = new GreenfootImage("shotgun_r4.png");
        reloadAnimation[5] = new GreenfootImage("shotgun_r5.png");
        reloadAnimation[6] = new GreenfootImage("shotgun_r6.png");
        reloadAnimation[7] = new GreenfootImage("shotgun_r2.png");
        reloadAnimation[8] = new GreenfootImage("shotgun_r1.png");
        reloadAnimation[9] = new GreenfootImage("shotgun6.png");
        reloadAnimation[10] = new GreenfootImage("shotgun5.png");
        reloadAnimation[11] = new GreenfootImage("shotgun4.png");
        reloadAnimation[12] = new GreenfootImage("shotgun5.png");
        reloadAnimation[13] = new GreenfootImage("shotgun6.png");
        reloadAnimation[14] = new GreenfootImage("shotgun1.png");

        for (GreenfootImage img : fireAnimation) {
            img.scale((int) (img.getWidth() * scale), (int) (img.getHeight() * scale));
        }

        for (GreenfootImage img : reloadAnimation) {
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
        if (ammo >= ammoMax) {
            state = 0;
            return;
        }
        setImage(reloadAnimation[step / 3]);
        step += 1;
        if (step >= reloadAnimation.length * 3) {
            step = 0;
            ammo = ammoMax;
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
        if (Greenfoot.isKeyDown("r") && state != 1) {
            step = 0;
            state = 2;
        }
        if (Greenfoot.isKeyDown("space") && state != 1 && ammo > 0) {
            step = 0;
            for (int i = 0; i < 20; i++) {
                float[] n = Vector3.scale(hero.getDirection(), 1);
                Vector3.rotateX(n, (r.nextFloat() - 0.5f) * 14);
                Vector3.rotateY(n, (r.nextFloat() - 0.5f) * 14);
                Vector3.rotateZ(n, (r.nextFloat() - 0.5f) * 14);
//                objects.add(new Bullet(
//                        Vector3.add(hero.getPos(), n),
//                        0.04f, ColorOperation.yellow,
//                        n, 1, 6, 15, 1)
//                );
            }
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
