package edu.jdgp;

public class Prueba {
	public static void test(int i) {
		try {
			System.out.println(i);
			test(i+1);
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	
	public static void main(String[] args) {
		try {
			// test(1);
			boolean[] flags = new boolean[10];
			for (int i = 0; i < 10; i++) {				
				System.out.println(flags[i]);
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
