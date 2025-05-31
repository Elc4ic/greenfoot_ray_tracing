import greenfoot.Greenfoot;
import greenfoot.MouseInfo;

import java.io.IOException;

public class Hero extends Creature {

    Camera camera;
    int gunInUse;
    int gunInHand;
    private int prevMouseX = 0;
    private int prevMouseY = 0;
    private boolean firstMouseMove = true;

    public Hero(float[] pos, float scale, float[] rotation, Camera camera, int textureIndex) throws IOException {
        super(pos, scale, rotation, "models\\block.obj", 0.5f, textureIndex);
        this.camera = camera;
        gunInUse = 0;
        gunInHand = 0;
    }

    void updateHero(WorldBase worldBase) {
        control();
        updateCreature(worldBase);
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

}
