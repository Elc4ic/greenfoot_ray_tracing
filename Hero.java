import greenfoot.Greenfoot;
import greenfoot.MouseInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Hero extends Creature {
    private long exp = 0;
    private long nextLvlXp = 2;
    private int lvl = 0;
    private float dmgMult = 1.0f;
    private float asMult = 1.0f;
    private int bonusProj = 0;


    private List<Weapon> weapons = new ArrayList<>();

    private int prevMouseX = 0;
    private int prevMouseY = 0;

    public Hero(float[] pos, float[] rot, float scale, int textureIndex) throws IOException {
        super(pos, rot, scale, "models\\block.obj", textureIndex);
        setBulletCollisionEnabled(false);
    }

    void updateHero() {
        control();
        updateWeapons();
        update();

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

            rotateYn(deltaX);
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

    public long getExp() {
        return exp;
    }

    public long getNextLvlXp() {
        return nextLvlXp;
    }

    public long getLvl() {
        return lvl;
    }

    public float getDmgMult() {
        return dmgMult;
    }
    public void incDmg(float val){
        dmgMult += val;
    }

    public float getAsMult() {
        return asMult;
    }

    public void incAS(float val){
        asMult -= val;
    }

    public int getBonusProj() {
        return bonusProj;
    }
    public void incProj(int val){
        bonusProj += val;
    }

    public void addExp(long experience) {
        this.exp += experience;
        if (exp >= nextLvlXp) {
            setState(Creature.STATE_UPGRADE);
            lvl++;
            nextLvlXp = nextLvlXp * 2;
        }
    }
}
