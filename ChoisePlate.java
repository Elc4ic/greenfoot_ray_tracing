import greenfoot.*;

public class ChoisePlate extends Actor {
    private GreenfootImage plate;
    private Weapon weapon;
    private boolean selected = false;
    private final Hero hero;
    private Object upgrade;

    public ChoisePlate(Hero hero) {
        setImage(Const.NOTHING);
        this.hero = hero;
    }

    public void setPlate(Object upgrade) {
        this.upgrade = upgrade;

        plate = new GreenfootImage("choisePlate.png");
        plate.scale(100,100);
        setImage(plate);

        if (upgrade instanceof Weapon) {
            Weapon weapon = (Weapon)upgrade;
            GreenfootImage weaponIcon = weapon.getIcon();
            weaponIcon.scale(50,50);
            getImage().drawImage(weaponIcon, 20, 20);
            getImage().setColor(Color.WHITE);
            String displayText = weapon.getName() + " (Lvl " + weapon.getLvl() + ")";
            getImage().drawString(displayText, 8, 90);
        } else if (upgrade instanceof Passive) {
            Passive pas = (Passive)upgrade;
            getImage().drawImage(pas.getImage(), 0, 0);
        }
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (upgrade instanceof Weapon) {
                Weapon weapon = (Weapon)upgrade;
                if (hero.getWeapons().contains(weapon)) {
                    weapon.upgrade();
                } else {
                    hero.addWeapon(weapon);
                }
            } else if (upgrade instanceof Passive) {
                Passive pas = (Passive)upgrade;
                pas.getEffect().run();
            }
            selected = true;
        }
    }

    public void hide() {
        setImage(Const.NOTHING);
        selected = false;
    }

    public boolean isSelected() {
        return selected;
    }
}
