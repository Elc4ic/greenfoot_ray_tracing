import greenfoot.*;

public class ChoisePlate extends Actor {
    private final GreenfootImage plate = new GreenfootImage("choisePlate.png");
    private Weapon weapon;
    private boolean selected = false;
    private final Hero hero;

    public ChoisePlate(Hero hero) {
        setImage(Const.NOTHING);
        this.hero = hero;
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (hero.getWeapons().contains(weapon)) {
                weapon.upgrade();
            } else {
                hero.addWeapon(weapon);
            }
            selected = true;
        }
    }

    public void setPlate(Weapon weapon) {
        this.weapon = weapon;
        setImage(plate);
        getImage().drawImage(weapon.getIcon(), 16, 16);
    }

    public void hide() {
        setImage(Const.NOTHING);
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }
}
