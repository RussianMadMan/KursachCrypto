
package signature522;

import java.math.BigInteger;
import static java.math.BigInteger.ONE;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;

public class PrimeGenerator {
    private final Random gen;
    private final int target,primesC = 3;
    private BigInteger[] lastPrimes;
    public PrimeGenerator(int length, Random r){
	target = length;
	gen = r;
    }
    public BigInteger getPrime(){
	BigInteger[] primes = new BigInteger[40];
	int l = target/primesC>10?target/primesC:10;	
	for(int i = 0;i<primes.length;i++){
	    primes[i] = new BigInteger(l,30,gen);
	    //System.out.println("BIT COUNT:" + primes[i].bitCount());
	    //System.out.println("BIT LENGTH:" + primes[i].bitLength()+"\n");
	}
	List<BigInteger> primesList = Arrays.stream(primes)
					    .distinct()
					    .collect(toCollection(LinkedList::new));
	List<BigInteger> selectedList,exps;	
	boolean found = false,med;
	BigInteger probable= null,pminus;
	BigInteger b;
	int i =0;
	while(!found){
	    i++;
	    Collections.shuffle(primesList);
	    selectedList = primesList.stream().limit(primesC).collect(toList());
	    probable = selectedList.stream().reduce(BigInteger::multiply).get();
	    probable = probable.multiply(BigInteger.valueOf(2)).add(ONE);
	    pminus = probable.subtract(ONE);
	    exps = new ArrayList<>();
	    for (BigInteger selectedList1 : selectedList) {
		exps.add(pminus.divide(selectedList1));
	    }
	    b = BigInteger.valueOf(2);	    
	    if(probable.isProbablePrime(30)){
		found =true;
		
		System.out.println("Попыток: "+i);
		lastPrimes = selectedList.toArray(new BigInteger[selectedList.size()]);
	    }	    	    
	}	
	return probable;
    }
    public BigInteger[] getLastFactors(){
	return this.lastPrimes;
    }
}
