import java.io.*;
public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String[] input = in.readLine().split(" ");
        int size = Integer.parseInt(input[0]);
        int type_num = Integer.parseInt(input[1]);


        int[][] triangles = new int[size+1][size+1];
        triangles[1][1] = 1;

        // 파스칼의 삼각형
        for(int y = 2; y <= size; y++){
            for(int x = 1; x <= y; x++){
                triangles[y][x] = triangles[y-1][x-1] + triangles[y-1][x];
            }
        }

        StringBuilder sb = new StringBuilder();

        switch(type_num){
            case 1:
                for(int y = 1; y <= size; y++){
                    for(int x = 1; x <= y; x++){
                        sb.append(triangles[y][x]).append(" ");
                    }
                    sb.append("\n");
                }
                break;
            
            case 2:
                for(int y = size; y >= 1; y--){
                    for(int x = size; x > y; x--)   sb.append(" ");
                    for(int x = 1; x <= y; x++){
                        sb.append(triangles[y][x]).append(" ");
                    }
                    sb.append("\n");
                }
                break;

            case 3:
                for(int x = size; x >= 1; x--){
                    for(int y = size; y >= x; y--){
                        sb.append(triangles[y][x]).append(" ");
                    }
                    sb.append("\n");
                }
                break;
        }

        System.out.println(sb);
    }
}