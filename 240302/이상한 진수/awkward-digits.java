import java.io.*;
import java.util.*;
import java.lang.Math;
public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String binary = in.readLine();
        String ternary = in.readLine();

        if(binary.charAt(0) - '0' == 0){
            binary = "0" + binary.substring(1);
            System.out.println(BinaryToNum(binary, 2));
            return;
        }

        Set<Integer> set = new HashSet<>();
        for(int i = 0; i < binary.length(); i++){
            StringBuilder sb = new StringBuilder(binary);
            int n = Math.abs((binary.charAt(i) - '0') - 1);
            sb.setCharAt(i, (char)(n + '0'));
            set.add(BinaryToNum(sb.toString(), 2));
        }

        for(int i = 0; i < ternary.length(); i++){
            StringBuilder sb = new StringBuilder(ternary);
            int n = Math.abs((ternary.charAt(i) - '0'));
            for(int j = 1; j < 3; j++){
                int k = (n + j) % 3;
                sb.setCharAt(i, (char)(k + '0'));
                int search = BinaryToNum(sb.toString(), 3);
                if(set.contains(search)){
                    System.out.println(search);
                    return;
                }
            }
        }
    }

    static int BinaryToNum(String str, int base){
        double answer = 0;
        int length = str.length();
        int pownum = 0;
        for(int i = length-1; i >= 0; i--){
            int n = str.charAt(i) - '0';
            answer += (Math.pow(base, pownum++) * n);
        }

        return (int)answer;
    }
}