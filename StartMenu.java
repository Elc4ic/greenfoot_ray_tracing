import greenfoot.*;

public class StartMenu extends Actor {
    private boolean isActive = true;
    private Button[] buttons = {};
    private Button startButton;
    private Button manualButton;
    private Button backButton;

    public StartMenu() {
        GreenfootImage bg = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
        bg.setColor(Color.BLACK);
        bg.fill();
        setImage(bg);
    }

    public void addButton(Button button) {
        Button[] newButtons = new Button[buttons.length + 1];
        System.arraycopy(buttons, 0, newButtons, 0, buttons.length);
        newButtons[buttons.length] = button;
        buttons = newButtons;

        if (button.getText().equals("START")) {
            startButton = button;
        } else if (button.getText().equals("MANUAL")) {
            manualButton = button;
        } else if (button.getText().equals("BACK")) {
            backButton = button;
        }
    }

    public void removeAllButtons() {
        for (Button button : buttons) {
            if (button.getWorld() != null) {
                getWorld().removeObject(button);
            }
        }
        buttons = new Button[0];
        startButton = null;
        manualButton = null;
        backButton = null;
    }

    public void showMainMenu() {
        GreenfootImage bg = new GreenfootImage(Const.WIDTH, Const.HEIGHT);
        bg.setColor(Color.BLACK);
        bg.fill();
        setImage(bg);

        if (backButton != null && backButton.getWorld() != null) {
            getWorld().removeObject(backButton);
            backButton = null;
        }

        if (getWorld() instanceof TheWorld) {
            TheWorld world = (TheWorld) getWorld();
            if (startButton == null) {
                startButton = new Button("START", 200, 60, () -> {
                    deactivate();
                    setImage(Const.NOTHING);
                });
                addButton(startButton);
            }

            if (manualButton == null) {
                manualButton = new Button("MANUAL", 200, 60, () -> {
                    showManual();
                });
                addButton(manualButton);
            }

            if (startButton.getWorld() == null) {
                world.addObject(startButton, Const.WIDTH/2, Const.HEIGHT/2);
            }
            if (manualButton.getWorld() == null) {
                world.addObject(manualButton, Const.WIDTH/2, Const.HEIGHT/2 + 80);
            }
        }
    }

    public void showManual() {
        GreenfootImage manualBg = new GreenfootImage(Const.WIDTH - 200, Const.HEIGHT - 200);
        manualBg.setColor(new Color(50, 50, 50));
        manualBg.fill();

        manualBg.setColor(Color.WHITE);
        String[] manualText = {
                "Инструкция:",
                "",
                "1. WASD для перемещения",
                "2. Перемещайте мышку чтобы осматриваться",
                "3. Убивайте врагов и собирайте опыт",
                "4. Повышайте уровень и усиливайтесь",
                "5. Проживите 15 минут для победы",
                "",
                "Удачи!"
        };
        int yPos = 30;
        for (String line : manualText) {
            manualBg.drawString(line, 20, yPos);
            yPos += 30;
        }

        setImage(manualBg);

        if (startButton != null && startButton.getWorld() != null) {
            getWorld().removeObject(startButton);
        }
        if (manualButton != null && manualButton.getWorld() != null) {
            getWorld().removeObject(manualButton);
        }

        if (backButton == null && getWorld() instanceof TheWorld) {
            TheWorld world = (TheWorld)getWorld();
            backButton = new Button("BACK", 200, 60, () -> {
                showMainMenu();
            });

            addButton(backButton);
            world.addObject(backButton, Const.WIDTH/2, Const.HEIGHT/2 + 250);
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void deactivate() {
        removeAllButtons();
        isActive = false;
    }

}