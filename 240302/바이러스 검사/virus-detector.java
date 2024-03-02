import java.io.*;
import java.math.BigInteger;
import java.lang.Math;

public class Main {
	public static void main(String[] args) throws Exception {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		BigInteger answer = BigInteger.ZERO;

		int size = Integer.parseInt(in.readLine());
		int[] customers = new int[size];
		String[] customer_input = in.readLine().split(" ");
		for(int i = 0; i < size; i++)
			customers[i] = Integer.parseInt(customer_input[i]);

		String strInput = in.readLine();
		int master_input = Integer.parseInt(strInput.split(" ")[0]);
		int team_input = Integer.parseInt(strInput.split(" ")[1]);

		for(int i = 0; i < size; i++){
			int leftover = Math.max(customers[i] - master_input, 0);
			if(leftover > 0) 
				answer = answer.add(BigInteger.valueOf(1 + (int)(Math.ceil(0 / leftover))));
			else answer = answer.add(BigInteger.ONE);
		}

		System.out.println(answer);
	}
}