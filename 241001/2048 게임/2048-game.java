import java.io.*;
import java.util.*;
public class Main {
    private static class GameBoard {
        int[][] map;
        int count, max_value;

        GameBoard(int[][] map, int count, int max_value){
            this.map = new int[map.length][map[0].length];
            for(int y = 0; y < map.length; y++)
                System.arraycopy(map[y], 0, this.map[y], 0, map[0].length);
            this.count = count;
            this.max_value = max_value;
        }

        GameBoard(GameBoard o){
            this.map = new int[o.map.length][o.map[0].length];
            for(int y = 0; y < map.length; y++)
                System.arraycopy(o.map[y], 0, this.map[y], 0, this.map[0].length);
            this.count = o.count;
            this.max_value = o.max_value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GameBoard other = (GameBoard) o;
            return Arrays.deepEquals(map, other.map);
        }

        @Override
        public int hashCode() {
            int result = Arrays.deepHashCode(this.map);  // 배열의 해시코드를 계산
            result = 31 * result + count;
            result = 31 * result + max_value;
            return result;
        }
    }

    private static BufferedReader in;
    private static GameBoard initMap;
    private static Set<GameBoard> mapSet;
    private static final int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; // 위, 아래, 왼쪽, 오른쪽
    private static int mapSize;
    private static final int LIMIT = 5;
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
        mapSize = Integer.parseInt(in.readLine());

        int[][] map = new int[mapSize][mapSize];
        int max_value = -1;
        for(int y = 0; y < mapSize; y++){
            String[] input = in.readLine().split(" ");
            for(int x = 0; x < mapSize; x++)   {
                map[y][x] = Integer.parseInt(input[x]);
                max_value = Math.max(max_value, map[y][x]);
            }
        }

        initMap = new GameBoard(map, 0, max_value);
        mapSet = new HashSet<>();
    }

    private static int solve() {
        int answer = 0;
        Queue<GameBoard> queue = new ArrayDeque<>();
        queue.offer(initMap);   mapSet.add(initMap);

        while(!queue.isEmpty()){
            GameBoard board = queue.poll();
            answer = Math.max(answer, board.max_value);
            if(board.count >= LIMIT)    continue;
            for(int d = 0; d < dir.length; d++){
                GameBoard processed = processBoard(board, d);
                if(!mapSet.contains(processed)){
                    mapSet.add(processed);
                    queue.offer(processed);
                }
            }
        }

        return answer;
    }

    private static GameBoard processBoard(GameBoard board, int d){
        GameBoard curBoard = new GameBoard(board);
        boolean[][] mixed = new boolean[mapSize][mapSize];
        if(d == 0){ // 위
            for(int x = 0; x < mapSize; x++){
                for(int y = 1; y < mapSize; y++){
                    curBoard.max_value = process(curBoard.map, y, x, d, curBoard.max_value, mixed);
                }
            }
        }
        else if(d == 1){ // 아래
            for(int x = 0; x < mapSize; x++){
                for(int y = mapSize - 2; y >= 0; y--){
                    curBoard.max_value = process(curBoard.map, y, x, d, curBoard.max_value, mixed);
                }
            }
        }
        else if(d == 2){ // 왼쪽
            for(int y = 0; y < mapSize; y++){
                for(int x = 1; x < mapSize; x++){
                    curBoard.max_value = process(curBoard.map, y, x, d, curBoard.max_value, mixed);
                }
            }
        }
        else { // 오른쪽
            for(int y = 0; y < mapSize; y++){
                for(int x = mapSize - 2; x >= 0; x--){
                    curBoard.max_value = process(curBoard.map, y, x, d, curBoard.max_value, mixed);
                }
            }
        }
        curBoard.count++;
        return curBoard;
    }

    private static int process(int[][] map, int y, int x, int d, int max_value, boolean[][] mixed){
        if(map[y][x] == 0)  return max_value;
        int answer = max_value;
        int dy = y, dx = x;
        boolean encounted = false;
        while(checkRange(dy + dir[d][0], dx + dir[d][1])){
            int move_y = dy + dir[d][0];
            int move_x = dx + dir[d][1];
            if(map[move_y][move_x] != 0){
                encounted = true;
                if(map[move_y][move_x] == map[y][x] && !mixed[move_y][move_x]){
                    map[move_y][move_x] = map[move_y][move_x] * 2;
                    answer = Math.max(answer, map[move_y][move_x]);
                    map[y][x] = 0;
                    mixed[move_y][move_x] = true;
                }
                else {
                    map[dy][dx] = map[y][x];
                    if(dy != y || dx != x) map[y][x] = 0;
                }
                break;
            }
            dy = move_y;    dx = move_x;
        }
        if(!encounted) {
            map[dy][dx] = map[y][x];  map[y][x] = 0;
        }
        return answer;
    }

    private static boolean checkRange(int y, int x){
        return y >= 0 && y < mapSize && x >= 0 && x < mapSize;
    }
}