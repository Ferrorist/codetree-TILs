import java.io.*;
import java.util.Stack;
public class Main {
    public static void main(String[] args) throws Exception {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

        String[] input = in.readLine().split(" ");

        int A = Integer.parseInt(input[0]);
        String N = input[1];
        int B = Integer.parseInt(input[2]);

        long value = 0;
        long 진수 = 1;
        for(int i = N.length() - 1; i >= 0; i--){
            int k = 0;
            char c = N.charAt(i);
            if(c >= 'a' && c <= 'z'){
                k = 10 + (int)(c - 'a');
            }
            else k = (int)(c - '0');

            value += (long)k * 진수;
            진수 *= A;
        }
        
        Stack<Character> stack = new Stack<>();
        while(value > 0){
            int leftover = (int)(value % (long)B);
            value /= B;
            char ch = '0';
            if(leftover >= 10) ch = (char)((int)'a' + (leftover - 10));
            else ch = (char)((int)'0' + leftover);
            stack.push(ch);
        }

        StringBuilder sb = new StringBuilder();
        while(!stack.isEmpty()){
            sb.append(stack.pop());
        }

        System.out.println(sb);
    }
}