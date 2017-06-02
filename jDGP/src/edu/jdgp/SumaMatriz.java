package edu.jdgp;

import java.util.HashMap;
import java.util.Arrays;
import java.util.Random;

public class SumaMatriz {
	int n;
	int m;
	int[][] matriz;
	int[][] sumas;
	public String[][] caminos;

	public SumaMatriz() {
		for (int j = 0; j < 11; j++) {
			System.out.println(j);
		}
	}
	
	public void matriz() {
		m = 4; n = 4; 
		matriz = new int[m][n];
		matriz[0][0] = 2; matriz[0][1] = 8; matriz[0][2] = 3; matriz[0][3] = 4;
		matriz[1][0] = 5; matriz[1][1] = 3; matriz[1][2] = 4; matriz[1][3] = 5;
		matriz[2][0] = 1; matriz[2][1] = 2; matriz[2][2] = 2; matriz[2][3] = 1;
		matriz[3][0] = 3; matriz[3][1] = 4; matriz[3][2] = 6; matriz[3][3] = 5;
		sumas = new int[m][n];
		caminos = new String[m][n];
		for (int i = 0; i < m; i++) {
			for (int j = 0; j < n; j++) {
				sumas[i][j] = -1;
				caminos[i][j] = "";
			}
		}
	}

	public int f(int x, int y) {
		if (x == m || y == n)
			return Integer.MAX_VALUE;
		if (sumas[x][y] != -1) 
			return sumas[x][y];
		if (x == m-1 && y == n-1) {
			sumas[x][y] = matriz[m-1][n-1];
			return matriz[m-1][n-1];
		}
		int suma;
		int abajo = f(x+1, y);
		int derecha = f(x, y+1);
		if (abajo < derecha) {
			suma = abajo + matriz[x][y];
			caminos[x][y] = "A" + caminos[x+1][y];
		} else {
			suma = derecha + matriz[x][y];
			caminos[x][y] = "D" + caminos[x][y+1];
		}
		sumas[x][y] = suma;
		return suma;
	}

	public void dump() {
		for (int i = 0; i < m; i++) {
			String fila = "";
			for (int j = 0; j < n; j++) {
				fila += sumas[i][j] + " (" +  matriz[i][j] + ")   ";
			}
			System.out.println(fila);
		}
	}
	
	public static void main(String args[]) {
		SumaMatriz sm = new SumaMatriz();
		sm.matriz();
		long start; int s; long end;
		//start = System.nanoTime();
		s = sm.f(0,0);
		System.out.println("Suma mínima: " + s + " camino: " + sm.caminos[0][0]);
		sm.dump();
		//end = System.nanoTime();
		//System.out.println("Programacion dinamica: " + s + " " + (end - start) + " " + sm.caminos[0][0]);
	}
	
}

/*
	public void matriz() {
		m = 100; n = 100;
		matriz = new int[m][n];
		sumas = new int[m][n];
		caminos = new String[m][n];
		Random rand = new Random();
		for (int i = 0; i <m; i++) {
			for (int j = 0; j < n; j++) {
				matriz[i][j] = rand.nextInt(10);
				sumas[i][j] = -1;
				caminos[i][j] = "";
			}
		}
	}
*/

/*
public class SumaMatriz {
	int n;
	int m;
	int[][] matriz;
	int[][] sumas;


	public void matriz() {
		m = 15; n = 15;
		matriz = new int[m][n];
		sumas = new int[m][n];
		Random rand = new Random();
		for (int i = 0; i <m; i++) {
			for (int j = 0; j < n; j++) {
				matriz[i][j] = rand.nextInt(10);
				sumas[i][j] = -1;
			}
		}
	}

	public void matriz() {
		m = 4; n = 4; 
		matriz = new int[m][n];
		matriz[0][0] = 2; matriz[0][1] = 8; matriz[0][2] = 3; matriz[0][3] = 4;
		matriz[1][0] = 5; matriz[1][1] = 3; matriz[1][2] = 4; matriz[1][3] = 5;
		matriz[2][0] = 1; matriz[2][1] = 2; matriz[2][2] = 2; matriz[2][3] = 1;
		matriz[3][0] = 3; matriz[3][1] = 4; matriz[3][2] = 6; matriz[3][3] = 5;
	}

	public int sumaMatriz(int x, int y) {
		if (sumas.get(x + "_" + y) != null) {
			return sumas.get(x + "_" + y);
		}
		if (x == m || y == n)
			return -1;
		if (x == m-1 && y == n-1)
			return matriz[m-1][n-1];
					
		int suma = -1;
		int abajo = sumaMatriz(x, y+1);
		int derecha = sumaMatriz(x+1, y);
		if (abajo >= 0 && derecha >= 0)
			suma = Math.min(abajo, derecha) + matriz[x][y];
		else if (abajo == -1)
			suma = derecha + matriz[x][y];
		else if (derecha == -1)
			suma = abajo + matriz[x][y];
		sumas.put(x + "_" + y, new Integer(suma));
		return suma;
	}

	public int sumaMatriz(int x, int y) {
		if (x == m || y == n)
			return -1;
		if (sumas[x][y] != -1) 
			return sumas[x][y];
		if (x == m-1 && y == n-1)
			return matriz[m-1][n-1];
					
		int suma = -1;
		int abajo = sumaMatriz(x, y+1);
		int derecha = sumaMatriz(x+1, y);
		if (abajo >= 0 && derecha >= 0)
			suma = Math.min(abajo, derecha) + matriz[x][y];
		else if (abajo == -1)
			suma = derecha + matriz[x][y];
		else if (derecha == -1)
			suma = abajo + matriz[x][y];
		sumas[x][y] = suma;
		return suma;
	}

	public int sumaMatriz2(int x, int y) {
		if (x == m || y == n)
			return -1;
		if (x == m-1 && y == n-1)
			return matriz[m-1][n-1];
					
		int suma = -1;
		int abajo = sumaMatriz2(x, y+1);
		int derecha = sumaMatriz2(x+1, y);
		if (abajo >= 0 && derecha >= 0)
			suma = Math.min(abajo, derecha) + matriz[x][y];
		else if (abajo == -1)
			suma = derecha + matriz[x][y];
		else if (derecha == -1)
			suma = abajo + matriz[x][y];
		return suma;
	}

	public static void main(String args[]) {
		SumaMatriz sm = new SumaMatriz();
		sm.matriz();
		long start; int s; long end;
		//s = sm.sumaMatriz2(0,0);
		
		start = System.nanoTime();
		s = sm.sumaMatriz(0,0);
		end = System.nanoTime();
		System.out.println("Programacion dinamica: " + s + " " + (end - start));

		start = System.nanoTime();
		s = sm.sumaMatriz2(0,0);
		end = System.nanoTime();
		System.out.println("Recusividad simple: " + s + " " + (end - start));
	}
}
*/
