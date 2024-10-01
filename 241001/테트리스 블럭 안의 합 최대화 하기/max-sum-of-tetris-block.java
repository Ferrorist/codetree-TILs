import java.io.*;
import java.util.*;
public class Main {
    private static BufferedReader in;
    private static int[][] map;
    private static List<int[][]> blockList;
    public static void main(String[] args) throws Exception {
        initReader();
        initArguments();
        int answer = solve();
        System.out.println(answer);
    }

    private static void initReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private static void initArguments() throws Exception {
        String[] input = in.readLine().split(" ");

        int sizeY = Integer.parseInt(input[0]);
        int sizeX = Integer.parseInt(input[1]);

        map = new int[sizeY][sizeX];

        for(int y = 0; y < sizeY; y++){
            input = in.readLine().split(" ");
            for(int x = 0; x < sizeX; x++)  map[y][x] = Integer.parseInt(input[x]);
        }

        initList();
    }

    private static int solve() {
        int answer = 0;
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[0].length; x++){
                for(int[][] block: blockList){
                    if(checkBlockRange(block, y, x))    answer = Math.max(answer, getSum(block, y, x));
                }
            }
        }
        return answer;
    }

    private static boolean checkBlockRange(int[][] block, int y, int x){
        for(int i = 0; i < block.length; i++){
            int cy = y + block[i][0], cx = x + block[i][1];
            if(cy < 0 || cy >= map.length || cx < 0 || cx >= map[0].length) return false;
        }

        return true;
    }

    private static int getSum(int[][] block, int y, int x){
        int answer = 0;
        for(int i = 0; i < block.length; i++){
            int cy = y + block[i][0], cx = x + block[i][1];
            answer += map[cy][cx];
        }

        return answer;
    }

    private static void initList() {
        blockList = new ArrayList<>();
        int[][][] block0 = {{{0, 0}, {1, 0}, {2, 0}, {3, 0}}, {{0, 0}, {0, 1}, {0, 2}, {0, 3}}}; // ----
        for(int i = 0; i < block0.length; i++)  blockList.add(block0[i]);
        
        int[][] block1 = {{0, 0}, {1, 0}, {0, 1}, {1, 1}}; // □
        blockList.add(block1);

        int[][][] block2 = {{{0, 0}, {1, 0}, {2, 0}, {2, 1}}, {{0, 0}, {0, 1}, {0, 2}, {1, 0}}, 
                            {{0, 0}, {0, 1}, {1, 1}, {2, 1}}, {{0, 0}, {1, 0}, {1, -1}, {1, -2}},
                            {{0, 0}, {1, 0}, {2, 0}, {2, -1}}, {{0, 0}, {0, 1}, {0, 2}, {1, 2}},
                            {{0, 0}, {0, 1}, {1, 0}, {2, 0}}, {{0, 0}, {1, 0}, {1, 1}, {1, 2}}}; // ┛
        for(int i = 0; i < block2.length; i++)  blockList.add(block2[i]);

        int[][][] block3 = {{{0, 0}, {1, 0}, {1, 1}, {2, 1}}, {{0, 0}, {1, 0}, {0, 1}, {1, -1}},
                            {{0, 0}, {0, 1}, {1, 1}, {1, 2}}, {{0, 0}, {1, 0}, {1, -1}, {2, -1}}};
        for(int i = 0; i < block3.length; i++)  blockList.add(block3[i]);

        int[][][] block4 = {{{0, 0}, {1, 0}, {1, 1}, {2, 0}}, {{0, 0}, {1, -1}, {1, 0}, {1, 1}},
                            {{0, 0}, {1, 0}, {1, -1}, {2, 0}}, {{0, 0}, {0, 1}, {0, 2}, {1, 1}}}; // ┴

        for(int i = 0; i < block4.length; i++)  blockList.add(block4[i]);
    }
}