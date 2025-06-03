import greenfoot.Greenfoot;
import greenfoot.MouseInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Hero extends Creature {
    private long exp = 0;
    private List<Weapon> weapons = new ArrayList<>();

    private int prevMouseX = 0;
    private int prevMouseY = 0;

    public Hero(float[] pos, float[] rot, float scale, int textureIndex) throws IOException {
        super(pos, rot, scale, "models\\block.obj", textureIndex);
        setBulletCollisionEnabled(false);
    }

    void updateHero() {
        control();
        update();
        updateWeapons();
    }

    private void updateWeapons() {
        for (Weapon weapon : weapons) {
            weapon.fire();
        }
    }

    void control() {
        if (Greenfoot.mouseMoved(null)) {
            MouseInfo mouse = Greenfoot.getMouseInfo();
            int x = mouse.getX();
            int y = mouse.getY();

            int deltaX = x - prevMouseX;
            int deltaY = y - prevMouseY;

            rotateXn(deltaX);
            //rotateYn(deltaY * Const.HEIGHT/Const.WIDTH);

            prevMouseX = x;
            prevMouseY = y;
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
    }

    public List<Weapon> getWeapons() {
        return weapons;
    }

    public void addWeapon(Weapon weapon) {
        weapons.add(weapon);
    }
}
