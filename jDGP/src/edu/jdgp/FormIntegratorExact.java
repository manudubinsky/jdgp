package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.ConjugateGradient;

/*
 * Implementacion exacta (backtracking) del algoritmo para integrar 1-formas
 * Ver: Integration1FormsGRASP.pdf
 * 1) Inicio: D x = v
 * 2) Busacamos D tal que  D^t v tenga norma máxima
 * 3) Para el D encontrado se resuelve el sistema laplaciano L x = D^t D x = D^t v
 */ 

public class FormIntegratorExact {
	public class FormIntegratorExactSolution {
		private SparseMatrixInt _incidenceMatrix;
		private VecFloat _x;
		private int _norm;
		
		public FormIntegratorExactSolution(SparseMatrixInt incidenceMatrix, int norm) {
			_incidenceMatrix = incidenceMatrix;
			_norm = norm;
		}
		
		public SparseMatrixInt getIncidenceMatrix() {
			return _incidenceMatrix;
		}

		public int getNorm() {
			return _norm;
		}

		public void solve(LaplacianMatrix laplacianMatrix, VecInt weights) throws Exception {
			VecInt w = _incidenceMatrix.transpose().multiplyByVector(weights);
			_x = ConjugateGradient.execute(laplacianMatrix, w);
		}

		public void verify(VecInt weights) throws Exception {
			float w = _incidenceMatrix.multiplyByVector(_x).subtract(weights).squareNorm();
			//System.out.println(_normOne + " " + w + " _x.squareNorm: " + _x.squareNorm());
			//_x.dump();
		}
		
		public VecFloat getX() {
			return _x;
		}
		
		public void dump() {
			_incidenceMatrix.dump();
			_x.dump();
		}
	}
	
	private WeightedGraph _graph;
	private SparseMatrixInt _transposeIncidenceMatrix;
	private FormIntegratorExactSolution _solution;
	private int idx;
	public FormIntegratorExact(WeightedGraph graph) throws Exception {
		_graph = graph;
		_transposeIncidenceMatrix = _graph.buildDirectedIncidenceMatrix().transpose();
		_solution = new FormIntegratorExactSolution(_transposeIncidenceMatrix.transpose(), 
									_transposeIncidenceMatrix.multiplyByVector(_graph.getEdgeWeights()).squareNorm());
	}

	private void checkCondition() throws Exception {
		//System.out.println(idx++);
		VecInt weights = _graph.getEdgeWeights();
		//long start = System.nanoTime();
		int norm = _transposeIncidenceMatrix.multiplyByVector(weights).squareNorm();
		//System.out.println(System.nanoTime() - start);
		if (norm > _solution.getNorm()) {
			//System.out.println("ACA entre");
			_solution = new FormIntegratorExactSolution(_transposeIncidenceMatrix.transpose(), norm);
		}
	}
	
	private void integrateInternal(int iE) throws Exception {
		if (iE == _graph.getNumberOfEdges()) {
			checkCondition();
		} else {
			integrateInternal(iE+1);

			// invertir la direccion del eje
			//System.out.println("******************");
			//System.out.println("iE: " + iE);
			//_incidenceMatrix.dump();
			_transposeIncidenceMatrix.set(_graph.getVertex0(iE), iE, 1);
			_transposeIncidenceMatrix.set(_graph.getVertex1(iE), iE, -1);
			//System.out.println("******");
			//_incidenceMatrix.dump();
			
			integrateInternal(iE+1);
			
			// hay que deshacer el cambio
			_transposeIncidenceMatrix.set(_graph.getVertex0(iE), iE, -1);
			_transposeIncidenceMatrix.set(_graph.getVertex1(iE), iE, 1);
		}
	}

	public FormIntegratorExactSolution integrate() throws Exception {
		integrateInternal(0);
		_solution.solve(new LaplacianMatrix(_graph), _graph.getEdgeWeights());
		//_solution.verify(_graph.getEdgeWeights());
		return _solution;
	}

	public static void main(String[] args) throws Exception {
		WeightedGraph g = new WeightedGraph(3);
		g.insertEdge(0, 1, 1000);
		g.insertEdge(0, 2, 30);
		g.insertEdge(1, 2, 5);
		FormIntegratorExact s = new FormIntegratorExact(g);
		s.integrate().dump();
	}
}
