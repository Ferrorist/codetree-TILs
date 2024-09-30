import java.io.*;
import java.util.StringTokenizer;
public class Main {
    private static BufferedReader in;
    private static int Restaurant_count; // 식당 수 ( 1 <= n <= 1_000_000 )
    private static int[] customers; // 각 식당 고객 수 ( 1 <= n <= 1_000_000 )
    private static int leader, member; // 팀장, 팀원 한 명이 검사 가능한 최대 고객 수
    public static void main(String[] args) throws Exception {
        initReader();
        initArguments();
        long answer = solve();
        System.out.println(answer);
    }

    private static void initReader() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    private static void initArguments() throws Exception {
        Restaurant_count = Integer.parseInt(in.readLine());
        customers = new int[Restaurant_count];

        StringTokenizer st = new StringTokenizer(in.readLine());
        for(int i = 0; i < Restaurant_count; i++)   customers[i] = Integer.parseInt(st.nextToken());

        st = new StringTokenizer(in.readLine());
        leader = Integer.parseInt(st.nextToken());
        member = Integer.parseInt(st.nextToken());
    }

    private static long solve() {
        long answer = 0;
        for(int Restaurant: customers) {
            Restaurant -= leader;
            answer += (1 + Math.max((long)Math.ceil((double)Restaurant/member), 0));
        }

        return answer;
    }
}