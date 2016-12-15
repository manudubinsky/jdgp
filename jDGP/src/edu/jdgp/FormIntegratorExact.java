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
 */ 

public class FormIntegratorExact {
	private WeightedGraph _graph;
	private SparseMatrixInt _transposeIncidenceMatrix;
	private SparseMatrix prueba;
	private VecInt _bestW;
	private float _bestWQuot;

	public FormIntegratorExact(WeightedGraph graph) throws Exception {
		_graph = graph;
		_transposeIncidenceMatrix = _graph.buildDirectedIncidenceMatrix().transpose();
		_bestWQuot = 0;
		//_transposeIncidenceMatrix.dump("INIT");
	}

	private void checkCondition(VecInt w) {
		int sumComponents = 0;
		int squaredNorm2 = 0;
		for (int i = 0; i < w.size(); i++) {
			int v = w.get(i);
			sumComponents += v;
			squaredNorm2 += v * v;
		}
		if (sumComponents < 0)
			sumComponents *= -1;
		float quot = (float)sumComponents/(float)squaredNorm2;
		if (_bestWQuot == 0 || quot < _bestWQuot) {
			_bestWQuot = quot;
			_bestW = w;
			//_transposeIncidenceMatrix.dump();
			prueba = _transposeIncidenceMatrix.toFloat();
			//System.out.println("_bestWQuot: " + _bestWQuot);
			//_bestW.dump();
		}
	}
	
	private void integrateInternal(int iE) {
		if (iE == _graph.getNumberOfEdges()) {
			VecInt w = _transposeIncidenceMatrix.multiplyByVector(_graph.getEdgeWeights());
			checkCondition(w);
		} else {
			integrateInternal(iE+1);

			// invertir la direccion del eje
			//System.out.println("*******");
			//System.out.println("(" + _graph.getVertex0(iE) + "," + iE + ") <-> (" + _graph.getVertex1(iE) + "," + iE + ")");
			//_transposeIncidenceMatrix.dump("ANTES");
			_transposeIncidenceMatrix.set(_graph.getVertex0(iE), iE, 1);
			_transposeIncidenceMatrix.set(_graph.getVertex1(iE), iE, -1);
			//_transposeIncidenceMatrix.dump("DESPUES");
			
			integrateInternal(iE+1);
		}
	}

	public VecFloat integrate() throws Exception {
		integrateInternal(0);
		ConjugateGradient conjugateGradient = new ConjugateGradient();
		VecFloat x = conjugateGradient.execute(_graph.buildLaplacianMatrix(), _bestW);
		prueba.transpose().dump();
		float n = prueba.transpose().multiplyByVector(x).subtract(_graph.getEdgeWeights().toFloat()).squareNorm();
		System.out.println(Math.sqrt(n));
		return x;
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
