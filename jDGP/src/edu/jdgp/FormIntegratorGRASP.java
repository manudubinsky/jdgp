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
		private VecInt _w;
		private int _sumWi;
		private long  _squaredNormW;

		public GRASPSolution() {
		}
		
		public GRASPSolution(VecInt w) {
			_w = w;
			init();
		}

		private void init() {
			_sumWi = 0;
			_squaredNormW = 0;
			for (int i = 0; i < _w.size(); i++) {
				int v = _w.get(i);
				_sumWi += v;
				_squaredNormW += v * v;
			}
			if (_sumWi < 0)
				_sumWi *= -1;			
		}
		
		public VecInt getW() {
			return _w;
		}
		
		public long getSquareNorm() {
			return _squaredNormW;
		}

		public void setSquareNorm(long norm) {
			_squaredNormW = norm;
		}
		
		public GRASPSolution clone() {
			GRASPSolution copy = new GRASPSolution();
			copy._w = _w.clone();
			copy._sumWi = _sumWi;
			copy._squaredNormW = _squaredNormW;
			return copy;
		}

		public void dump() {
			_w.dump();
		}
	}

	private GRASPParams _params;
	private WeightedGraph _graph;
	private GRASPSolution _currentW;
	private GRASPSolution _globalBest;
	private SparseMatrixInt _transposeIncidenceMatrix;
	

	public FormIntegratorGRASP(WeightedGraph graph, GRASPParams params) throws Exception {
		_graph = graph;
		_params = params;
		_transposeIncidenceMatrix = _graph.buildDirectedIncidenceMatrix().transpose();
		_currentW = new GRASPSolution(_transposeIncidenceMatrix.multiplyByVector(_graph.getEdgeWeights()));
		_globalBest = _currentW.clone();
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
	
	private void setBestNeighbor(VecInt[] neighbors) {
		VecInt currW = _currentW.getW();
		VecInt edgesWeights = _graph.getEdgeWeights();
		long bestNorm2 = -1;
		int best = -1;
		for (int i = 0; i < neighbors.length; i++) {
			VecInt neighbor = neighbors[i];
			long neighborNorm2 = _currentW.getSquareNorm();
			for (int j = 0; j < neighbor.size(); j++) {
				int iE = neighbor.get(j);
				int edgeWeight = edgesWeights.get(iE);

				int iV0 = _graph.getVertex0(iE);
				int iV1 = _graph.getVertex1(iE);

				int w0 = currW.get(iV0);
				int w1 = currW.get(iV1);

				//1) restar las componentes que queremos recalcular
				neighborNorm2 -= (w0 * w0 + w1 * w1);

				//2) obtener la direccion del eje 
				int edgeDir0 = _transposeIncidenceMatrix.get(iV0, iE);
				int edgeDir1 = _transposeIncidenceMatrix.get(iV1, iE);

				//3) sumar las componenetes recalculadas (w = D^t v)
				w0 += edgeDir0 == -1 ? 2 * edgeWeight : -2 * edgeWeight;
				w1 += edgeDir1 == -1 ? 2 * edgeWeight : -2 * edgeWeight;

				neighborNorm2 += w0 * w0 + w1 * w1;
			}
			if (best == -1 || neighborNorm2 > bestNorm2) { //queremos minimizar sum{w_i}/||w||_2^2
				best = i;
				bestNorm2 = neighborNorm2;
			}
		}
		setCurrentW(best, neighbors);
		if (_currentW.getSquareNorm() > _globalBest.getSquareNorm()) {
			_globalBest = _currentW.clone();
		}
	}

	private void setCurrentW(int bestNeigbor, VecInt[] neigbors) {
		VecInt neighbor = neigbors[bestNeigbor];
		VecInt currW = _currentW.getW();
		VecInt edgesWeights = _graph.getEdgeWeights();
		long neighborNorm2 = _currentW.getSquareNorm();
		for (int j = 0; j < neighbor.size(); j++) {
				int iE = neighbor.get(j);
				int edgeWeight = edgesWeights.get(iE);

				int iV0 = _graph.getVertex0(iE);
				int iV1 = _graph.getVertex1(iE);			

				int w0 = currW.get(iV0);
				int w1 = currW.get(iV1);

				neighborNorm2 -= (w0 * w0 + w1 * w1);

				int edgeDir0 = _transposeIncidenceMatrix.get(iV0, iE);
				int edgeDir1 = _transposeIncidenceMatrix.get(iV1, iE);

				w0 += edgeDir0 == -1 ? 2 * edgeWeight : -2 * edgeWeight;
				w1 += edgeDir1 == -1 ? 2 * edgeWeight : -2 * edgeWeight;

				neighborNorm2 += w0 * w0 + w1 * w1;

				_currentW.setSquareNorm(neighborNorm2);

				currW.set(iV0, w0);
				currW.set(iV1, w1);

				_transposeIncidenceMatrix.invert(iV0, iE);
				_transposeIncidenceMatrix.invert(iV1, iE);
		}
	}
	
	private void integrateInternal() {
		int i = 0;
		while (i < _params.getMaxIter()) {
			int alpha = ThreadLocalRandom.current().nextInt(1, _params.getAlphaMax() + 1);
			int beta = ThreadLocalRandom.current().nextInt(1, _params.getBetaMax() + 1);
			VecInt[] neigbors = generateNeighbors(alpha, beta);
			setBestNeighbor(neigbors);
			i++;
		}
	}

	public VecFloat integrate() throws Exception {
		integrateInternal();
		ConjugateGradient conjugateGradient = new ConjugateGradient();
		VecFloat x = conjugateGradient.execute(_graph.buildLaplacianMatrix(), _globalBest.getW());
		return x;
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
