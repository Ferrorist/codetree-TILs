import java.io.*;
import java.util.*;
public class Main {
    private static class Dice {
        //   2
        // 3 1 4
        //   5
        //   6
        int[] dice; // 주사위(정육면체)의 면의 각 값. 맨 아랫면이 1 => 맨 위가 6

        Dice() {
            dice = new int[7];
        }

        void moveDice(int d){
            switch(d) {
                case 1:
                    moveRight();
                    return;
                case 2:
                    moveLeft();
                    return;
                case 3:
                    moveUp();
                    return;
                case 4:
                    moveDown();
                    return;
                default: 
                    return;
            }
        }

        void moveRight() {
            int temp = dice[4];
            dice[4] = dice[6];
            dice[6] = dice[3];
            dice[3] = dice[1];
            dice[1] = temp;
        }

        void moveLeft() {
            int temp = dice[3];
            dice[3] = dice[6];
            dice[6] = dice[4];
            dice[4] = dice[1];
            dice[1] = temp;
        }

        void moveUp() {
            int temp = dice[2];
            dice[2] = dice[6];
            dice[6] = dice[5];
            dice[5] = dice[1];
            dice[1] = temp;
        }

        void moveDown() {
            int temp = dice[5];
            dice[5] = dice[6];
            dice[6] = dice[2];
            dice[2] = dice[1];
            dice[1] = temp;
        }

        int getBottom() {
            return dice[1];
        }

        int getTop() {
            return dice[6];
        }

        void setBottom(int value){
            dice[1] = value;
        }

        String printDice() {
            StringBuilder sb = new StringBuilder();
            sb.append("  ").append(dice[2]).append("\n");
            sb.append(dice[3]).append(" ").append(dice[1]).append(" ").append(dice[4]).append("\n");
            sb.append("  ").append(dice[5]).append("\n");
            sb.append("  ").append(dice[6]);
            return sb.toString();
        }
    }

    private static BufferedReader in;
    private static final int[][] dir = {{0, 0}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}}; // 1: 동, 2: 서, 3: 북, 4: 남
    private static int[][] map;
    private static int[] diceRoot;
    private static int dice_x, dice_y;
    public static void main(String[] args) throws Exception {
        initReader();
        initArguments();
        solve();
    }

    private static void initReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private static void initArguments() throws Exception {
        StringTokenizer st = new StringTokenizer(in.readLine());
        map = new int[Integer.parseInt(st.nextToken())][Integer.parseInt(st.nextToken())];

        dice_x = Integer.parseInt(st.nextToken());
        dice_y = Integer.parseInt(st.nextToken());
        diceRoot = new int[Integer.parseInt(st.nextToken())];

        for(int y = 0; y < map.length; y++){
            st = new StringTokenizer(in.readLine());
            for(int x = 0; x < map[0].length; x++)  map[y][x] = Integer.parseInt(st.nextToken());
        }

        st = new StringTokenizer(in.readLine());
        for(int i = 0; i < diceRoot.length; i++)    diceRoot[i] = Integer.parseInt(st.nextToken());
    }

    private static void solve() {
        Dice dice = new Dice();
        StringBuilder sb = new StringBuilder();
        for(int d = 0; d < diceRoot.length; d++){
            int dy = dice_y + dir[diceRoot[d]][0];
            int dx = dice_x + dir[diceRoot[d]][1];

            if(!checkRange(dy, dx)) continue;

            dice.moveDice(diceRoot[d]);
            if(map[dy][dx] == 0){
                map[dy][dx] = dice.getBottom();
            }
            else {
                dice.setBottom(map[dy][dx]);
                map[dy][dx] = 0;
            }
            sb.append(dice.getTop());
            if(d < diceRoot.length - 1) sb.append("\n");
            dice_x = dx;    dice_y = dy;
        }

        System.out.println(sb.toString());
    }

    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }

    private static void printMap() {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[0].length; x++) sb.append(map[y][x]).append(" ");
            if(y < map.length - 1)  sb.append("\n");
        }

        System.out.println(sb.toString());
    }
}