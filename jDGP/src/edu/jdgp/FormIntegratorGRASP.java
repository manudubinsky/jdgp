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
import edu.jdgp.WeightedGraphBuilder;
import edu.jdgp.WeightedGraphBuilder.ExactForm;
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

	public class GRASPSolution {
		private SparseMatrixInt _transposeIncidenceMatrix;
		private VecInt _w;
		private VecFloat _x;
		private int _norm;
		private float _verif;
		
		public GRASPSolution(SparseMatrixInt transposeIncidenceMatrix, VecInt w) {
			this(transposeIncidenceMatrix, w, w.squareNorm());
		}

		public GRASPSolution(SparseMatrixInt transposeIncidenceMatrix, VecInt w, int norm) {
			_transposeIncidenceMatrix = transposeIncidenceMatrix;
			_w = w;
			_norm = norm;			
		}
		
		public SparseMatrixInt getIncidenceMatrix() {
			return _transposeIncidenceMatrix;
		}

		public VecInt getW() {
			return _w;
		}

		public void calculateW(VecInt weights) {
			_w = _transposeIncidenceMatrix.multiplyByVector(weights);
		}

		public int getNorm() {
			return _norm;
		}

		public void setNorm(int norm) { // para no tener que recalcularla permitimos setear
			_norm = norm;
		}

		public void solve(LaplacianMatrix laplacianMatrix, VecInt weights) throws Exception {
			VecInt w = _transposeIncidenceMatrix.multiplyByVector(weights);
			//System.out.println("ACA INIT !!!!!!!!!!!");
			//_transposeIncidenceMatrix.dump();
			//weights.dump();
			//System.out.println("ACA END!!!!!!!!!!!");
			_x = ConjugateGradient.execute(laplacianMatrix, w);
			_verif = -1;
		}

		public float verify(VecInt weights) throws Exception {
			if (_verif == -1)
				_verif = _transposeIncidenceMatrix.transpose().multiplyByVector(_x).subtract(weights).squareNorm();
			return _verif;
		}
		
		public VecFloat getX() {
			return _x;
		}

		public GRASPSolution clone() {
			return new GRASPSolution(_transposeIncidenceMatrix.clone(), _w.clone(), _norm);
		}

		public void dump() {
			System.out.println("norm: " + _norm);
			_transposeIncidenceMatrix.transpose().dump();
			_w.dump();
			if (_x != null) {
				_x.dump();
			}
		}

	}
	
	private GRASPParams _params;
	private WeightedGraph _graph;
	private GRASPSolution _current;
	private GRASPSolution _globalBest;
	private int[] _deltaVec; //un vector auxiliar para evitar crearlo en cada iteracion
	private int[] _degrees;
	private MethodStats _stats;
	
	public FormIntegratorGRASP(WeightedGraph graph, GRASPParams params, MethodStats stats) throws Exception {
		_stats = stats;
		_graph = graph;
		_params = params;
		_degrees = graph.getDegrees();
		SparseMatrixInt transpose = _graph.buildDirectedIncidenceMatrix().transpose();
		_current = new GRASPSolution(transpose, transpose.multiplyByVector(_graph.getEdgeWeights()));
		_globalBest = _current.clone();
		_deltaVec = new int[_graph.getNumberOfVertices()];
	}

	private VecInt[] generateNeighbors(int alpha, int beta) {
		int totalEdges = _graph.getNumberOfEdges();
		VecInt[] neighbors = new VecInt[beta];
		for (int i = 0; i < beta; i++) {
			neighbors[i] = new VecInt(alpha);
			for (int j = 0; j < alpha; j++) {
				neighbors[i].pushBackUnique(ThreadLocalRandom.current().nextInt(1, totalEdges));
			}
			//System.out.println("***** i: " + i);
			//neighbors[i].dump();
		}
		return neighbors;
	}
	
	private int check(VecInt neighbor) {
		SparseMatrixInt transpose = _current.getIncidenceMatrix();
		for (int i = 0; i < neighbor.size(); i++) {
			int iE = neighbor.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			transpose.invert(iV0, iE);
			transpose.invert(iV1, iE);
		}
		int v = transpose.multiplyByVector(_graph.getEdgeWeights()).squareNorm();
		for (int i = 0; i < neighbor.size(); i++) {
			int iE = neighbor.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			transpose.invert(iV0, iE);
			transpose.invert(iV1, iE);
		}
		return v;
	}
		
	private void setBestNeighbor(VecInt[] neighbors) throws Exception {
		//_stats.start("setBestNeighbor");
		VecInt edgesWeights = _graph.getEdgeWeights();
		int bestNorm = -1;
		int best = -1;
		SparseMatrixInt transpose = _current.getIncidenceMatrix();
		VecInt vertexIndexes = new VecInt(2 * _params.getAlphaMax());
		VecInt w = _current.getW();
		for (int i = 0; i < neighbors.length; i++) {
			VecInt neighbor = neighbors[i];
			vertexIndexes._reset();
			for (int j = 0; j < neighbor.size(); j++) {
				// 1) conseguir el peso del eje que se quiere invertir
				int iE = neighbor.get(j);
				int edgeWeight = edgesWeights.get(iE);
				
				int iV0 = _graph.getVertex0(iE);
				int iV1 = _graph.getVertex1(iE);
				
				//2) registrar los vertices involucrados
				vertexIndexes.pushBackUnique(iV0);
				vertexIndexes.pushBackUnique(iV1);

				//4) obtener la direccion del eje 
				int edgeDir0 = transpose.get(iV0, iE);
				int edgeDir1 = transpose.get(iV1, iE);
				
				//System.out.println("edgeWeight: " + edgeWeight);
				//System.out.println("ANTES ! _deltaVec["+iV0+"]: " + _deltaVec[iV0] + " _deltaVec["+iV1+"]: " + _deltaVec[iV1]);

				//5) calcular delta de w
				_deltaVec[iV0] += edgeDir0 == -1 ? 2 * edgeWeight : -2 * edgeWeight;
				_deltaVec[iV1] += edgeDir1 == -1 ? 2 * edgeWeight : -2 * edgeWeight;
				
				//System.out.println("DESPUES ! _deltaVec["+iV0+"]: " + _deltaVec[iV0] + " _deltaVec["+iV1+"]: " + _deltaVec[iV1]);
			}

			int neighborNorm = _current.getNorm();
			//System.out.println("*****ANTES neighborNorm: " + neighborNorm);
			//vertexIndexes.dump();
			for (int j = 0; j < vertexIndexes.size(); j++) {
				int iV = vertexIndexes.get(j);
				int vertexValue = w.get(iV);
				//System.out.println("<1> _deltaVec["+iV+"]: " + _deltaVec[iV] + " vertexValue: " + vertexValue + " neighborNorm: " + neighborNorm);
				//System.out.println("ANTES vertexValue: " + vertexValue);
				neighborNorm -= vertexValue * vertexValue;
				//System.out.println("ANTES neighborNorm: " + neighborNorm);
				vertexValue += _deltaVec[iV];
				//System.out.println("DESPUES vertexValue: " + vertexValue);
				neighborNorm += vertexValue * vertexValue;
				//System.out.println("<2> _deltaVec["+iV+"]: " + _deltaVec[iV] + " vertexValue: " + vertexValue + " neighborNorm: " + neighborNorm);
				//System.out.println("DESPUES neighborNorm: " + neighborNorm);
				_deltaVec[iV] = 0; // resetear para la proxima iteracion
			}
			//System.out.println("******DESPUES sneighborNorm: " + neighborNorm);
			if (best == -1 || neighborNorm > bestNorm) {
				best = i;
				bestNorm = neighborNorm;
			}
		}
		//6) setear como siguiente al mejor vecino
		//_current.dump();
		_current.setNorm(bestNorm);
		VecInt neighbor = neighbors[best];
		for (int j = 0; j < neighbor.size(); j++) {
			int iE = neighbor.get(j);
			int edgeWeight = edgesWeights.get(iE);

			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);

			// invertir la direccion del eje
			transpose.invert(iV0, iE);
			transpose.invert(iV1, iE);
		}
		_current.calculateW(edgesWeights);
		//System.out.println("ACA!! " + bestNorm + " " + _current.getW().squareNorm());
		//7) verificar si mejoro globalmente
		//System.out.println("******FIN bestNorm: " + bestNorm + " global: " + _globalBest.getNorm());
		if (bestNorm > _globalBest.getNorm()) {
			GRASPSolution auxSolution = _current.clone();
			auxSolution.solve(_auxLaplacian, edgesWeights);
			if (_globalBest.getX() == null || auxSolution.verify(edgesWeights) < _globalBest.verify(edgesWeights)) {
				_globalBest = auxSolution;
				System.out.println("ACA " + bestNorm + " " + _globalBest.verify(edgesWeights));
			}
		}
		//_stats.stop("setBestNeighbor");
	}
	
	public GRASPSolution integrate() throws Exception {
		int i = 0;
		while (i < _params.getMaxIter()) {
			int alpha = _params.getAlphaMax(); // ThreadLocalRandom.current().nextInt(0, _params.getAlphaMax()+1);
			int beta = _params.getBetaMax();   //ThreadLocalRandom.current().nextInt(1, _params.getBetaMax() + 1);
			
			//System.out.println("alpha: " + alpha + " beta: " + beta);
			VecInt[] neighbors = generateNeighbors(alpha, beta);
			setBestNeighbor(neighbors);
			i++;
		}
		LaplacianMatrix mm = new LaplacianMatrix(_graph);
		_globalBest.solve(mm, _graph.getEdgeWeights());
		//System.out.println("ACSAAAA 1: " +  _globalBest.verify(_graph.getEdgeWeights()));
		mm.compact();
		_globalBest.solve(mm, _graph.getEdgeWeights());
		//System.out.println("ACSAAAA 2: " +  _globalBest.verify(_graph.getEdgeWeights()));

		return _globalBest;
	}

