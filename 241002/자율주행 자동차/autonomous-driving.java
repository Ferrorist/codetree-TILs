// + 5분
import java.io.*;
import java.util.*;
public class Main {
    private static class Car {
        int y, x;
        int direction;
        boolean[] checked;

        Car(int y, int x, int direction){
            this.y = y;
            this.x = x;
            this.direction = direction;
            checked = new boolean[dir.length];
        }
    }
    private static BufferedReader in = null;
    private static final int EMPTY = 0, WALL = 1, VISITED = 2;
    private static final int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // 북, 동, 남, 서
    private static int[][] map;
    private static Car car;
    public static void main(String[] args) throws Exception {
        initArguments();
        int answer = solve();
        System.out.println(answer);
    }

    private static void initReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private static void initArguments() throws Exception {
        if(in == null)  initReader();
        StringTokenizer st = new StringTokenizer(in.readLine());

        int n = Integer.parseInt(st.nextToken());   // 맵의 세로 크기
        int m = Integer.parseInt(st.nextToken());   // 맵의 가로 크기 (3 <= n,m <= 50)

        map = new int[n][m]; // 맵 (9칸 <= 맵 크기 <= 2500칸)

        st = new StringTokenizer(in.readLine());
        int car_y = Integer.parseInt(st.nextToken());
        int car_x = Integer.parseInt(st.nextToken());
        int car_dir = Integer.parseInt(st.nextToken());

        car = new Car(car_y, car_x, car_dir);

        for(int y = 0; y < map.length; y++) {
            st = new StringTokenizer(in.readLine());
            for(int x = 0; x < map[0].length; x++) {
                map[y][x] = Integer.parseInt(st.nextToken());
            }
        }
    }

    private static int solve() {
        map[car.y][car.x] = VISITED;

        // 차의 움직임
        // 1. 차를 왼쪽으로 돌리고 visited인지 확인하기. (왼쪽 칸 == EMPTY)
        while(true){
            if(moveCar())   continue; // 1,2번 과정 진행
            else if(backProcess())  continue; // 3번 과정 진행
            else break;
        }

        return findVisited();
    }

    // 1, 2번 과정
    private static boolean moveCar() {
        int direction = car.direction;
        int cy = car.y, cx = car.x;

        while(true){
            direction = (direction + 3) % 4; // 차를 왼쪽으로 돌림.
            if(car.checked[direction])  break;
            car.checked[direction] = true;
            int dy = cy + dir[direction][0];
            int dx = cx + dir[direction][1];

            if(checkRange(dy, dx) && map[dy][dx] == EMPTY){ // 가본적이 없다면 1칸 전진
                processCar(dy, dx, direction);
                return true;
            }
        }

        return false; // 이동을 하지 못했다면, false
    }

    private static void processCar(int dy, int dx, int direction) {
        if(!checkRange(dy, dx) || map[dy][dx] == WALL)  return;

        car.y = dy; car.x = dx;
        car.direction = direction;
        Arrays.fill(car.checked, false); // 이동 후 체크한 방향 초기화
        map[dy][dx] = VISITED;
        return;
    }

    // 3번 과정 (후진)
    private static boolean backProcess() {
        int direction = (car.direction + 2) % 4;
        int dy = car.y + dir[direction][0], dx = car.x + dir[direction][1];

        if(checkRange(dy, dx) && map[dy][dx] != WALL) { // 후진이 가능한 경우 (맵 밖이 아니며 WALL이 아니어야 함)
            processCar(dy, dx, car.direction);
            return true;
        }
        else return false; // 후진조차 하지 못할 경우, false
    }

    private static int findVisited() {
        int answer = 0;

        for(int y = 0; y < map.length; y++){
            for(int x = 0; x < map[0].length; x++){
                if(map[y][x] == VISITED)    answer++;
            }
        }

        return answer;
    }


    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }
}