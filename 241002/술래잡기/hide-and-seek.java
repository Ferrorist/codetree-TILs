import java.io.*;
import java.util.*;
public class Main {
    private static class Move {
        int length, moved;
        int direction;

        Move(int length, int direction) {
            this.length = length; this.moved = 0;
            this.direction = direction;
        }
    }

    private static class Runner implements Comparable<Runner> {
        int y, x;
        int direction;

        Runner(int y, int x, int direction){
            this.y = y; this.x = x;
            this.direction = direction;
        }

        @Override
        public int compareTo(Runner o){
            if(this.y != o.y) return this.y - o.y;
            return this.x - o.x;
        }

        @Override
        public int hashCode() {
            return 100 * this.y + this.x;
        }

        @Override
        public boolean equals(Object o) {
            if(this == o)   return true;
            if(!(o instanceof Runner)) return false;
            Runner o2 = (Runner)o;
            return this.y == o2.y && this.x == o2.x;
        }
    }

    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static int[][] map;
    private static boolean[][] existTree;
    private static final int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // 상, 우, 하, 좌
    private static int limit_turn = 0;
    private static List<Runner> runnerList;
    private static boolean[] catched;
    private static int tagger_y, tagger_x, tagger_dir;
    public static void main(String[] args) throws Exception {
        initArguments();
        int score = solve();
        System.out.println(score);
    }

    private static void initArguments() throws Exception {
        StringTokenizer st = new StringTokenizer(in.readLine());
        int mapSize = Integer.parseInt(st.nextToken());
        int runnerCount = Integer.parseInt(st.nextToken());
        int treeCount = Integer.parseInt(st.nextToken());
        limit_turn = Integer.parseInt(st.nextToken());

        map = new int[mapSize][mapSize];
        existTree = new boolean[mapSize][mapSize];
        runnerList = new ArrayList<>();

        // 도망자 정보 입력
        for(int i = 0; i < runnerCount; i++){
            st = new StringTokenizer(in.readLine());
            int runner_y = Integer.parseInt(st.nextToken()) - 1;
            int runner_x = Integer.parseInt(st.nextToken()) - 1;
            int runner_dir = Integer.parseInt(st.nextToken());
            runnerList.add(new Runner(runner_y, runner_x, runner_dir));
            map[runner_y][runner_x]++;
        }

        // 나무 정보 입력
        for(int i = 0; i < treeCount; i++){
            st = new StringTokenizer(in.readLine());
            int tree_y = Integer.parseInt(st.nextToken()) - 1;
            int tree_x = Integer.parseInt(st.nextToken()) - 1;
            existTree[tree_y][tree_x] = true;
        }

        // 술래 좌표 설정
        tagger_y = tagger_x = map.length / 2;
        tagger_dir = 0;
    }

    private static int solve() {
        int score = 0;
        // 술래의 이동 경로 확인
        List<Move> moveList = initMoveList(map.length * map.length - 1);

        int turn = 1, tagger_moveIdx = 0;
        while(limit_turn >= turn){
            // 1. 도망자 이동
            moveRunner();

            // 2. 술래 이동
            if(!moveTagger(moveList, tagger_moveIdx)) {
                moveList.get(tagger_moveIdx).moved = 0;
                tagger_moveIdx = (tagger_moveIdx + 1) % moveList.size();
                tagger_dir = moveList.get(tagger_moveIdx).direction;
            }

            // 3. 술래의 도망자 탐색 및 턴 종료
            int catchCount = searchRunner();
            score += (catchCount * turn++);
        }

        return score;
    }

    // 술래의 이동 경로 저장
    private static List<Move> initMoveList(int limit) {
        List<Move> moveList = new ArrayList<>();
        List<Integer> moveLengths = new ArrayList<>();
        List<Integer> moveDirs = new ArrayList<>();
        int turn = 0, total = 0, move_dir = 0, length = 1, moveCount = 0;
        
        // 정방향 움직임 저장
        while(total < limit){
            moveLengths.add(Math.min(length, limit-total));
            moveDirs.add(move_dir);
            total += length;
            moveCount++;
            if(moveCount >= length)  {
                length += (turn++) % 2; move_dir = (move_dir + 1) % 4;
            }
        } 

        // 역방향 움직임 저장
        for(int i = moveLengths.size() - 1; i >= 0; i--){
            int reverse_length = moveLengths.get(i);
            int reverse_dir = (moveDirs.get(i) + 2) % 4;
            moveLengths.add(reverse_length);
            moveDirs.add(reverse_dir);
        } 

        for(int i = 0; i < moveLengths.size(); i++){
            moveList.add(new Move(moveLengths.get(i), moveDirs.get(i)));
        }

        return moveList;
    }

    private static void moveRunner() {
        for(int i = 0; i < runnerList.size(); i++){
            Runner runner = runnerList.get(i);
            int cy = runner.y, cx = runner.x;
            int direction = runner.direction;

            if(Math.abs(cy - tagger_y) + Math.abs(cx - tagger_x) > 3)   continue;

            int dy = cy + dir[direction][0], dx = cx + dir[direction][1];

            if(!checkRange(dy, dx)){
                runner.direction = (direction + 2) % 4;
                direction = runner.direction;
                dy = cy + dir[direction][0];    dx = cx + dir[direction][1];
            }

            if(tagger_y != dy || tagger_x != dx) {
                map[cy][cx] = Math.max(map[cy][cx] - 1, 0);  map[dy][dx]++;
                runner.y = dy;
                runner.x = dx;
            }
        }

        Collections.sort(runnerList);
    }

    private static boolean moveTagger(List<Move> moveList, int idx){
        Move move = moveList.get(idx);
        tagger_y += dir[move.direction][0];
        tagger_x += dir[move.direction][1];
        move.moved++;
        return move.length > move.moved;
    }

    private static int searchRunner() {
        int count = 0;
        int cy = tagger_y, cx = tagger_x, direction = tagger_dir;

        for(int i = 0; i < 3; i++){
            int dy = cy + (dir[direction][0] * i);
            int dx = cx + (dir[direction][1] * i);
            if(!checkRange(dy, dx)) break; // 맵 밖이면 탐색 중단
            if(existTree[dy][dx] || map[dy][dx] <= 0)   continue; // 도망자가 없거나 나무가 있는 곳이면 탐색하지 않음

            Runner[] arr = runnerList.toArray(new Runner[runnerList.size()]);
            int searchIdx = Arrays.binarySearch(arr, new Runner(dy, dx, 0));


            if(searchIdx >= 0){
                while(runnerList.size() - 1 >= searchIdx && runnerList.get(searchIdx).hashCode() == dy * 100 + dx){
                    runnerList.remove(searchIdx);
                    count++;
                }
            }
        }

        return count;
    }


    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }
}