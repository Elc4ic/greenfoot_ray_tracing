import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.Random;

/// надо нагенерить ассеты меню и плитки для него или как там это делается
public class ChoiseMenu extends Actor {
    private GreenfootImage menu = new GreenfootImage("choiseMenu.png");
    private Random rand = new Random();
    private ChoisePlate choisPlate1;
    private ChoisePlate choisPlate2;
    private ChoisePlate choisPlate3;
    private boolean showed = false;

    public ChoiseMenu(ChoisePlate choisPlate1, ChoisePlate choisPlate2, ChoisePlate choisPlate3) {
        setImage(Const.NOTHING);
        this.choisPlate1 = choisPlate1;
        this.choisPlate2 = choisPlate2;
        this.choisPlate3 = choisPlate3;
    }

    public void act() {

    }

    public void showMenu(Weapon[] weapons) {
        if (showed) return;
        setImage(menu);
        int cp1 = rand.nextInt(weapons.length);
        int cp2 = rand.nextInt(weapons.length);
        int cp3 = rand.nextInt(weapons.length);
        choisPlate1.setPlate(weapons[cp1]);
        choisPlate2.setPlate(weapons[cp2]);
        choisPlate3.setPlate(weapons[cp3]);
        showed = true;
    }

    public boolean isWeaponSelected() {
        return choisPlate1.isSelected() || choisPlate2.isSelected() || choisPlate3.isSelected();
    }

    public void hide() {
        choisPlate1.hide();
        choisPlate2.hide();
        choisPlate3.hide();
        setImage(Const.NOTHING);
        showed = false;
    }

}
