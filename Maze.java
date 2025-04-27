import java.util.Random;

public class Maze {
    private static final int WALL = 0;
    private static final int ROAD = 1;
    private static final int LIGHT = 8;
    private static final int MONSTER = 3;
    private static final int EXIT = 2;

    private Random random;

    private int width;
    private int height;
    public int[][] map;
    private int xx;
    private int yy;

    Maze(int x, int y) {
        random = new Random();
        width = x;
        height = y;
        xx = 2 * width + 1;
        yy = 2 * height + 1;
        map = new int[xx][yy];
    }

    public int[][] init() {
        for (int i = 0; i < xx; i++)
            for (int j = 0; j < yy; j++)
                map[i][j] = WALL;
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                map[2 * i + 1][2 * j + 1] = ROAD;
        accLabPrime();
        placeSpecialObjects();
        map[1][1] = LIGHT;
        map[xx - 2][yy - 2] = EXIT;
        return map;
    }

    public void accLabPrime() {
        int[] ok, not;
        int sum = width * height;
        int count = 0;
        ok = new int[sum];
        not = new int[sum];
        int[] offR = {-1, 1, 0, 0};
        int[] offC = {0, 0, 1, -1};

        int[] offS = {-1, 1, width, -width};
        for (int i = 0; i < sum; i++) {
            ok[i] = 0;
            not[i] = 0;
        }
        Random rd = new Random();
        ok[0] = rd.nextInt(sum);
        int pos = ok[0];
        not[pos] = 1;
        while (count < sum) {
            int x = pos % width;
            int y = pos / width;
            int offpos = -1;
            int w = 0;
            while (++w < 5) {
                int point = rd.nextInt(4);
                int repos;
                int move_x, move_y;
                repos = pos + offS[point];
                move_x = x + offR[point];
                move_y = y + offC[point];
                if (move_y >= 0 && move_x >= 0 && move_x < width && move_y < height && repos >= 0 && repos < sum
                        && not[repos] != 1) {
                    not[repos] = 1;
                    ok[++count] = repos;
                    pos = repos;
                    offpos = point;
                    map[2 * x + 1 + offR[point]][2 * y + 1 + offC[point]] = ROAD;
                    break;
                } else {
                    if (count == sum - 1)
                        return;
                }
            }
            if (offpos < 0) {
                pos = ok[rd.nextInt(count + 1)];
            }
        }
    }




    public void printMap() {
        for (int y = 0; y < yy; y++) {
            for (int x = 0; x < xx; x++) {
                System.out.print(map[y][x] + " ");
            }
            System.out.println();
        }
    }

    private void placeSpecialObjects() {
//        int exitX, exitY;
//        int way = random.nextInt(4);
//        if (way == 1) {
//            exitX = xx - 2;
//            exitY = yy - 2;
//        } else if (way == 2) {
//            exitY = yy - 2;
//            exitX = 1;
//        } else {
//            exitX = xx - 2;
//            exitY = 1;
//        }
//        map[exitY][exitX] = EXIT;

        int monsters = xx * yy / 20;
        int lights = xx * yy / 5;

        for (int i = 0; i < lights; i++) {
            int x, y;
            do {
                x = random.nextInt(xx - 2) + 1;
                y = random.nextInt(yy - 2) + 1;
            } while (map[y][x] != ROAD);
            map[y][x] = LIGHT;
        }

        for (int i = 0; i < monsters; i++) {
            int x, y;
            do {
                x = random.nextInt(xx - 2) + 1;
                y = random.nextInt(yy - 2) + 1;
            } while (map[y][x] != ROAD);
            map[y][x] = MONSTER;
        }
    }
}

