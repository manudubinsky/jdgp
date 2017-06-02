package edu.jdgp;

import java.lang.Object;
import java.util.List;
import java.util.ArrayList;

public class divisors {
	
	public static void main(String[] args) throws Exception {
		int _sieve_size = 710;
		boolean[] bitmask = new boolean[710];
		int[] primes = new int[200];
		int cantPrimes = 0;
		// criba
		bitmask[0] = bitmask[1] = true;		
		for (int i = 2; i < _sieve_size; i++) {
			if (!bitmask[i]) {
				for (int j = i * i; j < _sieve_size; j += i)
					bitmask[j] = true;
				primes[cantPrimes++] = i;
			}
		}
		//divisores de N
		int N = 444932;
		int K = 444931;
		List divisores = new ArrayList();
		divisores.add(1);
		int PF_idx = 0;
		int PF = primes[PF_idx]; // primes has been populated by sieve
		while (PF * PF < N) {
			if (N % PF == 0) {
				int powerPF = PF;
				while (N % PF == 0) {
					int i = 1;
					int size = divisores.size();
					while (i < size && ((Integer)divisores.get(i)) != PF) {
						//System.out.print(((Integer)divisores.get(i)) + " ");
						if (((Integer)divisores.get(i)) * powerPF % K != 0)
							divisores.add(((Integer)divisores.get(i)) * powerPF);
						i++;
					}
					N /= PF;
					if (powerPF % K != 0)
						divisores.add(new Integer(powerPF));
					powerPF *= PF;
				}
			}
			PF = primes[++PF_idx];
		}

		if (N != 1) { // el ultimo es un factor primo
			int i = 1;
			int size = divisores.size();
			while (i < size) {
				if (((Integer)divisores.get(i)) * N % K != 0)
					divisores.add(((Integer)divisores.get(i)) * N);
				i++;
			}
			if (N % K != 0)
				divisores.add(new Integer(N));
		}
		
		int sum = 0;
		for(Object object : divisores) {
			Integer element = (Integer)object;
			//System.out.print(element + " ");
			sum += element;
		}
		System.out.println(sum);
	}
}

