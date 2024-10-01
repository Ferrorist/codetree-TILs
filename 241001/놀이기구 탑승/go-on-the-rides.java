import java.io.*;
import java.util.*;
public class Main {
    private static class Seat implements Comparable<Seat> {
        int y, x;
        int empty_count;
        
        Seat(int y, int x){
            this.y = y;
            this.x = x;
            empty_count = setEmptyCount(y, x);
        }

        @Override
        public int compareTo(Seat o){
            if(this.empty_count == o.empty_count){
                if(this.y == o.y)   return this.x - o.x;
                else return this.y - o.y;
            }
            else return o.empty_count - this.empty_count;
        }

        @Override
        public boolean equals(Object o){
            if(this == o)   return true;
            else if(!(o instanceof Seat)) return false;
            Seat seat = (Seat)o;
            return this.y == seat.y && this.x == seat.x && this.empty_count == seat.empty_count;
        }

        @Override
        public int hashCode() {
            return 100_000 * this.y + 100 * this.x + empty_count;
        }
    }

    private static BufferedReader in = null;
    private static final int[][] dir = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    private static int[] locations; // 각 학생 번호의 학생이 앉은 자리의 번호
    private static int[][] map; // 놀이기구 자리 현황
    private static String[][] stdInputArray;
    private static List<Integer>[] left_seats; // 주변에 비어있는 칸이 n개 있는 자리의 번호
    private static int[] empty_Seatcounts; // 각 자리의 비어있는 자리 수 ( 0 ~ 4 )
    public static void main(String[] args) throws Exception {
        initReader();
        initArguments();
        int answer = solve();
        System.out.println(answer);
    }

    private static void initReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    public static void initArguments() throws Exception {
        if(in == null)  initReader();
        int n = Integer.parseInt(in.readLine());

        locations = new int[n*n + 1];   Arrays.fill(locations, -1);
        map = new int[n][n];
        stdInputArray = new String[n*n][];
        
        for(int i = 0; i < n*n; i++){
            stdInputArray[i] = in.readLine().split(" ");
        }

        empty_Seatcounts = new int[n * n];
        left_seats = new ArrayList[5];
        for(int i = 0; i < left_seats.length; i++)  left_seats[i] = new ArrayList<>();
        
        for(int y = 0; y < map.length; y++) {
            for(int x = 0; x < map[0].length; x++) {
                int count = setEmptyCount(y, x);
                int seat_number = getSeatCount(y, x);
                left_seats[count].add(seat_number);
                empty_Seatcounts[seat_number] = count;
            }
        }
    }

    private static int solve() {
        for(int i = 0; i < stdInputArray.length; i++) {
            Map<Seat, Integer> hashMap = new HashMap<>();
            int student = Integer.parseInt(stdInputArray[i][0]);
            int[] favorite = new int[4];
            for(int j = 1; j < 5; j++)  favorite[j-1] = Integer.parseInt(stdInputArray[i][j]);

            int max_count = -1;
            // 좋아하는 학생의 위치를 파악한 후, 앉은 자리의 주변. 그 중 비어있는 자리에 대한 정보를 HashMap에 넣음.
            for(int favor: favorite){
                if(locations[favor] == -1)  continue;
                int ly = locations[favor] / map.length;
                int lx = locations[favor] % map.length;
                for(int d = 0; d < 4; d++){
                    int dy = ly + dir[d][0], dx = lx + dir[d][1];
                    if(checkRange(dy, dx) && map[dy][dx] == 0){
                        Seat seat = new Seat(dy, dx);
                        hashMap.put(seat, hashMap.getOrDefault(seat, 0) + 1);
                        max_count = Math.max(hashMap.get(seat), max_count);
                    }
                }
            }
            // 현재 학생이 좋아하는 학생 모두가 아직 타지 않았을 경우
            int seat_number = -1;
            if(hashMap.size() == 0) {
                getSeatNumber:
                for(int c = 4; c >= 0; c--){
                    Collections.sort(left_seats[c]);
                    for(Integer num : left_seats[c]){
                        int seatY = num / map.length;
                        int seatX = num % map.length;
                        if(map[seatY][seatX] == 0)  {
                            seat_number = num;
                            break getSeatNumber;
                        }
                    }
                }
            }
            else {
                List<Map.Entry<Seat, Integer>> entrySetList = new ArrayList<>();
                for(Map.Entry<Seat, Integer> entry: hashMap.entrySet()){
                    entrySetList.add(entry);
                }

                Collections.sort(entrySetList, new Comparator<Map.Entry<Seat, Integer>>(){
                    @Override
                    public int compare(Map.Entry<Seat, Integer> entry1, Map.Entry<Seat, Integer> entry2){
                        if(entry2.getValue() == entry1.getValue())  return entry1.getKey().compareTo(entry2.getKey());
                        return entry2.getValue() - entry1.getValue();
                    }
                });

                seat_number = getSeatCount(entrySetList.get(0).getKey().y, entrySetList.get(0).getKey().x);
            }
            // 배치 후, 남은 비어있는 칸 새로고침
            locations[student] = seat_number;
            int std_y = seat_number / map.length;
            int std_x = seat_number % map.length;
            map[std_y][std_x] = student;

            for(int d = 0; d < 4; d++){
                int dy = std_y + dir[d][0], dx = std_x + dir[d][1];
                if(checkRange(dy, dx)){
                    int adjust_seatNumber = getSeatCount(dy, dx);
                    left_seats[empty_Seatcounts[adjust_seatNumber]].remove(Integer.valueOf(adjust_seatNumber));
                    empty_Seatcounts[adjust_seatNumber] = Math.max(0, empty_Seatcounts[adjust_seatNumber] - 1);
                    left_seats[empty_Seatcounts[adjust_seatNumber]].add(adjust_seatNumber);
                }
            }
        }
        
        // 점수 계산
        int score = 0;
        for(String[] input : stdInputArray){
            int student = Integer.parseInt(input[0]);
            int[] favorite = new int[4];
            for(int j = 1; j < 5; j++)  favorite[j-1] = Integer.parseInt(input[j]);
            Arrays.sort(favorite);

            int[] cover = new int[4];
            int cy = locations[student] / map.length;
            int cx = locations[student] % map.length;
            for(int d = 0; d < 4; d++){
                int dy = cy + dir[d][0], dx = cx + dir[d][1];
                if(checkRange(dy,dx)){
                    cover[d] = map[dy][dx];
                }
            }
            Arrays.sort(cover);
            int count = 0;
            for(int favor : favorite){
                if(Arrays.binarySearch(cover, favor) < 0)   continue;
                else count++;
            }
            if(count > 0)   score += (int)Math.pow(10, count-1);
        }

        return score;
    }

    static int setEmptyCount(int y, int x) {
        int count = 0;
        for(int d = 0; d < dir.length; d++){
            int dy = y + dir[d][0];
            int dx = x + dir[d][1];
            if(checkRange(dy, dx) && map[dy][dx] == 0)
                count++;
        }
        return count;
    }

    private static boolean checkRange(int y, int x){
        return y >= 0 && y < map.length && x >= 0 && x < map[0].length;
    }

    private static int getSeatCount(int y, int x){
        return y * map.length + x;
    }
}