import greenfoot.Color;

import java.io.IOException;

public class MazeWorld extends WorldBase {
    private final Maze maze = new Maze(8, 8);
    private final int[][] map = maze.init();
    private final int[][] distanceArr = new int[map.length][map[0].length];
    private Hero hero;
    private int endX, endY;
    private int sellSize = 3;

    public MazeWorld(Hero hero) {
        this.hero = hero;
//        super(hero);
        maze.printMap();
        try {
            for (int y = 0; y < map.length; y++) {
                for (int x = 0; x < map[y].length; x++) {
                    if (map[y][x] == 0) {
                        getObjects().add(new ObjFile(
                                new float[]{x * sellSize, 0, y * sellSize},
                                1.5f,
                                ColorOperation.GColorToInt(new Color(0, 0, 0)),
                                "D:\\C_project\\Raytracer\\models\\block.obj",
                                0, true,
                                "D:\\C_project\\Raytracer\\images\\wall.png"
                        ));
                    }
                    if (map[y][x] == 2) {
                        endX = x;
                        endY = y;
                        ObjFile w = new ObjFile(
                                new float[]{x * sellSize, 0, y * sellSize},
                                0.2f,
                                ColorOperation.GColorToInt(new Color(0, 0, 0)),
                                "D:\\C_project\\Raytracer\\models\\flag.obj",
                                0, true,
                                "D:\\C_project\\Raytracer\\images\\flag.png"
                        );
                        setWinShape(w);
                        getObjects().add(w);
                    }
//                    if (map[y][x] == 3) {
//                        getObjects().add(new Npc(
//                                new float[]{x * 3, 1, y * 3},
//                                new float[]{-1, 0, 0},
//                                getObjects(),
//                                hero
//                        ));
//                    }
//                    if (map[y][x] == 8) {
//                        getLights().add(new Light(new float[]{x * sellSize, sellSize, y * sellSize},
//                                200, new float[]{getR().nextFloat(), getR().nextFloat(), getR().nextFloat()})
//                        );
//                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        distanceRecursion(endX, endY, 1);
        for (int i = 0; i < distanceArr.length; i++) {
            for (int j = 0; j < distanceArr[0].length; j++) {
                System.out.print(distanceArr[i][j] + " ");
            }
            System.out.println();
        }
    }

    private void distanceRecursion(int x, int y, int distance) {
        distanceArr[y][x] = distance;
        int[][] neighbors = {{x, y - 1}, {x, y + 1}, {x - 1, y}, {x + 1, y}};
        for (int[] neighbor : neighbors) {
            if (map[neighbor[1]][neighbor[0]] != 0 && distanceArr[neighbor[1]][neighbor[0]] == 0) {
                distanceRecursion(neighbor[0], neighbor[1], distance + 1);
            }
        }
    }

    public void getHeroNormal() {
        int[] xy = findNearestSell();
        int resX = xy[0];
        int resY = xy[1];
        int[][] neighbors = {{0, 1}, {0, -1}, {-1, 0}, {1, 0}};
        int min = Integer.MAX_VALUE;
        for (int[] neighbor : neighbors) {
            int xx = xy[0] + neighbor[0];
            int yy = xy[1] + neighbor[1];
            if (map[yy][xx] != 0 && distanceArr[yy][xx] < min) {
                min = distanceArr[yy][xx];
                resX = neighbor[0];
                resY = neighbor[1];
            }
        }
//        System.out.println(resX + " " + resY + " = " + min);
        float[] n = new float[]{resX, 0, resY};
        hero.setNormal(Vector3.normalize(n));
    }

    public int[] findNearestSell() {
        float min = Float.MAX_VALUE;
        int[] xy = new int[2];
        for (int y = 0; y < map.length; y++) {
            for (int x = 0; x < map[y].length; x++) {
                if (map[y][x] != 0) {
                    float xx = (float) (x + 0.5) * sellSize;
                    float yy = (float) (y + 0.5) * sellSize;
                    float heroX = hero.getPos()[0];
                    float heroY = hero.getPos()[2];
                    float edgeX = xx - heroX;
                    float edgeY = yy - heroY;
                    float distance = (float) Math.sqrt(edgeX * edgeX + edgeY * edgeY);
                    if (distance < min) {
                        xy[0] = x;
                        xy[1] = y;
                        min = distance;
                    }
                }
            }
        }
        return xy;
    }


}
