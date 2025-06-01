import java.io.IOException;

///интернет кабель RJ45 - стреляет в ближайшего врага пакетами данных
public class RJ45 extends Weapon {

    public RJ45(Hero hero) throws IOException {
        super(
                hero.getPos(), new float[]{0, 0, 0}, 1f,
                "models\\rj45.obj",
                "images\\rj45.png",
                TextureCollection.getInstance().getIndex("rj45"),
                hero
        );
    }

    @Override
    public void fire(WorldBase worldBase) {

    }

    @Override
    public void upgrade() {

    }
}
