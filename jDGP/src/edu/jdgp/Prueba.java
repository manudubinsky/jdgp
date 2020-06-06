package edu.jdgp;

import java.util.Random;

public class Prueba {
	public static void main(String[] args) {
/*		int a = 11;
		int b = 3;
		float c = (float)a/b;
		int d = Math.round(c);
		System.out.println("c: " + c + " d: " + d);
*/
/*		int[] v;
		v = new int[0];
		System.out.println(v.length);
*/
/*		StringBuffer s = new StringBuffer();
		int[] v = {1,2,-1,2};
		s.append(String.format("%1$5d", v[0]));
		s.append(String.format("%1$5d", v[1]));
		System.out.println(s);
		s = new StringBuffer();
		s.append(String.format("%1$5d", v[2]));
		s.append(String.format("%1$5d", v[3]));
		System.out.println(s);

		System.out.printf("%1$13d", v[0]);
		System.out.printf("%1$13d", v[1]);
		System.out.println();
		System.out.printf("%1$13d", v[2]);
		System.out.printf("%1$13d", v[3]);
*/
/*		Map<Integer, Boolean> h = new HashMap<Integer, Boolean>();
		h.remove(12);
*/
/*
		for (int i = 0; i < 20; i++) {
			System.out.println(ThreadLocalRandom.current().nextInt(1, 5));
		}
*/
/*		long a = 7;
		long b = 3;
		System.out.println(a & b);
		if ((a & b) == 3)
			System.out.println("si");
*/
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			System.out.println(r.nextInt(5) + 2);			
		}
	}
}
