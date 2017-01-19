package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.ConjugateGradient;
import edu.jdgp.MethodStats;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Implementacion GRASP del algoritmo para integrar 1-formas
 * Ver: Integration1FormsGRASP.pdf
 */ 

public class FormIntegratorGRASP2 {
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

	public class GRASPSolution {
		private SparseMatrixInt _incidenceMatrix;
		private VecFloat _x;
		private float _norm;
		
		private GRASPSolution() {
			
		}
			
		public GRASPSolution(LaplacianMatrix laplacianMatrix, SparseMatrixInt incidenceMatrix, VecInt edgeWeights) throws Exception {
			_incidenceMatrix = incidenceMatrix;
			//_incidenceMatrix.dump();
			//_stats.begin("GRASPSolution.init");
			_x = ConjugateGradient.execute(laplacianMatrix, incidenceMatrix.transpose().multiplyByVector(edgeWeights));
			//_stats.end("GRASPSolution.init");
			_norm = _incidenceMatrix.multiplyByVector(_x).subtract(edgeWeights).squareNorm();
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

		public void dump() {
			System.out.println("norm: " + _norm);
			_x.dump();
			_incidenceMatrix.dump();
		}

	}
	
	private GRASPParams _params;
	private WeightedGraph _graph;
	private GRASPSolution _current;
	private GRASPSolution _globalBest;
	private LaplacianMatrix _laplacianMatrix;
	private MethodStats _stats;
	
	public FormIntegratorGRASP2(WeightedGraph graph, GRASPParams params, MethodStats stats) throws Exception {
		_stats = stats;
		_graph = graph;
		_params = params;
		_laplacianMatrix = new LaplacianMatrix(graph);
		_laplacianMatrix.compact();
		_current = new GRASPSolution(_laplacianMatrix, _graph.buildDirectedIncidenceMatrix(), _graph.getEdgeWeights());
		_globalBest = _current.clone();
	}

	private VecInt[] generateNeighbors(int alpha, int beta) {
		int totalEdges = _graph.getNumberOfEdges();
		VecInt[] neighbors = new VecInt[beta];
		for (int i = 0; i < beta; i++) {
			neighbors[i] = new VecInt(alpha);
			for (int j = 0; j < alpha; j++) {
				neighbors[i].pushBackUnique(ThreadLocalRandom.current().nextInt(1, totalEdges));
			}
		}
		return neighbors;
	}
	
	private void setBestNeighbor(VecInt[] neighbors) throws Exception {
		GRASPSolution localBest = null;
		SparseMatrixInt incidenceMatrix = _current.getIncidenceMatrix().clone();
		//System.out.println("*INIT ******");
		//incidenceMatrix.dump();
		//System.out.println("*******");
		for (int i = 0; i < neighbors.length; i++) {
			VecInt neighbor = neighbors[i];
			//System.out.println("size: " + neighbor.size());
			for (int j = 0; j < neighbor.size(); j++) {
				int iE = neighbor.get(j);
				//System.out.println("iE: " + iE);
				incidenceMatrix.invertRow(iE);
			}
			//incidenceMatrix.dump();
			//System.out.println("*******");
			//evaluar la solucion que induce el vecino
			GRASPSolution neighborSolution = new GRASPSolution(_laplacianMatrix, incidenceMatrix, _graph.getEdgeWeights());
			
			//registrar el mejor de los vecinos para setearlo al final
			if (localBest == null || neighborSolution.getNorm() < localBest.getNorm()) {
				//System.out.println("ACA!! " + neighborSolution.getNorm());
				localBest = neighborSolution.clone();
			}
			
			//6) revertir el cambio en w para analizar el próximo vecino
			for (int j = 0; j < neighbor.size(); j++) {
				int iE = neighbor.get(j);
				incidenceMatrix.invertRow(iE);
			}
			
		}
		//System.out.println("Antes " + localBest.getNorm() + ", " + _globalBest.getNorm());
		if (localBest.getNorm() < _globalBest.getNorm()) {
			//System.out.println("Entre " + localBest.getNorm() + " < " + _globalBest.getNorm());
			_globalBest = localBest.clone();
			//_globalBest.dump();
			//System.out.println("*******");
		}
		_current = localBest;
	}
	
	public GRASPSolution integrate() throws Exception {
		int i = 0;
		while (i < _params.getMaxIter()) {
			int alpha = ThreadLocalRandom.current().nextInt(0, _params.getAlphaMax()+1);
			int beta = ThreadLocalRandom.current().nextInt(1, _params.getBetaMax() + 1);
			//System.out.println("alpha: " + alpha + " beta: " + beta);
			VecInt[] neighbors = generateNeighbors(alpha, beta);
			setBestNeighbor(neighbors);
			i++;
		}
		return _globalBest;
	}

//-343.33334 331.6667 11.666667
//-343.33334 335.0 8.333334
	public static void main(String[] args) throws Exception {
		WeightedGraph g = new WeightedGraph(3);
		g.insertEdge(0, 1, 1000);
		g.insertEdge(0, 2, 30);
		g.insertEdge(1, 2, 5);
		GRASPParams params = new GRASPParams(2, 1, 10);
		FormIntegratorGRASP2 integ = new FormIntegratorGRASP2(g, params,null);
/*
		VecInt[] n = integ.generateNeighbors(2,1);
		for (int i = 0; i < 1; i++) {
			System.out.println("i: " + i + " size: " + n[i].size());
			n[i].dump();
		}
		*/

		GRASPSolution s = integ.integrate();
		s.dump();
		//float v1 = s.getIncidenceMatrix().multiplyByVector(s.getX()).subtract(g.getEdgeWeights()).squareNorm();
		/*
 		VecFloat v = new VecFloat(3);
		v.pushBack(-343.33334f);v.pushBack(335.0f);v.pushBack(8.333334f);
		float v2 = s.getIncidenceMatrix().multiplyByVector(v).subtract(g.getEdgeWeights()).squareNorm();
		System.out.println("v1: " + v1 + " v2: " + v2);
		*/ 
	}
}
