import java.io.*;
import java.util.*;

public class Main {

    private static final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    private static final int[][] dir = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};

    private static Queue<Gun>[][] Gunmap;
    private static Player[] players;
    private static Player[][] playerMap;
    private static int[] scores;
    private static int mapLength, rounds;
    

    public static void main(String[] args) throws Exception {
        inputArguments();
        solve();
        printScores();
    }

    private static void inputArguments() throws Exception {
        String[] inputs = in.readLine().split(" ");

        mapLength = Integer.parseInt(inputs[0]);
        int[][] damageMap = getDamageMap(mapLength);
        initMap(mapLength, damageMap);

        initPlayers(Integer.parseInt(inputs[1]));
        rounds = Integer.parseInt(inputs[2]);
    }

    private static void initMap(int length, int[][] damageMap) {
        Gunmap = new PriorityQueue[length][length];
        playerMap = new Player[length][length];

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < length; x++) {
                Gunmap[y][x] = new PriorityQueue<>();
                if(damageMap[y][x] > 0) {
                    Gunmap[y][x].offer(new Gun(damageMap[y][x]));
                }
            }
        }
    }

    private static int[][] getDamageMap(int length) throws Exception {
        int[][] damageMap = new int[length][length];
        for (int y = 0; y < length; y++) {
            String[] inputs = in.readLine().split(" ");
            for (int x = 0; x < length; x++) {
                damageMap[y][x] = Integer.parseInt(inputs[x]);
            }
        }

        return damageMap;
    }

    private static void initPlayers(int count) throws Exception {
        players = new Player[count];
        scores = new int[count];
        for (int i = 0; i < count; i++) {
            String[] inputs = in.readLine().split(" ");
            int y = Integer.parseInt(inputs[0]) - 1;
            int x = Integer.parseInt(inputs[1]) - 1;
            int direction = Integer.parseInt(inputs[2]);
            int state = Integer.parseInt(inputs[3]);

            players[i] = new Player(y, x, direction, state, i);
            playerMap[y][x] = players[i];
        }
    }

    private static void solve() {
        for (int i = 1; i <= rounds; i++) {
            processRound();
        }
    }

    private static void processRound() {
        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = players[i];
            movePlayer(currentPlayer);
            int dy = currentPlayer.y, dx = currentPlayer.x;

            if(checkPlayer(dy, dx)) {
                DualPlayer(currentPlayer, playerMap[dy][dx]);
            }
            else {
                process(currentPlayer, 0);
            }
            playerMap[currentPlayer.y][currentPlayer.x] = currentPlayer;
            players[currentPlayer.idx] = currentPlayer;
        }
    }

    private static void movePlayer(Player player) {
        int cy = player.y, cx = player.x;
        int direction = player.dir;

        playerMap[cy][cx] = null;
        int dy = cy + dir[direction][0], dx = cx + dir[direction][1];
        if (!checkRange(dy, dx)) {
            direction = (direction + (dir.length / 2)) % dir.length;
            dy = cy + dir[direction][0]; dx = cx + dir[direction][1];
        }

        player.y = dy;  player.x = dx;
        player.dir = direction;
    }

    private static void DualPlayer(Player... duals) {
        Player[] DualPlayers = new Player[]{duals[0], duals[1]};
        for(Player player : duals) {
            playerMap[player.y][player.x] = null;
        }   

        Arrays.sort(DualPlayers);
        int getScore = Math.abs(DualPlayers[0].getTotalState() - DualPlayers[1].getTotalState());
        moveLoser(DualPlayers[1]);
        process(DualPlayers[0], getScore);

        for(Player player : DualPlayers) {
            playerMap[player.y][player.x] = player;
            players[player.idx] = player;
        }
    }

    private static void moveLoser(Player player) {
        releasePlayerGun(player);
        int cy = player.y, cx = player.x;
        for (int i = 0; i < dir.length; i++) {
            int direction = (player.dir + i) % dir.length;
            int dy = cy + dir[direction][0];
            int dx = cx + dir[direction][1];

            if(checkRange(dy, dx) && !checkPlayer(dy,dx)) {
                player.y = dy;  player.x = dx;
                player.dir = direction;
                process(player, 0);
                return;
            }
        }
    }

    private static void process(Player player, int score) {
        if(!Gunmap[player.y][player.x].isEmpty()){
            Gun releaseGun = player.changeGun(getGun(player.y, player.x));
            if (releaseGun != null) {
                Gunmap[player.y][player.x].offer(releaseGun);
            }
        }
        scores[player.idx] += score;
    }

    private static void releasePlayerGun(Player player) {
        if(player.hasGun != null) {
            int cy = player.y, cx = player.x;
            Gunmap[cy][cx].offer(player.hasGun);
        }
        player.hasGun = null;
    }

    private static Gun getGun(int y, int x) {
        if (Gunmap[y][x].isEmpty()) {
            return null;
        }
        return Gunmap[y][x].poll();
    }

    private static boolean checkPlayer(int y, int x) {
        return playerMap[y][x] != null;
    }

    private static boolean checkRange(int y, int x) {
        return y >= 0 && x >= 0 && y < mapLength && x < mapLength;
    }

    private static void printScores() {
        StringBuilder sb = new StringBuilder();
        for (int score : scores) {
            sb.append(score).append(" ");
        }
        System.out.println(sb.toString());
    }
}

class Gun implements Comparable<Gun> {
    int damage;

    public Gun(int damage) {
        this.damage = damage;
    }

    @Override
    public int compareTo(Gun o) {
        return o.damage - this.damage;
    }
}

class Player implements Comparable<Player> {
    int idx;
    int y, x, state;
    int dir;
    Gun hasGun;

    public Player(int y, int x, int d, int s, int idx) {
        this.y = y;
        this.x = x;
        this.dir = d;
        this.state = s;
        this.idx = idx;
        hasGun = null;
    }

    public Gun changeGun(Gun o) {
        if (hasGun == null) {
            this.hasGun = o;
            return null;
        }
        Gun currentGun = this.hasGun;
        if (this.hasGun.damage < o.damage) {
            this.hasGun = o;
            return currentGun;
        }
        return o;
    }

    public int getTotalState() {
        if(this.hasGun == null) {
            return this.state;
        }
        return this.state + this.hasGun.damage;
    }

    @Override
    public int compareTo(Player o) {
        if (this.getTotalState() == o.getTotalState()) {
            return o.state - this.state;
        }
        return o.getTotalState() - this.getTotalState();
    }
}