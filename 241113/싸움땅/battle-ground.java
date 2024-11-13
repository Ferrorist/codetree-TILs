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
            // System.out.println("round " + i + ": ");
            processRound();
            // printPlayersStates();
        }
    }

    private static void processRound() {
        for (int i = 0; i < players.length; i++) {
            Player currentPlayer = players[i];
            int cy = currentPlayer.y, cx = currentPlayer.x;
            int direction = currentPlayer.dir;

            int dy = cy + dir[direction][0], dx = cx + dir[direction][1];
            if (!checkRange(dy, dx)) {
                direction = (direction + (dir.length / 2)) % dir.length;
                dy = cy + dir[direction][0]; dx = cx + dir[direction][1];
            }

            if(checkPlayer(dy, dx)) {
                Player DualPlayer = playerMap[dy][dx];
                currentPlayer.y = dy;   currentPlayer.x = dx;
                currentPlayer.dir = direction;
                playerMap[cy][cx] = null;
                DualPlayer(currentPlayer, DualPlayer);
            }
            else {
                currentPlayer.y = dy;   currentPlayer.x = dx;
                currentPlayer.dir = direction;
                process(currentPlayer, 0);
            }

            playerMap[cy][cx] = null;
            playerMap[currentPlayer.y][currentPlayer.x] = currentPlayer;
            players[currentPlayer.idx] = currentPlayer;
        }
    }

    private static void DualPlayer(Player player1, Player player2) {
        Player[] DualPlayers = new Player[]{player1, player2};
        Arrays.sort(DualPlayers);
        int getScore = Math.abs(DualPlayers[0].getTotalState() - DualPlayers[1].getTotalState());
        // System.out.println("Winner Player's idx: " + DualPlayers[0].idx + ", getTotalState(): " + DualPlayers[0].getTotalState());
        // System.out.println("Winner Player's idx: " + DualPlayers[1].idx + ", getTotalState(): " + DualPlayers[1].getTotalState());
        moveLoser(DualPlayers[1]);
        process(DualPlayers[0], getScore);

        for(Player player : DualPlayers) {
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
                playerMap[cy][cx] = null;
                playerMap[dy][dx] = player;
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

    private static void printPlayersStates() {
        StringBuilder sb = new StringBuilder();
        for(Player player : players) {
            sb.append("[Player " + player.idx + "] ");
            sb.append("y: ").append(player.y);
            sb.append(", x: ").append(player.x);
            sb.append(", dir: ").append(player.dir).append(", state: " + player.state);
            sb.append(", gunDamage: ");
            if(player.hasGun == null) {
                sb.append(0);
            }
            else sb.append(player.hasGun.damage);
            sb.append(", score: ").append(scores[player.idx]);
            sb.append("\n");
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