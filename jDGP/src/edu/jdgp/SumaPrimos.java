package edu.jdgp;

import java.util.HashMap;
import java.util.Arrays;

//n<=1120, k <= 14


public class SumaPrimos {
	int[] criba;
	HashMap<String, Integer> sumaPrimos = new HashMap<String, Integer>();
	
	public void cribaPrimos() {
		criba = new int[187];
		int max = 1120;
		int idx = 0;
		criba[idx++] = 2;
		// loop through the numbers one by one
		for (int i = 3; i < max; i++) {
			boolean isPrimeNumber = true;
			for (int j = 2; j < i; j++) {
				if (i % j == 0) {
					isPrimeNumber = false;
					break;
				}
			}
			if (isPrimeNumber) {
				criba[idx++] = i;
			}
		}
		//System.out.println(Arrays.toString(criba));
	}

	public int sumaPrimos(int n, int k, int idxPrimo) {	
		if (sumaPrimos.get(n + "_" + k) != null)
			return sumaPrimos.get(n + "_" + k);
		else if (n == 0 && k == 0) {
			System.out.println("OK!");
			return 1;
		}
		else if (n < 0 || k <= 0)
			return 0;
		else {
			int cant = 0;
			for (int i = 0; i < (criba.length - idxPrimo) && (criba[idxPrimo + i + 1] <= n); i++) {
				//String s = "";
				//for (int j = 0; j < 3 - k; j++) {
					//s += "\t";
				//}
				//System.out. println(s + criba[idxPrimo + (i - idxPrimo)]);
				cant += sumaPrimos(n - criba[idxPrimo + i], k - 1, idxPrimo + i + 1);
				//System.out.println(n + " " + k + " " + idxPrimo + " " + cant);
			}
			sumaPrimos.put(n + "_" + k, new Integer(cant));
		}
		return sumaPrimos.get(n + "_" + k);
	}
	
	public static void main(String args[]) {
		SumaPrimos sp = new SumaPrimos();
		sp.cribaPrimos();
		System.out.println(sp.sumaPrimos(24, 3, 0));
	}
}
