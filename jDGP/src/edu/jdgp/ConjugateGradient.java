package edu.jdgp;

import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.SparseMatrixInt;
/*
 * Implementacion de Gradiente Conjugado
 * */
public class ConjugateGradient {
	
	public VecFloat execute(SparseMatrix A, VecFloat b, VecFloat xInitial, float delta) throws Exception {
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

	public VecFloat execute(SparseMatrix A, VecFloat b) throws Exception {
		return execute(A, b, new VecFloat(A.getCols(), 0), 0.0000001f);
	}


	public VecFloat execute(SparseMatrixInt A, VecInt b) throws Exception {
		return execute(A.toFloat(), b.toFloat());
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

	public static void main(String[] args) throws Exception {
		SparseMatrix A = new SparseMatrix(4);
		A.set(0, 0, 10); A.set(0, 1, -1); A.set(0, 2, 2);  A.set(0, 3, 0);
		A.set(1, 0, -1); A.set(1, 1, 11); A.set(1, 2, -1); A.set(1, 3, 3);
		A.set(2, 0, 2);  A.set(2, 1, -1); A.set(2, 2, 10); A.set(2, 3, -1);
		A.set(3, 0, 0);  A.set(3, 1, 3);  A.set(3, 2, -1); A.set(3, 3, 8);
		VecFloat b = new VecFloat(4);
		b.pushBack(6); b.pushBack(25); b.pushBack(-11); b.pushBack(15);
		ConjugateGradient c = new ConjugateGradient();
		c.execute(A,b).dump();
	}
}
