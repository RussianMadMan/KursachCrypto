
package signature522;

import java.util.Arrays;

public class TestGen {
    public static void main(String[] args){
	long times[] = new long[1];
	int iii = 0;	
	do{
	long time = System.currentTimeMillis(); 
	
	
	Signature.main(args);
	times[iii] = System.currentTimeMillis()-time;
	iii++;
	}while(iii<times.length);
	double asDouble = Arrays.stream(times).average().getAsDouble();
	System.out.println("Среднее время: " + asDouble);
    }
}
