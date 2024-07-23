import java.io.*;
import java.util.Arrays;
public class Main {
    static class myClass implements Comparable<myClass> {
        char name;
        int score;

        public myClass(char name, int score){
            this.name = name;
            this.score = score;
        }

        @Override
        public int compareTo(myClass o){
            if(this.score == o.score) return this.name - o.name;
            return o.score - this.score;
        }
    }

    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        StringBuilder sb = new StringBuilder();
        int N = Integer.parseInt(in.readLine()); // 학생 n명

        myClass[] arr = new myClass[4];
        char WinnerClass = 'A';
        
        for(int i = 0; i < 4; i++){
            String[] input = in.readLine().split(" ");

            int sum = 0;
            for(int j = 1; j < input.length; j++)  sum += Integer.parseInt(input[j]);

            arr[i] = new myClass((char)('A' + i), sum);
            sb.append(arr[i].name).append(" - ").append(arr[i].score).append("\n");
        }
        Arrays.sort(arr);
        sb.append("Class ").append(arr[0].name).append(" is winner!");

        System.out.println(sb);
    }
}