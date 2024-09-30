import java.io.*;
import java.util.*;
public class Main {
    private static class Corr {
        int y, x;

        Corr(int y, int x){
            this.y = y;
            this.x = x;
        }

        Corr(Corr o){
            this.y = o.y;
            this.x = o.x;
        }

        boolean equals(int y, int x){
            return this.y == y && this.x == x;
        }

        boolean equalsCorr(Corr o){
            return this.y == o.y && this.x == o.x;
        }
    }

    private static class GameBoard {
        char[][] map;
        int count;
        Corr Blue, Red, Finish;

        GameBoard(char[][] map, int count){
            this.map = new char[map.length][map[0].length];
            this.count = count;

            for(int y = 0; y < map.length; y++){
                System.arraycopy(map[y], 0, this.map[y], 0, map[0].length);
            }
        }

        void setBlue(Corr blue){
            this.Blue = new Corr(blue);
        }

        void setRed(Corr red){
            this.Red = new Corr(red);
        }

        void setFinish(Corr finish){
            this.Finish = new Corr(finish);
        }

        GameBoard copyBoard() {
            GameBoard returnBoard = new GameBoard(this.map, this.count);
            returnBoard.setBlue(this.Blue);
            returnBoard.setRed(this.Red);
            returnBoard.setFinish(this.Finish);

            return returnBoard;
        }

        void printBoard() {
            StringBuilder sb = new StringBuilder();
            for(int y = 0; y < this.map.length; y++){
                for(int x = 0; x < this.map[0].length; x++) sb.append(this.map[y][x]);
                if(y < this.map.length - 1) sb.append("\n");
            }

            System.out.println(sb.toString());
            System.out.println("count: " + this.count);
        }
    }

    private static BufferedReader in;
    private static final int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // 상, 우, 하, 좌
    private static final char WALL = '#', EMPTY = '.';
    private static final int LIMIT = 10;
    private static Queue<GameBoard> BoardQueue;
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
        StringTokenizer st = new StringTokenizer(in.readLine());
        int N = Integer.parseInt(st.nextToken());
        int M = Integer.parseInt(st.nextToken());

        char[][] map = new char[N][M];
        Corr Blue = null, Red = null, Finish = null;
        for(int y = 0; y < map.length; y++){
            String input = in.readLine();
            for(int x = 0; x < map[0].length; x++){
                map[y][x] = input.charAt(x);
                if(map[y][x] == WALL || map[y][x] == EMPTY) continue;
                else if(map[y][x] == 'B')   Blue = new Corr(y, x);
                else if(map[y][x] == 'R')   Red = new Corr(y, x);
                else Finish = new Corr(y, x);               
            }
        }
        BoardQueue = new ArrayDeque<>();

        GameBoard initBoard = new GameBoard(map, 0);
        initBoard.setBlue(Blue);
        initBoard.setRed(Red);
        initBoard.setFinish(Finish);

        BoardQueue.offer(initBoard);
    }

    private static int solve() {
        while(!BoardQueue.isEmpty()){
            GameBoard current = BoardQueue.poll();
            if(current.count > LIMIT)   continue; // 제한 횟수 초과 시 continue
            for(int d = 0; d < dir.length; d++){
                GameBoard processBoard = current.copyBoard();
                processBoard.count += 1;
                // System.out.println("before process: ");
                // processBoard.printBoard();
                boolean red, blue;
                if(isFront(processBoard.Red, processBoard.Blue, d)) { // 빨간 사탕 먼저 움직임
                    red = process(processBoard, processBoard.Red, d, true);
                    blue = process(processBoard, processBoard.Blue, d, false); 
                }
                else { // 파란 사탕 먼저 움직임
                    blue = process(processBoard, processBoard.Blue, d, false);
                    red = process(processBoard, processBoard.Red, d, true);
                }

                // System.out.println("after process: ");
                // processBoard.printBoard();
                // System.out.println("red: " + red + ", blue: " + blue);
                // System.out.println(current.Red.y + ", " + current.Red.x);
                // System.out.println(processBoard.Red.y + ", " + processBoard.Red.x);
                // System.out.println(current.Blue.y + ", " + current.Blue.x);
                // System.out.println(processBoard.Blue.y + ", " + processBoard.Blue.x);

                // System.out.println("변화가 없는가?: " + (processBoard.Red.equalsCorr(current.Red) && processBoard.Blue.equalsCorr(current.Blue)));

                // 진행하여도 빨간 사탕과 파란 사탕의 위치가 같을 경우 Queue에 넣지 않음.
                if(processBoard.Red.equalsCorr(current.Red) && processBoard.Blue.equalsCorr(current.Blue))  continue;
                else if(blue)   continue;   // 파란 사탕이 들어갔을 경우 해당 경우 또한 Queue에 넣지 않음.
                else if(red)    return processBoard.count; // 빨간 사탕만 들어갔다면 횟수 return
                else {
                    BoardQueue.offer(processBoard); // 둘 다 들어가지 않았으므로 해당 경우를 Queue에 넣음.
                    // System.out.println("Queue에 넣음!");
                }
            }
        }

        return -1;
    }

    // Red가 Blue보다 앞인지
    private static boolean isFront(Corr Red, Corr Blue, int d){
        switch(d){
            case 0: // 상
                return Red.y <= Blue.y;
            case 1: // 우
                return Red.x >= Blue.x;
            case 2: // 하
                return Red.y >= Blue.y;
            default: // case 3 : 좌
                return Red.x <= Blue.x;
        }
    }

    // 출구에 들어갔으면 true, 그렇지 않으면 false
    private static boolean process(GameBoard board, Corr candy, int d, boolean isRed) {
        int dy = candy.y, dx = candy.x;
        while(true){
            if(dy < 0 || dy >= board.map.length || dx < 0 || dx >= board.map[0].length) break;

            int nexty = dy + dir[d][0], nextx = dx + dir[d][1];

            if(board.map[nexty][nextx] == EMPTY){
                dy = nexty; dx = nextx;
            }
            else {
                board.map[candy.y][candy.x] = EMPTY;
                if(board.map[nexty][nextx] == 'O'){ 
                    if(isRed) board.setRed(board.Finish);
                    else board.setBlue(board.Finish);
                    return true;
                }
                else {
                    if(isRed)   {
                        board.map[dy][dx] = 'R';
                        board.setRed(new Corr(dy, dx));
                    }
                    else {
                        board.map[dy][dx] = 'B';
                        board.setBlue(new Corr(dy, dx));
                    }
                    break;
                }
            }
        }

        return false;
    }    
}