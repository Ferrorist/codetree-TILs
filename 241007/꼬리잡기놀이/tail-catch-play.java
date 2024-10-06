import java.io.*;
import java.util.*;
public class Main {
    private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final int EMPTY = 0, HEAD = 1, OTHER = 2, TAIL = 3, MOVELINE = 4;
    private static int roundCount = 0;
    private static int[][] map, movelineMap;
    private static long[] scores;
    private static final int[][] dir = {{0, 1}, {-1, 0}, {0, -1}, {1, 0}}; // 우, 상, 좌, 하
    private static LinkedList<int[]>[] teamLists;

    public static void main(String[] args) throws Exception {
        initArguments();
        long answer = solve();
        System.out.println(answer);
    }

    private static void initArguments() throws Exception {
        StringTokenizer st = new StringTokenizer(in.readLine());
        int mapSize = Integer.parseInt(st.nextToken()); // 3 <= size <= 20
        int teamCount = Integer.parseInt(st.nextToken()); // 1 <= count <= 5
        roundCount = Integer.parseInt(st.nextToken()); // 1 <= round <= 1_000

        List<int[]> headList = new ArrayList<>();
        map = new int[mapSize][mapSize];
        movelineMap = new int[mapSize][mapSize];

        for(int y = 0; y < mapSize; y++) {
            st = new StringTokenizer(in.readLine());
            for(int x = 0; x < mapSize; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
                if(map[y][x] == HEAD)   headList.add(new int[]{y, x});
            }
        }

        teamLists = new LinkedList[teamCount];
        for(int i = 0; i < teamCount; i++)  teamLists[i] = new LinkedList<>();

        initMoveLines(headList);

        scores = new long[teamCount];
    }

    private static void initMoveLines(List<int[]> headList) {
        if(headList == null || headList.size() == 0)    return;

        int team = 0;
        boolean[][] visited = new boolean[map.length][map.length];

        for(int[] head : headList){
            teamLists[team] = moveLineBFS(head[0], head[1], visited, team);
            team++;
        }      
    }

    private static LinkedList<int[]> moveLineBFS(int start_y, int start_x, boolean[][] visited, int teamNum) {
        LinkedList<int[]> list = new LinkedList<>();
        int[] tail = new int[2];
        Queue<int[]> queue = new ArrayDeque<>();
        queue.offer(new int[]{start_y, start_x});

        while(!queue.isEmpty()){
            int[] current = queue.poll();
            int cy = current[0], cx = current[1];
            if(visited[cy][cx]) continue;
            visited[cy][cx] = true;

            movelineMap[cy][cx] = teamNum;
            if(map[cy][cx] == HEAD) list.addFirst(new int[]{cy, cx});
            else if(map[cy][cx] != MOVELINE)    {
                if(map[cy][cx] != TAIL) list.addLast(new int[]{cy, cx});
                else {
                    tail[0] = cy;   tail[1] = cx;
                    continue;
                }
            }
            movelineMap[cy][cx] = teamNum;

            for(int d = 0; d < dir.length; d++){
                int dy = cy + dir[d][0], dx = cx + dir[d][1];
                if(checkRange(dy, dx) && !visited[dy][dx] && map[dy][dx] != EMPTY) {
                    queue.offer(new int[]{dy, dx});
                }
            }
        }

        list.addLast(new int[]{tail[0], tail[1]});
        return list;
    }

    private static long solve() {
        long answer = 0;
        final int[][] starts = {{0, 0}, {map.length-1, 0}, {map.length - 1, map.length - 1}, {0, map.length - 1}};
        final int[][] moveRounds = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};

        for(int round = 1; round <= roundCount; round++){
            for(LinkedList<int[]> team: teamLists){
                moveTeam(team);
            }
            int roundGroup = ((round-1) / map.length) % 4;
            int step = ((round-1) % map.length);
            int ry = starts[roundGroup][0] + moveRounds[roundGroup][0] * step, rx = starts[roundGroup][1] + moveRounds[roundGroup][1] * step;

            int[] result = shootBall(ry, rx, roundGroup);
            if(result[0] != -1) {
                long calc = (long)result[1] * (long)result[1];
                scores[result[0]] += calc;
                teamLists[result[0]] = reverseTeam(result[0]);
            }

        }

        for(long score: scores) answer += score;

        return answer;
    }

    private static void moveTeam(LinkedList<int[]> team) {
        int[] head = team.peekFirst();
        int[] tail = team.peekLast();
        int[] tailsecond = team.get(team.size() - 2);

        int dy = -1, dx = -1;
        for(int d = 0; d < dir.length; d++){
            int cy = head[0] + dir[d][0], cx = head[1] + dir[d][1];
            if(checkRange(cy, cx) && map[cy][cx] != OTHER && map[cy][cx] != EMPTY) {
                dy = cy;    dx = cx;
                break;
            }
        }

        if(map[dy][dx] == TAIL){
            map[tailsecond[0]][tailsecond[1]] = TAIL;
            map[dy][dx] = HEAD;
            map[head[0]][head[1]] = OTHER;
        }
        else {
            map[dy][dx] = HEAD;
            map[head[0]][head[1]] = OTHER;
            map[tailsecond[0]][tailsecond[1]] = TAIL;
            map[tail[0]][tail[1]] = MOVELINE;
        }

        team.addFirst(new int[]{dy, dx});
        team.pollLast();
    }

    // answer[0] = 공을 만난 팀, answer[1] = 공을 받은 팀이 몇 번째 사람인지 확인
    private static int[] shootBall(int start_y, int start_x, int direction) {
        int[] answer = new int[]{-1, -1};

        int cy = start_y, cx = start_x;
        while(checkRange(cy, cx)){
            if(map[cy][cx] % MOVELINE != EMPTY){
                answer[0] = movelineMap[cy][cx];
                if(map[cy][cx] == HEAD) answer[1] = 1;
                else if(map[cy][cx] == TAIL)    answer[1] = teamLists[answer[0]].size();
                else answer[1] = findMember(answer[0], cy, cx);
                break;
            }
            cy += dir[direction][0];    cx += dir[direction][1];
        }

        return answer;
    }

    private static int findMember(int teamNum, int search_y, int search_x) {
        for(int i = 0; i < teamLists[teamNum].size(); i++){
            int[] current = teamLists[teamNum].get(i);
            if(current[0] == search_y && current[1] == search_x)    return i+1;
        }
        return -1;
    }

    private static LinkedList<int[]> reverseTeam(int teamNum) {
        LinkedList<int[]> list = new LinkedList<int[]>();
        while(teamLists[teamNum].size() > 0){
            list.addLast(teamLists[teamNum].pollLast());
        }

        int[] head = list.peekFirst();
        int[] tail = list.peekLast();

        map[head[0]][head[1]] = HEAD;
        map[tail[0]][tail[1]] = TAIL;
        return list;
    }

    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map.length;
    }
}