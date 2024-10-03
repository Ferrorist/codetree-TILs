import java.io.*;
import java.util.*;
public class Main {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static int[][] map;
    private static int[] parents;
    private static final int LIMIT_ROTATION = 4;
    private static final int[][] dir = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}}; // 우, 하, 좌, 상
    private static List<Integer> squareCount;
    public static void main(String[] args) throws Exception {
        initArguments();
        int score = solve();
        System.out.println(score);
    }

    private static void initArguments() throws Exception {
        StringTokenizer st;
        int mapSize = Integer.parseInt(in.readLine());

        map = new int[mapSize][mapSize];
        for(int y = 0; y < mapSize; y++){
            st = new StringTokenizer(in.readLine());
            for(int x = 0; x < mapSize; x++)    map[y][x] = Integer.parseInt(st.nextToken());
        }

        parents = new int[mapSize*mapSize];
        initStats();
    }

    private static void initStats() {
        initParents();
        squareCount = new ArrayList<>();
    }

    private static void initParents() {
        for(int i = 0; i < parents.length; i++) parents[i] = i;
    }

    private static int solve() {
        int score = 0;
        for(int i = 0; i < LIMIT_ROTATION; i++){
            List<int[]> corrList = constructCluster(); // 그룹 생성
            int[][] touchEdges = checkEdges(corrList); // 각 그룹이 서로 맞닿아 있는 변의 수 계산
            int calc = calcScore(corrList, touchEdges);
            score += calc; // 예술 점수 계산
            rotateMap(); // 그림 회전
            initStats(); // 회전 후 parents와 squareCount를 초기화
        }

        return score;
    }

    private static List<int[]> constructCluster() {
        List<int[]> corrList = new ArrayList<>();
        boolean[][] visited = new boolean[map.length][map.length];

        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map.length; x++){
                if(visited[y][x]) continue;
                int count = 1;
                corrList.add(new int[]{y, x});
                int a = map.length * y + x;
                visited[y][x] = true;
                Queue<int[]> queue = new ArrayDeque<>();    queue.offer(new int[]{y, x});
                while(!queue.isEmpty()){
                    int[] current = queue.poll();
                    int value = map[current[0]][current[1]];
                    for(int d = 0; d < dir.length; d++){
                        int dy = current[0] + dir[d][0];
                        int dx = current[1] + dir[d][1];
                        if(checkRange(dy, dx) && !visited[dy][dx] && value == map[dy][dx]){
                            visited[dy][dx] = true; count++;
                            int b = dy * map.length + dx;
                            union(a, b);
                            queue.offer(new int[]{dy, dx});
                        }
                    }
                }
                squareCount.add(count);
            }
        }
        
        return corrList;
    }

    private static int[][] checkEdges(List<int[]> corrList) {
        int[][] values = new int[corrList.size()][corrList.size()];
        boolean[][] visited = new boolean[map.length][map.length];
        int[] codes = new int[corrList.size()];

        for(int i = 0; i < corrList.size(); i++){
            int cy = corrList.get(i)[0], cx = corrList.get(i)[1];
            codes[i] = cy * map.length + cx;
        }

        for(int i = 0; i < corrList.size(); i++){
            int cy = corrList.get(i)[0], cx = corrList.get(i)[1];
            if(visited[cy][cx]) continue;
            visited[cy][cx] = true;
            Queue<int[]> queue = new ArrayDeque<>(); queue.offer(new int[]{cy, cx});

            while(!queue.isEmpty()){
                int[] current = queue.poll();
                int value = map[current[0]][current[1]];
                for(int d = 0; d < dir.length; d++){
                    int dy = current[0] + dir[d][0];
                    int dx = current[1] + dir[d][1];
                    if(checkRange(dy, dx) && !visited[dy][dx]){
                        if(value == map[dy][dx]) {
                            visited[dy][dx] = true;
                            queue.offer(new int[]{dy, dx});
                        }
                        else {
                            int bCode = dy * map.length + dx;
                            int idx = Arrays.binarySearch(codes, find(bCode));
                            values[i][idx]++;   values[idx][i]++;
                        }
                    }
                }
            }
        }

        return values;
    }

    private static int calcScore(List<int[]> corrList, int[][] touchEdges) {
        int score = 0;
        for(int i = 0; i < corrList.size() - 1; i++){
            for(int j = i+1; j < corrList.size(); j++){
                if(touchEdges[i][j] <= 0) continue;
                int ay = corrList.get(i)[0], ax = corrList.get(i)[1];
                int by = corrList.get(j)[0], bx = corrList.get(j)[1];
                int countA = squareCount.get(i), countB = squareCount.get(j);
                int numA = map[ay][ax], numB = map[by][bx];
                int calc = ((countA + countB) * numA * numB * touchEdges[i][j]);
                score += calc;
            }
        }

        return score;
    }

    private static void rotateMap() {
        int center = map.length / 2;
        rotateCross(0, center);

        boolean[][] visited = new boolean[map.length][map.length];

        rotateSquare(visited, 0, 0, center-1, center-1);
        rotateSquare(visited, 0, center+1, center-1, map.length-1);
        rotateSquare(visited, center+1, 0, map.length-1, center-1);
        rotateSquare(visited, center+1, center+1, map.length-1, map.length-1);
    }

    private static void rotateCross(int length, int center){
        if(length >= center) return;
        rotateCross(length + 1, center);
        int last = map.length - 1;

        int temp = map[center][last - length];
        map[center][last - length] = map[last - length][center];
        map[last - length][center] = map[center][length];
        map[center][length] = map[length][center];
        map[length][center] = temp;
    }

    private static void rotateSquare(boolean[][] visited, int start_y, int start_x, int end_y, int end_x) {
        if(start_y >= end_y && start_x >= end_x)    return;
        rotateSquare(visited, start_y + 1, start_x + 1, end_y - 1, end_x - 1);
        List<int[]> moveList = new ArrayList<>();
        Deque<Integer> deque = new ArrayDeque<>();
        int cy = start_y, cx = start_x, direction = 0;
        while(!visited[cy][cx]){
            deque.offerLast(map[cy][cx]);
            moveList.add(new int[]{cy, cx});
            visited[cy][cx] = true;
            int dy = cy + dir[direction][0], dx = cx + dir[direction][1];
            if(!(checkRange(dy, dx) && dy >= start_y && dy <= end_y && dx >= start_x && dx <= end_x)){
                direction = (direction + 1) % dir.length;
                dy = cy + dir[direction][0];    dx = cx + dir[direction][1];
            }
            cy = dy;    cx = dx;
        }

        deque.offerFirst(deque.pollLast());
        for(int[] corr : moveList){
            map[corr[0]][corr[1]] = deque.pollFirst();
        }
    }

    private static int find(int a){
        if(parents[a] == a) return a;
        else return find(parents[a]);
    }

    private static boolean union(int a, int b) {
        if(a > b)   return union(b, a);
        if(parents[a] != parents[b]) {
            parents[b] = find(parents[a]);  return true;
        }
        return false;
    }

    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map.length;
    }

    private static void print2dArray(int[][] arr){
        StringBuilder sb = new StringBuilder();
        for(int y = 0; y < arr.length; y++){
            for(int x = 0; x < arr[0].length; x++) sb.append(arr[y][x]).append(" ");
            if(y < arr.length - 1) sb.append("\n");
        }
        System.out.println(sb.toString());
    }
}