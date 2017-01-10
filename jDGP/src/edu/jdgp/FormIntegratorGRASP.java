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
	public static class GRASPParams {
		private int _alphaMax;
		private int _betaMax;
		private int _maxIter;
		
		public GRASPParams(int alphaMax, int betaMax, int maxIter) {
			_alphaMax = alphaMax;
			_betaMax = betaMax;
			_maxIter = maxIter;
		}
		
		public int getAlphaMax() {
			return _alphaMax;
		}
		
		public int getBetaMax() {
			return _betaMax;
		}

		public int getMaxIter() {
			return _maxIter;
		}		
	}

	public static class GRASPSolution {
		private SparseMatrixInt _incidenceMatrix;
		private VecFloat _x;
		private float _norm;
		
		private GRASPSolution() {
			
		}
			
		public GRASPSolution(LaplacianMatrix laplacianMatrix, SparseMatrixInt incidenceMatrix, VecInt edgeWeights) throws Exception {
			_incidenceMatrix = incidenceMatrix;
			VecFloat x = ConjugateGradient.execute(laplacianMatrix, incidenceMatrix.transpose().multiplyByVector(edgeWeights));
			float norm = _incidenceMatrix.multiplyByVector(x).subtract(edgeWeights).squareNorm();
		}
		
		public float getNorm() {
			return _norm;
		}
		
		public SparseMatrixInt getIncidenceMatrix() {
			return _incidenceMatrix;
		}

		public VecFloat getX() {
			return _x;
		}

		public GRASPSolution clone() {
			GRASPSolution copy = new GRASPSolution();
			copy._incidenceMatrix = _incidenceMatrix.clone();
			copy._x = _x.clone();
			copy._norm = _norm;
			return copy;
		}

	}
	
	private GRASPParams _params;
	private WeightedGraph _graph;
	private GRASPSolution _current;
	private GRASPSolution _globalBest;
	private LaplacianMatrix _laplacianMatrix;
	
	public FormIntegratorGRASP(WeightedGraph graph, GRASPParams params) throws Exception {
		_graph = graph;
		_params = params;
		_laplacianMatrix = new LaplacianMatrix(graph);
		_laplacianMatrix.compact();
		_current = new GRASPSolution(_laplacianMatrix, _graph.buildDirectedIncidenceMatrix(), _graph.getEdgeWeights());
	}

	private VecInt[] generateNeighbors(int alpha, int beta) {
		int totalEdges = _graph.getNumberOfEdges();
		VecInt[] neighbors = new VecInt[beta];
		for (int i = 0; i < beta; i++) {
			neighbors[i] = new VecInt(alpha);
			for (int j = 0; j < alpha; j++) {
				neighbors[i].pushBackUnique(ThreadLocalRandom.current().nextInt(1, totalEdges + 1));
			}
		}
		return neighbors;
	}
	
	private void setBestNeighbor(VecInt[] neighbors) throws Exception {
		GRASPSolution localBest = null;
		SparseMatrixInt incidenceMatrix = _current.getIncidenceMatrix().clone();
		for (int i = 0; i < neighbors.length; i++) {
			VecInt neighbor = neighbors[i];
			for (int j = 0; j < neighbor.size(); j++) {
				int iE = neighbor.get(j);
				incidenceMatrix.invertRow(iE);
			}
			//evaluar la solucion que induce el vecino
			GRASPSolution neighborSolution = new GRASPSolution(_laplacianMatrix, incidenceMatrix, _graph.getEdgeWeights());
			
			//registrar el mejor de los vecinos para setearlo al final
			if (localBest == null || neighborSolution.getNorm() < localBest.getNorm()) {
				localBest = neighborSolution.clone();
			}
			
			//6) revertir el cambio en w para analizar el próximo vecino
			for (int j = 0; j < neighbor.size(); j++) {
				int iE = neighbor.get(j);
				incidenceMatrix.invertRow(iE);
			}
			
		}
		if (_globalBest == null || localBest.getNorm() < _globalBest.getNorm()) {
			_globalBest = localBest.clone();
		}
	}
	
	private void integrateInternal() throws Exception {
		int i = 0;
		while (i < _params.getMaxIter()) {
			int alpha = ThreadLocalRandom.current().nextInt(1, _params.getAlphaMax() + 1);
			int beta = ThreadLocalRandom.current().nextInt(1, _params.getBetaMax() + 1);
			VecInt[] neigbors = generateNeighbors(alpha, beta);
			setBestNeighbor(neigbors);
			i++;
		}
	}

	public GRASPSolution integrate() throws Exception {
		integrateInternal();
		return _globalBest;
	}

	public static void main(String[] args) throws Exception {
		GRASPParams params = new GRASPParams(5, 5, 10000);
		WeightedGraph g = new WeightedGraph(3);
		g.insertEdge(0, 1, 1000);
		g.insertEdge(0, 2, 30);
		g.insertEdge(1, 2, 5);
		FormIntegratorGRASP s = new FormIntegratorGRASP(g, params);
		/*
		VecInt[] n = s.generateNeighbors(5,5);
		for (int i = 0; i < 5; i++) {
			n[i].dump();
		}
		*/ 
		//s.integrate().dump();
	}
}
