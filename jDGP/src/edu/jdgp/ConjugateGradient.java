package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.SparseMatrixInt;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Implementacion de Gradiente Conjugado
 * */
public class ConjugateGradient {
	
	public static VecFloat execute(SparseMatrix A, VecFloat b, VecFloat xInitial, float delta) throws Exception {
		//A.dump(); b.dump();
		VecFloat x = xInitial;
		VecFloat r = b.clone();
		//A.multiplyByVector(x).dump();
		//r.dump();
		r.subtract(A.multiplyByVector(x));
		VecFloat p = r.clone();
		//x.dump(); r.dump(); p.dump();
		int dim = A.getRows();
		int i = 0;		
		while (A.multiplyByVector(x).subtract(b).squareNorm() > delta && i < dim) {
			VecFloat Ap = A.multiplyByVector(p);
			float alfa = r.squareNorm()/p.innerProd(Ap);
			x.addMultiple(p, alfa);
			VecFloat rNext = r.clone();
			rNext.addMultiple(Ap, -alfa);
			float beta = rNext.squareNorm()/r.squareNorm();
			p.multiplyByScalar(beta).add(rNext);
			r = rNext.clone();
			i++;
		}
		return x;
	}

	public static VecFloat execute(LaplacianMatrix A, VecFloat b, VecFloat xInitial, float delta) throws Exception {
		//A.dump(); b.dump();
		VecFloat x = xInitial;
		VecFloat r = b.clone();
		r.subtract(A.multiplyByVector(x));
		VecFloat p = r.clone();
		//x.dump(); r.dump(); p.dump();
		int dim = A.getRows();
		int i = 0;		
		while (A.multiplyByVector(x).subtract(b).squareNorm() > delta && i < dim) {
			VecFloat Ap = A.multiplyByVector(p);
			float alfa = r.squareNorm()/p.innerProd(Ap);
			x.addMultiple(p, alfa);
			VecFloat rNext = r.clone();
			rNext.addMultiple(Ap, -alfa);
			float beta = rNext.squareNorm()/r.squareNorm();
			p.multiplyByScalar(beta).add(rNext);
			r = rNext.clone();
			i++;
		}
		return x;
	}

	public static VecFloat execute(SparseMatrix A, VecFloat b) throws Exception {
		return execute(A, b, new VecFloat(A.getCols(), 0), 0.0000001f);
	}


	public static VecFloat execute(SparseMatrixInt A, VecInt b) throws Exception {
		return execute(A.toFloat(), b.toFloat());
	}

	public static VecFloat execute(LaplacianMatrix A, VecFloat b) throws Exception {
		return execute(A, b, new VecFloat(A.getCols(), 0), 0.0000001f);
	}


