package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SpanningTree;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.WeightedGraphBuilder;
import edu.jdgp.WeightedGraphBuilder.ExactForm;
import edu.jdgp.BFSSpanningTree;


public class Jacobi {
	
	/* 
	 * Iteracion de Jacobi:
	 *  A = D + R (D es la diagonal de A)
	 *  x_(k+1) = D^-1 (b - R x_(k))
	 * 
	 * Este metodo resuelve el sistema lineal: Ax = b adaptado al problema de
	 * integración de 1-formas. En ese caso la matriz se descompone como:
	 *     A = [L|B;A,I]
	 *  Y si expresamos a A = R + D, D es la identidad (ver FormIntegratorSimple)
	 *    x_(k+1) = b + (-R) x_k
	 */ 
	public VecInt solve(SparseMatrixInt A, VecInt x, VecInt b) throws Exception {
		SparseMatrixInt minusR = A.clone();
		minusR.jacobiR(); //transforma A en -R
		int maxIter = 10;
		int i = 0;
		while (i < maxIter) {
			x = minusR.multiplyByVector(x);
			x.add(b);
			i++;
		}
		return x;
	}

	public static void main(String[] args) throws Exception  {
		SparseMatrixInt A = new SparseMatrixInt(3);
		//A = [4, -1, 0; -1, 4, -1; 0, -1, 4];
		A.set(0,0,1); A.set(0,1,0); A.set(0,2,0);
		A.set(1,0,-1); A.set(1,1,1); A.set(1,2,0);
		A.set(2,0,-1); A.set(2,1,-1); A.set(2,2,1);
		A.fullDump();
		//b = [2;6;2];
		VecInt b = new VecInt(3, 0);
		b.set(0,2); b.set(1,6); b.set(2,2);
		
		b.dump();
		
		VecInt x = new VecInt(3, 0);
		
		Jacobi j = new Jacobi();
		
		j.solve(A, x, b).dump();
		
		VecInt result = j.solve(A, x, b);
		A.multiplyByVector(result).dump();
		//x.dump();
	}
}
