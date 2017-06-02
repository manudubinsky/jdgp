package edu.jdgp;


public class constant {
/*
5
1 1 1 1 1 1 1 1 1
1 2 3 4 5 6 7 8 9
2 4 6 8 10 12 14 16 29
2 4 6 8 10 12 14 17 17
1 1 1 1 1 1 1 1 100	
*/

	public static long f(int v[], int pos, int sumaParcial) {		
		long cant = 0;
		if (pos < 8) {			
			int h = (v[8] - sumaParcial) / v[pos];
			for (int i = 0; i <= h; i++) {				
				cant += f(v, pos + 1, sumaParcial + v[pos] * i);
			}
		} else if ((v[8] - sumaParcial) == 0) {
			cant = 1;
		}
		return cant;
	}
		
	public static void main(String[] args) throws Exception {
		int[] v = new int[]{ 1, 1, 1, 1, 1, 1, 1, 1, 100};		
		System.out.println(f(v,0,0));
		
	}
}