	public static VecFloat execute(LaplacianMatrix A, VecInt b) throws Exception {
		return execute(A, b.toFloat());
	}

/*	
Ejemplo para probar solvers Ax=b
el resultado deberia ser [1, 2, −1, 1]'
A = [10, -1, 2, 0;
	-1, 11, -1, 3;
	2, -1, 10, -1;
	0, 3, -1, 8]

b = [6, 25, -11, 15]'
*/
/*
	public static void main(String[] args) throws Exception {
		SparseMatrix A = new SparseMatrix(4);
		A.set(0, 0, 10); A.set(0, 1, -1); A.set(0, 2, 2);  A.set(0, 3, 0);
		A.set(1, 0, -1); A.set(1, 1, 11); A.set(1, 2, -1); A.set(1, 3, 3);
		A.set(2, 0, 2);  A.set(2, 1, -1); A.set(2, 2, 10); A.set(2, 3, -1);
		A.set(3, 0, 0);  A.set(3, 1, 3);  A.set(3, 2, -1); A.set(3, 3, 8);
		VecFloat b = new VecFloat(4);
		b.pushBack(6); b.pushBack(25); b.pushBack(-11); b.pushBack(15);
		ConjugateGradient.execute(A,b).dump();
	}
*/
/*
	public static void main(String[] args) throws Exception {
		int N = 5;
		Graph g = Graph.buildCompleteGraph(N);
		VecInt x = new VecInt(N);
		for (int i = 0; i < N; i++) {
			x.pushBack(ThreadLocalRandom.current().nextInt(1, 10 + 1));
		}
		SparseMatrixInt m = g.buildLaplacianMatrix();
		m.multiplyByVector(x).dump();
		VecFloat xSolution = ConjugateGradient.execute(m,m.multiplyByVector(x));
		x.dump();
		xSolution.dump();
		xSolution.subtract(x.toFloat()).dump();
	}
*/
//-2 -2 13 -7 -2
/*
	public static void main(String[] args) throws Exception {
		int N = 5;
		Graph g = Graph.buildCompleteGraph(N);
		SparseMatrixInt A = g.buildLaplacianMatrix();
		VecInt b = new VecInt(5);
		b.pushBack(-2); b.pushBack(-2); b.pushBack(13); b.pushBack(-7); b.pushBack(-2);
		ConjugateGradient.execute(A,b).dump();
		LaplacianMatrix m = new LaplacianMatrix(g);
		m.compact();
		ConjugateGradient.execute(A,b).dump();
	}
*/
/*
i: 30 norm: 45.641106
*****
dump() _vecLen: 30 _size: 30
 76 16 -14 16 -134 106 106 106 106 46 16 -74 -134 16 46 106 -74 106 106 16 76 -14 -44 -74 -44 -134 -164 -104 -14 -44
*****
*****
dump() _vecLen: 30 _size: 30
 76.0 16.0 -14.0 16.0 -134.0 106.0 106.0 106.0 106.0 46.0 16.0 -74.0 -134.0 16.0 46.0 106.0 -74.0 106.0 106.0 16.0 76.0 -14.0 -44.0 -74.0 -44.0 -134.0 -164.0 -104.0 -14.0 -44.0
 */
/*
	public static void main(String[] args) throws Exception {
		int cntSparse = 0;
		int cntLaplacian = 0;
		int N = 50;
		for (int i = 4; i < N; i++) {
			Graph g = Graph.buildCompleteGraph(i);
			SparseMatrixInt A = g.buildLaplacianMatrix();
			LaplacianMatrix m = new LaplacianMatrix(g);
			m.compact();
			for (int j = 0; j < 20; j++) {
				VecInt x = new VecInt(i);
				for (int k = 0; k < i; k++) {
					x.pushBack(ThreadLocalRandom.current().nextInt(1, 10 + 1));
				}
				VecFloat xFloat = x.toFloat();
				VecFloat b = m.multiplyByVector(xFloat);
				VecFloat xSparse = ConjugateGradient.execute(A,A.multiplyByVector(x));
				VecFloat xLaplacian = ConjugateGradient.execute(m,b);
				VecFloat diffSparse = xSparse.subtract(xFloat);
				VecFloat diffLaplacian = xLaplacian.subtract(xFloat);
				float normSparse = diffSparse.squareNorm();
				float normLaplacian = diffLaplacian.squareNorm();
				if (Math.abs(normLaplacian - normSparse) > 0.000f) {
					System.out.println("i: " + i);
					diffLaplacian.dump();
					diffSparse.dump();
					System.out.println("*****************************");
				}
				//System.out.println("diffSparse: " + diffSparse + " diffLaplacian: " + diffLaplacian);
				
			}
		}
		System.out.println("cntSparse: " + cntSparse + " cntLaplacian: " + cntLaplacian);
	}
	*/ 
/*	
	public static void main(String[] args) throws Exception {
		int cntSparse = 0;
		int cntLaplacian = 0;
		int N = 50;
		for (int i = 4; i < N; i++) {
			Graph g = Graph.buildCompleteGraph(i);
			SparseMatrixInt A = g.buildLaplacianMatrix();
			LaplacianMatrix m = new LaplacianMatrix(g);
			m.compact();
			for (int j = 0; j < 20; j++) {
				VecInt x = new VecInt(i);
				for (int k = 0; k < i; k++) {
					x.pushBack(ThreadLocalRandom.current().nextInt(1, 10 + 1));
				}
				VecFloat xFloat = x.toFloat();
				VecFloat b = m.multiplyByVector(xFloat);
				VecFloat xSparse = ConjugateGradient.execute(A,A.multiplyByVector(x));
				VecFloat xLaplacian = ConjugateGradient.execute(m,b);
				VecFloat diffSparse = b.subtract(A.multiplyByVector(xSparse));
				VecFloat diffLaplacian = b.subtract(m.multiplyByVector(xLaplacian));
				float normSparse = diffSparse.squareNorm();
				float normLaplacian = diffLaplacian.squareNorm();
				// if (Math.abs(normLaplacian - normSparse) > 0.000f) {
				System.out.println("i: " + i + " normLaplacian: " + normLaplacian + " normSparse: " + normSparse);
				//}
				//System.out.println("diffSparse: " + diffSparse + " diffLaplacian: " + diffLaplacian);
				
			}
		}
		System.out.println("cntSparse: " + cntSparse + " cntLaplacian: " + cntLaplacian);
	}
*/

/*
	public static void main(String[] args) throws Exception {
		int N = 500;
		for (int i = 4; i < N; i++) {
			Graph g = Graph.buildCompleteGraph(i);
			LaplacianMatrix m = new LaplacianMatrix(g);
			m.compact();
			VecFloat x = new VecFloat(i);
			x.pushBack(1); //un vector ortogonal al (1,...,1)
			x.pushBack(-1);
			VecFloat b = m.multiplyByVector(x);
			long start = System.nanoTime();
			VecFloat xLaplacian = ConjugateGradient.execute(m,b);
			long end = System.nanoTime();
			VecFloat diffLaplacian = b.subtract(m.multiplyByVector(xLaplacian));
			float normLaplacian = diffLaplacian.squareNorm();
			System.out.println("i: " + i + " normLaplacian: " + normLaplacian + " end - delta: " + (end - start)/1000000);
		}
	}
*/
/*
	public static void main(String[] args) throws Exception {
		int N = 500;
		for (int i = 4; i < N; i++) {
			Graph g = Graph.buildCompleteGraph(i);
			LaplacianMatrix m = new LaplacianMatrix(g);
			m.compact();
			VecInt x = new VecInt(i);
			for (int k = 0; k < i; k++) {
				x.pushBack(ThreadLocalRandom.current().nextInt(1, 10 + 1));
			}
			VecFloat b = m.multiplyByVector(x.toFloat());
			long start = System.nanoTime();
			VecFloat xLaplacian = ConjugateGradient.execute(m,b);
			long end = System.nanoTime();
			VecFloat diffLaplacian = b.subtract(m.multiplyByVector(xLaplacian));
			float normLaplacian = diffLaplacian.squareNorm();
			System.out.println("i: " + i + " normLaplacian: " + normLaplacian + " end - delta: " + (end - start)/1000000);
		}
	}
*/
/*
	public static void main(String[] args) throws Exception {
		int N = 500;
		for (int i = 4; i < N; i++) {
			Graph g = Graph.buildCompleteGraph(i);
			LaplacianMatrix m = new LaplacianMatrix(g);
			m.compact();
			VecFloat b = new VecFloat(i, 1); //un vector generador del subespacio ortogonal a m
			long start = System.nanoTime();
			VecFloat xLaplacian = ConjugateGradient.execute(m,b);
			long end = System.nanoTime();
			VecFloat diffLaplacian = b.subtract(m.multiplyByVector(xLaplacian));
			float normLaplacian = diffLaplacian.squareNorm();
			System.out.println("i: " + i + " normLaplacian: " + normLaplacian + " end - delta: " + (end - start)/1000000);
		}
	}
*/
/*
	public static void main(String[] args) throws Exception {
		int N = 1000;
		Graph g = Graph.buildCompleteGraph(N);
		LaplacianMatrix m = new LaplacianMatrix(g);
		m.compact();
		VecInt x = new VecInt(N);
		for (int k = 0; k < N; k++) {
			x.pushBack(ThreadLocalRandom.current().nextInt(1, 10 + 1));
		}
		VecFloat b = m.multiplyByVector(x.toFloat());
		long start = System.nanoTime();
		VecFloat xLaplacian = ConjugateGradient.execute(m,b);
		long end = System.nanoTime();
		VecFloat diffLaplacian = b.subtract(m.multiplyByVector(xLaplacian));
		float normLaplacian = diffLaplacian.squareNorm();
		System.out.println(" normLaplacian: " + normLaplacian + " end - delta: " + (end - start)/1000000);
	}
*/
// 5 (0) 1 (1) 3 (2) 1 (3)
	public static void main(String[] args) throws Exception {
		Graph g = Graph.buildCompleteBipartite(2,2);
		SparseMatrixInt A = g.buildLaplacianMatrix();
		VecInt b = new VecInt(4);
		b.pushBack(5); b.pushBack(1); b.pushBack(3); b.pushBack(1);
		A.dump();
		b.dump();
		ConjugateGradient.execute(A,b).dump();
	}

}
