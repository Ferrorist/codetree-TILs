import java.io.*;
import java.util.*;
public class Main {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final int FIRE = 2, WALL = 1, EMPTY = 0;
    private static final int[][] dir = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
    private static int[][] map;
    private static List<int[]> fires = new ArrayList<>();
    private static List<int[]> emptyBlocks = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        initArguments();
        int answer = solve();
        System.out.println(answer);
    }

    private static void initArguments() throws Exception {
        StringTokenizer st = new StringTokenizer(in.readLine());
        int n = Integer.parseInt(st.nextToken());
        int m = Integer.parseInt(st.nextToken());

        map = new int[n][m];
        for(int y = 0; y < map.length; y++){
            st = new StringTokenizer(in.readLine());
            for(int x = 0; x < map[0].length; x++){
                map[y][x] = Integer.parseInt(st.nextToken());
                if(map[y][x] == FIRE)   fires.add(new int[]{y, x});
                else if(map[y][x] == EMPTY) emptyBlocks.add(new int[]{y, x});
            }
        }
    }

    private static int solve() {
        int answer = 0;
        int emptySize = emptyBlocks.size();

        for(int a = 0; a < emptySize - 2; a++){
            int[] blockA = emptyBlocks.get(a);
            map[blockA[0]][blockA[1]] = WALL;
            for(int b = a+1; b < emptySize - 1; b++){
                int[] blockB = emptyBlocks.get(b);
                map[blockB[0]][blockB[1]] = WALL;
                for(int c = b+1; c < emptySize; c++) {
                    int[] blockC = emptyBlocks.get(c);
                    map[blockC[0]][blockC[1]] = WALL;
                    answer = Math.max(answer, simulate());
                    map[blockC[0]][blockC[1]] = EMPTY;
                }
                map[blockB[0]][blockB[1]] = EMPTY;
            }
            map[blockA[0]][blockA[1]] = EMPTY;
        }
        
        return answer;
    }

    private static int simulate() {
        int fire_count = 0;
        boolean[][] visited = new boolean[map.length][map[0].length];
        for(int[] fire: fires) {
            Queue<int[]> queue = new ArrayDeque<>();
            queue.offer(fire);

            while(!queue.isEmpty()){
                int[] current = queue.poll();
                if(visited[current[0]][current[1]]) continue;
                visited[current[0]][current[1]] = true;
                fire_count++;

                for(int d = 0; d < dir.length; d++){
                    int dy = current[0] + dir[d][0];
                    int dx = current[1] + dir[d][1];

                    if(checkRange(dy, dx) && !visited[dy][dx] && map[dy][dx] != WALL) {
                        queue.offer(new int[]{dy, dx});
                    }
                }
            }
        }

        return (emptyBlocks.size() - 3) - (fire_count - fires.size());
    }

    private static boolean checkRange(int y, int x) {
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }

    private static void printMap() {
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[0].length; x++) {
                sb.append(map[y][x]).append(" ");
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());
    }
}