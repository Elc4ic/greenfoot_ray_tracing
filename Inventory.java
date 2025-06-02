import greenfoot.*;

import java.util.List;

/// тут типо храняться все вещи, в Images есть иконки примерных оружий
/// я хочу такую типо прогерскую стилистику (типо кофе хилки, wifi - радик)
public class Inventory extends Actor {
    GreenfootImage img = new GreenfootImage("inventory.png");
    Hero hero;

    public Inventory() {
        setImage(Const.NOTHING);
    }

    public void act() {

    }

    public void update(){
        List<Weapon> weapons = hero.getWeapons();
        int x = weapons.size() * Const.ICON_SIZE;
        img = new GreenfootImage(x, Const.ICON_SIZE);
        for (int i = 0; i < weapons.size(); i++) {
            img.drawImage(weapons.get(i).getIcon(), i * Const.ICON_SIZE, 0);
        }
    }
}
