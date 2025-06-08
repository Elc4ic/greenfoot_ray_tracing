import greenfoot.*;

public class Button extends Actor {
    private String text;
    private Runnable onClick;
    private Color normalColor = new Color(0, 100, 0);
    private Color hoverColor = new Color(0, 150, 0);
    private boolean isVisible = true;

    public Button(String text, int width, int height, Runnable onClick) {
        this.text = text;
        this.onClick = onClick;
        drawButton(width, height, normalColor);
    }

    private void drawButton(int width, int height, Color color) {
        GreenfootImage img = new GreenfootImage(width, height);
        if (isVisible) {
            img.setColor(color);
            img.fill();
            img.setColor(Color.WHITE);
            int textWidth = img.getFont().getSize() * text.length() / 2;
            img.drawString(text, width/2 - textWidth/2, height/2 + 8);
        } else {
            img.setTransparency(0);
        }
        setImage(img);
    }

    public void act() {
        if (!isVisible) return;

        if (Greenfoot.mouseMoved(this)) {
            drawButton(getImage().getWidth(), getImage().getHeight(), hoverColor);
        } else if (Greenfoot.mouseMoved(null) && !Greenfoot.mouseMoved(this)) {
            drawButton(getImage().getWidth(), getImage().getHeight(), normalColor);
        }

        if (Greenfoot.mouseClicked(this)) {
            onClick.run();
        }
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
        drawButton(getImage().getWidth(), getImage().getHeight(),
                visible ? normalColor : new Color(0, 0, 0, 0));
    }

    public void deleteButton() {
        if (getWorld() != null) {
            getWorld().removeObject(this);
        }
    }

    public String getText() {
        return text;
    }
}