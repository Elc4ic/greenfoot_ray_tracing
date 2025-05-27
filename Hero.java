import greenfoot.Greenfoot;
import greenfoot.MouseInfo;

import java.io.IOException;

public class Hero extends Creature {

    Camera camera;
    Gun[] arsenal;
    int gunInUse;
    int gunInHand;

    public Hero(float[] pos, float scale, float[] normal, Camera camera, boolean hasTexture, int textureIndex) throws IOException {
        super(pos, scale, normal, "models\\block.obj", ColorOperation.green, 0.5f, hasTexture, textureIndex);
        this.arsenal = new Gun[]{
                new Pistol(this, Const.WIDTH - 80, Const.HEIGHT - 170, Const.HEIGHT_SCALE),
                new Shotgun(this, Const.WIDTH / 2 - 10, Const.HEIGHT - 110, Const.HEIGHT_SCALE * 2),
                new Smg(this, Const.WIDTH / 2 - 40, Const.HEIGHT - 120, Const.HEIGHT_SCALE * 2),
                new Bfg(this, Const.WIDTH / 2, Const.HEIGHT - 90, Const.HEIGHT_SCALE * 2)
        };
        this.camera = camera;
        gunInUse = 0;
        arsenal[gunInUse].show(true);
        gunInHand = 0;
    }

    void updateHero(WorldBase worldBase) {
        control();
        updateGun();
        updateCreature(worldBase);
    }

    Gun getGun() {
        return arsenal[gunInUse];
    }

    void winCondition(WorldObject cube) {
        if (cube != null && cube.getCollision(getPos(), hitBoxRadius)) {
            state = -1;
        }
    }

    void updateGun() {
        if (gunInUse != gunInHand) {
            arsenal[gunInHand].show(false);
            arsenal[gunInUse].show(true);
            gunInHand = gunInUse;
        }
        arsenal[gunInHand].act();
    }

    void switchGun(int i) {
        gunInUse = i % arsenal.length;
    }

    void control() {
        if (Greenfoot.mouseMoved(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            int x = mouse.getX();
            int y = mouse.getY();
            int mx = Const.SCALED_WIDTH / 2 - x;
            int my = y - Const.SCALED_HEIGHT / 2;
            rotateXn(mx / 10f);
//                hero.rotateYn(my / 10.0);
        }
        if (Greenfoot.isKeyDown("a")) {
            moveRight();
        }
        if (Greenfoot.isKeyDown("d")) {
            moveLeft();
        }
        if (Greenfoot.isKeyDown("w")) {
            moveForward();
        }
        if (Greenfoot.isKeyDown("s")) {
            moveBackward();
        }
        if (Greenfoot.isKeyDown("z")) {
            jump();
        }
        if (Greenfoot.isKeyDown("1")) {
            switchGun(0);
        }
        if (Greenfoot.isKeyDown("2")) {
            switchGun(1);
        }
        if (Greenfoot.isKeyDown("3")) {
            switchGun(2);
        }
        if (Greenfoot.isKeyDown("4")) {
            switchGun(3);
        }
    }

}
