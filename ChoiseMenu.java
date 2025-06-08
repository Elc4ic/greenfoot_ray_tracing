import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

import java.util.*;

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

    public void showMenu(Weapon[] weapons, Hero hero) {
        if (showed) return;
        menu.scale(400,230);
        setImage(menu);

        List<Object> upgrades = new ArrayList<>();
        upgrades.addAll(Arrays.asList(weapons));

        upgrades.add(new Passive("Damage+", () -> { hero.incDmg(0.2f);}));
        upgrades.add(new Passive("AS+", () -> { hero.incAS(0.1f);}));
        upgrades.add(new Passive("Proj+", () -> { hero.incProj(1);}));

        Collections.shuffle(upgrades);
        choisPlate1.setPlate(upgrades.get(0));
        choisPlate2.setPlate(upgrades.get(1));
        choisPlate3.setPlate(upgrades.get(2));
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