//-343.33334 331.6667 11.666667
//-343.33334 335.0 8.333334
/*
	public static void main(String[] args) throws Exception {
		WeightedGraph g = new WeightedGraph(3);
		g.insertEdge(0, 1, 1000);
		g.insertEdge(0, 2, 30);
		g.insertEdge(1, 2, 5);
		GRASPParams params = new GRASPParams(2, 1, 10);
		FormIntegratorGRASP integ = new FormIntegratorGRASP(g, params,null);

		GRASPSolution s = integ.integrate();
		s.dump();
		System.out.println(s.verify(g.getEdgeWeights()));
	}
	*/ 
	public static void main(String[] args) throws Exception {
		Graph g = Graph.buildCompleteGraph(100);
		for (int j = 0; j < 50; j++) {
			//System.out.println("***************CASO 1");
			
			ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
			GRASPParams params = new GRASPParams(10, 2, 10);
			FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, null);
			GRASPSolution s = integ.integrate();
			VecInt weights = form.getWeightedGraph().getEdgeWeights();
			//s.dump();
			System.out.println(s.verify(form.getWeightedGraph().getEdgeWeights()));
/*
			System.out.println("***************CASO 2");
			
			params = new GRASPParams(2, 10, 5);
			integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, null);
			s = integ.integrate();
			//s.dump();
			System.out.println(s.verify(form.getWeightedGraph().getEdgeWeights()));
*/
		}
	}

}
