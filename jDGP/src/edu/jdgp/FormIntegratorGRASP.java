package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.ConjugateGradient;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Implementacion GRASP del algoritmo para integrar 1-formas
 * Ver: Integration1FormsGRASP.pdf
 */ 

public class FormIntegratorGRASP {
	private WeightedGraph _graph;
	private SparseMatrixInt _transposeIncidenceMatrix;
	private VecInt _bestW;
	private float _bestWQuot;
	private int _alphaMax;
	private int _betaMax;

	public FormIntegratorGRASP(WeightedGraph graph, int alphaMax, int betaMax) throws Exception {
		_graph = graph;
		_transposeIncidenceMatrix = _graph.buildDirectedIncidenceMatrix().transpose();
		_bestWQuot = 0;
		_alphaMax = alphaMax;
		_betaMax = betaMax;
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
			//System.out.println("_bestWQuot: " + _bestWQuot);
			//_bestW.dump();
		}
	}

	public VecInt[] generateNeighbors(int alpha, int beta) {
		int totalEdges = _graph.getNumberOfEdges();
		VecInt[] neighbors = new VecInt[beta];
		for (int i = 0; i < beta; i++) {
			neighbors[i] = new VecInt(alpha);
			for (int j = 0; j < alpha; j++) {
				neighbors[i].pushBack(ThreadLocalRandom.current().nextInt(1, totalEdges + 1));
			}
		}
		return neighbors;
	}
	
	private void integrateInternal(int maxIter) {
		int i = 0;
		while (i < maxIter) {
			int alpha = ThreadLocalRandom.current().nextInt(1, _alphaMax + 1);
			int beta = ThreadLocalRandom.current().nextInt(1, _betaMax + 1);
			VecInt[] neigbors = generateNeighbors(alpha, beta);
			//setNextPoint(neigbors);
			i++;
		}
	}

	public VecFloat integrate(int maxIter, int alphaMax, int betaMax) throws Exception {
		integrateInternal(10000);
		ConjugateGradient conjugateGradient = new ConjugateGradient();
		VecFloat x = conjugateGradient.execute(_graph.buildLaplacianMatrix(), _bestW);
		return x;
	}

	public static void main(String[] args) throws Exception {
		WeightedGraph g = new WeightedGraph(3);
		g.insertEdge(0, 1, 1000);
		g.insertEdge(0, 2, 30);
		g.insertEdge(1, 2, 5);
		FormIntegratorGRASP s = new FormIntegratorGRASP(g, 5, 3);
		/*
		VecInt[] n = s.generateNeighbors(5,5);
		for (int i = 0; i < 5; i++) {
			n[i].dump();
		}
		*/ 
		//s.integrate().dump();
	}
}
