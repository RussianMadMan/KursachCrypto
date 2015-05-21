
package signature522;

import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import static java.util.stream.Collectors.toList;
import static java.math.BigInteger.ONE;
import java.util.Arrays;

public class Signature {

    public static int length = 1024;

    public static void main(String[] args) {	
	//Хэш сообщения
	BigInteger h = new BigInteger(length*2,new Random());
	System.out.println("h: " + h);
	//Генерируем p q n
	PrimeGenerator gen = new PrimeGenerator(length, new Random());
	BigInteger p,q;
	BigInteger[] lastFactorsP,lastFactorsQ;	
	do{
	    p = gen.getPrime();
	    lastFactorsP = gen.getLastFactors();
	    q = gen.getPrime();
	    lastFactorsQ = gen.getLastFactors();
	}while(p.equals(q)); 	
	BigInteger n = p.multiply(q);	
	System.out.println("p: " + p);
	System.out.println("q: " + q);	
	System.out.println("n=p*q " + n);	

	//Получаем гамма1 и гамма2 из простых чисел в разложении p-1 и q-1	    	
	BigInteger[] gammas = getGammas(lastFactorsP,lastFactorsQ);
	BigInteger gamma = gammas[0].multiply(gammas[1]);
	System.out.println("gamma1: " + gammas[0]);
	System.out.println("gamma2: " + gammas[1]);
	System.out.println("gamma = gamma1*gamma2: " + gamma);	
	/*Ищем такое альфа что бы a^g = 1 mod n*/
	BigInteger a = getAlpha(gammas,p,q);
	System.out.println("alpha: "+ a);
	BigInteger[] publicKey = {n,a};
	BigInteger[] privateKey = {n,a,gamma};
	
	BigInteger[] signature = sign(h,privateKey);
	
	boolean verified = check(h,signature,publicKey);
	System.out.println(verified);
	
	
    }
    
    public static BigInteger[] sign(BigInteger h, BigInteger[] privateKey){
	BigInteger n = privateKey[0];
	BigInteger a = privateKey[1];
	BigInteger gamma = privateKey[2];
	BigInteger u = new BigInteger((int)(length*1.5),new Random());
	System.out.println("u: "+u);
	BigInteger invTwo = BigInteger.valueOf(2).modInverse(gamma);
	System.out.println("2^-1 mod gamma " + invTwo);
	/*Вычисляем параметры Z, k и g*/
	BigInteger z = a.modPow(u, n); //Z = a^U mod n
	BigInteger k = u.subtract(h.multiply(z)) //
			.multiply(invTwo)	 // k = (U-HZ)/2 mod gamma
			.mod(gamma);		 //
	BigInteger g = u.add(h.multiply(z))	 //   
			.multiply(invTwo)	 // g = (U+HZ)/2 mod gamma
			.mod(gamma);		 //
	System.out.println("z: " + z);
	System.out.println("k: " + k);
	System.out.println("g: " + g);
	/*Вычисляем подпись S*/
	BigInteger s = a.modPow(g, n); //S = a^g mod n
	System.out.println("s: " + s);
	
	BigInteger[] signature = {k,s};
	return signature;
    } 
    public static boolean check(BigInteger h, BigInteger[] signature, BigInteger[] publicKey){
	BigInteger n = publicKey[0];
	BigInteger a = publicKey[1];
	BigInteger k = signature[0];
	BigInteger s = signature[1];
	BigInteger test = a.modPow(k, n);   //a^k mod n
	test = test.multiply(s).mod(n);	    // s*a^k mod n
	test = test.multiply(h);	    // h*s*a^k mod n
	test = test.add(k);		    //k + h*s*a^k mod n
	test = a.modPow(test, n);	    //a^(k + h*s*a^k mod n) mod n
	System.out.println(test);
	return test.equals(s);
    }
    public static BigInteger getAlpha(BigInteger[] gammas, BigInteger p,BigInteger q){
	Random r = new Random();
	BigInteger mod =p.multiply(q);
	BigInteger exp1 =  p.subtract(ONE)
			    .multiply(q.subtract(ONE))
			    .divide(gammas[0]);
	BigInteger exp2 =  p.subtract(ONE)
			    .multiply(q.subtract(ONE))
			    .divide(gammas[1]);
	BigInteger[] s = new BigInteger[2];
	int count=0;
	do{
	    s[count] = new BigInteger(mod.bitCount()-1, r);
	    if(s[count].gcd(mod).equals(ONE))count++;
	}while(count!=2);
	BigInteger result = s[0].modPow(exp1, mod)
				.multiply(s[1].modPow(exp2, mod))
				.mod(mod);	
	return result;
    }
    public static BigInteger[] getGammas(BigInteger[] p, BigInteger[] q){
	List<BigInteger> primesFromp = Arrays.asList(p);
	List<BigInteger> primesFromq = Arrays.asList(q);
	//гамма1 не должна делить q а гамма2 не должна делить p
	List<BigInteger> nprimesFromp =  primesFromp.stream()
						    .filter(big -> !primesFromq.contains(big))
						    .collect(toList());
	List<BigInteger> nprimesFromq =  primesFromq.stream()
						    .filter(big -> !primesFromp.contains(big))
						    .collect(toList());	
	Random r = new Random();
	int pp = r.nextInt(nprimesFromp.size());
	int qp = r.nextInt(nprimesFromq.size());	    
	return new BigInteger[]{nprimesFromp.get(pp),nprimesFromq.get(qp)};
    }    
}
