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
 * 2) Resolvemos por conjugate gradient: L x = D^t D x = D^t v
 * 3) Verificamos si el "x" del paso 2) es el mejor: argmin_x(||v - D x||^2)
 */ 

public class FormIntegratorExact2 {
	private WeightedGraph _graph;
	private SparseMatrixInt _incidenceMatrix;
	private SparseMatrixInt _bestIncidenceMatrix;
	private SparseMatrixInt _laplacianMatrix;
	private VecFloat _bestX;
	private float _bestXNorm;

	public FormIntegratorExact2(WeightedGraph graph) throws Exception {
		_graph = graph;
		_incidenceMatrix = _graph.buildDirectedIncidenceMatrix();		
		_laplacianMatrix = _graph.buildLaplacianMatrix();
	}

	private void checkCondition() throws Exception {
		VecInt weights = _graph.getEdgeWeights();
		SparseMatrixInt _transposeIncidenceMatrix = _incidenceMatrix.transpose();
		VecInt w = _transposeIncidenceMatrix.multiplyByVector(weights);
		VecFloat x = ConjugateGradient.execute(_laplacianMatrix, w);
		float norm = _incidenceMatrix.multiplyByVector(x).subtract(weights).squareNorm();
		//_incidenceMatrix.dump();
		//System.out.println("*******");
		//w.dump();
		if (_bestX == null || norm < _bestXNorm) {
			//System.out.println(norm + " < " + _bestXNorm);
			//_incidenceMatrix.dump();
			//w.dump();
			//x.dump();
			_bestXNorm = norm;
			_bestX = x;
			_bestIncidenceMatrix = _incidenceMatrix.clone();
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
			_incidenceMatrix.set(iE, _graph.getVertex0(iE), 1);
			_incidenceMatrix.set(iE, _graph.getVertex1(iE), -1);
			//System.out.println("******");
			//_incidenceMatrix.dump();
			
			integrateInternal(iE+1);
			
			// hay que deshacer el cambio
			_incidenceMatrix.set(iE, _graph.getVertex0(iE), -1);
			_incidenceMatrix.set(iE, _graph.getVertex1(iE), 1);
		}
	}

	public VecFloat integrate() throws Exception {
		integrateInternal(0);
		//_bestIncidenceMatrix.dump();
		//_incidenceMatrix.dump();
		return _bestX;
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
