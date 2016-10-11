package edu.jdgp;

//ACM

public class Triang {
		/*
		double x = 0, media = , sqrtX, nivel, cantTriangulos, minCantTriangulos = media, nivelAnterior = 0;
		System.out.println(media);
		for (int i = 0; i < K; i++) {
			x += media;
			sqrtX = Math.sqrt(x);
			nivel = Math.floor(sqrtX);
			if (sqrtX - nivel > 0.5) {
				nivel += 1;
			}
			cantTriangulos = nivel * nivel - nivelAnterior * nivelAnterior;
			if (cantTriangulos < minCantTriangulos) {
				minCantTriangulos = cantTriangulos;
			}
			nivelAnterior = nivel;
		}
		System.out.println(minCantTriangulos);
		*/

/*
	public static void test(long L, long K) {
		long mediaDiv = (L * L / K);
		long mediaMod = (L * L % K);
		long nivel, nivelAnterior = 0, minCantTriangulos = mediaDiv + 1;
		double sqrt;
		for (int i = 0; i < K; i++) {
			sqrt = Math.sqrt((i+1) * mediaDiv + (i+1) * mediaMod % K);
			nivel = (long) Math.floor(sqrt);			
			if (sqrt - nivel > 0.5) {
				nivel++;
			}
			long cantTriangulos = (nivel * nivel - nivelAnterior * nivelAnterior);
			if (cantTriangulos < minCantTriangulos) {
				minCantTriangulos = cantTriangulos;
			}
			nivelAnterior = nivel;			
		}
		System.out.println(minCantTriangulos);
	}
	*/
/*	
	public static void test(int L, int K) {
		long LL = (long) L;
		long mediaDiv = (LL * LL / (long)K);
		double KK = (double)K;
		double sqrt = Math.sqrt((KK-1)/K); //raiz de K-1/K
		int v[] = new int[K-1];
		int h = L;
		for (int i = 1; i <= K-1; i++) {
			if (Math.max(mediaDiv, 2 * h - 1) == mediaDiv)			
				v[K-1-i] = (int)Math.floor(h * sqrt);
			else
				v[K-1-i] = h - 1;
			h = v[K-1-i];
		}
		for (int i = 0; i < K-1; i++) {
			System.out.println(v[i]);
		}
	}
*/

	public static void test(int L, int K) {		
		long LL = (long) L;
		long mediaDiv = (LL * LL / (long)K);
		long cantMinTriangulos = mediaDiv + 1;		
		long h = L, hh;
		for (int i = 0; i < K; i++) {			
			if (Math.max(mediaDiv, 2 * h - 1) == mediaDiv) {
				if (i < K-1) {					
					hh = (int)Math.floor(Math.sqrt(h*h - mediaDiv));
					//System.out.println("h: " + h + " hh: " + hh + " exp: " + (2 * h * h - 2 * mediaDiv - 2 * hh * hh - 2 * hh));
					if (2 * h * h - 2 * mediaDiv - 2 * hh * hh - 2 * hh > 1) {
						hh++;
					}
				} else {
					hh = 0;
				}
			}
			else {
 				hh = h - 1;
 			}
 			
			long cantTriangulos = h * h - hh * hh;
			if (cantTriangulos < cantMinTriangulos) {
				cantMinTriangulos = cantTriangulos;
			}
			h = hh;
		}
		System.out.println(cantMinTriangulos);
	}

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();		
		test(1000000,30000);
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		//System.out.println(totalTime);
	}

}
