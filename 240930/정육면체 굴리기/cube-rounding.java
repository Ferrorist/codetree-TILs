// 해설지를 참고한 코드
import java.io.*;
import java.util.StringTokenizer;
public class Main {
    private static class Dice {
        int Front, Up, Right;

        Dice(int up, int front, int right) {
            Up = up;
            Front = front;
            Right = right;
        }

        Dice moveDice(int d){
            switch(d){
                case 1: // 동쪽
                    return new Dice(7 - this.Right, this.Front, this.Up);
                case 2: // 서쪽
                    return new Dice(this.Right, this.Front, 7 - this.Up);
                case 3: // 북쪽
                    return new Dice(this.Front, 7 - this.Up, this.Right);
                case 4: // 남쪽
                    return new Dice(7 - this.Front, this.Up, this.Right);
                default:    
                    return null;
            }
        }

        int getBottomIndex() {
            return 7 - this.Up;
        }
    }

    private static BufferedReader in;
    private static int[][] map;
    private static final int[][] dir = {{0, 0}, {0, 1}, {0, -1}, {-1, 0}, {1, 0}};
    private static int[] Dices = new int[7];
    private static int dice_y, dice_x;
    private static int[] moveDir;
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

        dice_y = Integer.parseInt(st.nextToken());
        dice_x = Integer.parseInt(st.nextToken());

        moveDir = new int[Integer.parseInt(st.nextToken())];

        for(int y = 0; y < map.length; y++){
            st = new StringTokenizer(in.readLine());
            for(int x = 0; x < map[0].length; x++)  map[y][x] = Integer.parseInt(st.nextToken());
        }

        st = new StringTokenizer(in.readLine());
        for(int i = 0; i < moveDir.length; i++) moveDir[i] = Integer.parseInt(st.nextToken());
    }

    private static void solve() {
        StringBuilder sb = new StringBuilder();
        Dice dice = new Dice(1, 2, 3);

        for(int d : moveDir){
            int dy = dice_y + dir[d][0], dx = dice_x + dir[d][1];

            if(!checkRange(dy, dx)) continue;
            dice = dice.moveDice(d);
            
            if(map[dy][dx] == 0){
                map[dy][dx] = Dices[dice.getBottomIndex()];
            }
            else {
                Dices[dice.getBottomIndex()] = map[dy][dx];
                map[dy][dx] = 0;
            }

            sb.append(Dices[dice.Up]).append("\n");
            dice_y = dy;    dice_x = dx;
        }

        System.out.println(sb.toString());
    }

    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }
}