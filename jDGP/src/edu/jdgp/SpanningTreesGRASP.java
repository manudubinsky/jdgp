package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.CustomSpanningTree.IntComparator;
import edu.jdgp.CustomSpanningTree.IntHeap;
import edu.jdgp.CustomSpanningTree.MinComparator;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import java.util.concurrent.ThreadLocalRandom;
import edu.jdgp.SpanningTreesMatsui;

/*
 * Implementacion de metaheurística GRASP para encontrar el árbol 
 * generador de un grafo que optimice cierta propiedad
 * 
 * La vecindad de un árbol generador es la descripta en la 
 * implementacion del algoritmo exacto de Matsui para generar todos los 
 * árboles generadores de un grafo.  Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * 
 * */
public class SpanningTreesGRASP {
	public static class GRASPSolution {
		private VecInt _treeEdges;
		private int _value;
		
		public GRASPSolution(VecInt treeEdges, int value) {
			_treeEdges = treeEdges.clone();
			_value =value;
		}
		
		public int getValue() {
			return _value;
		}
		
		public void dump() {
			System.out.println("value: " + _value);
			_treeEdges.dump();
		}
	}

	public static class GRASPLocalSolution extends GRASPSolution {
		private int _pivotEdge; //el switch de ejes de esta solución respecto al padre
		private int _cycleEdge;
		public GRASPLocalSolution(VecInt treeEdges, int value, int pivotEdge, int cycleEdge) {
			super(treeEdges, value);
			_pivotEdge = pivotEdge;
			_cycleEdge = cycleEdge;
		}

		public int getPivotEdge() {
			return _pivotEdge;
		}

		public int getCycleEdge() {
			return _cycleEdge;
		}

	}
	
	public static class GRASPSolutionComparator implements IntComparator {
		private GRASPSolution[] values;
		private IntComparator comparator;
		
		public GRASPSolutionComparator(GRASPSolution[] values, IntComparator comparator) {
			this.values = values;
			this.comparator = comparator;
		}
		
		public int compare(int v1, int v2) {
			return comparator.compare(values[v1].getValue(), values[v2].getValue());
		}
		
		public int getElemValue(int v) {
			return values[v].getValue();
		}
	}

	public static class GRASPLocalCandidatesBestN {
		private GRASPLocalSolution _localBest;
		private GRASPLocalSolution[] _localBestN;
		private int _maxSolutionIDX; 
		private IntHeap _heap;
		
		public GRASPLocalCandidatesBestN(int bestN) {
			//System.out.println("bestN: " + bestN);			
			_localBestN = new GRASPLocalSolution[bestN];
			_heap = new IntHeap(new GRASPSolutionComparator(_localBestN, new MinComparator()));
			_maxSolutionIDX = 0;
		}
		
		public void addNeighbor(VecInt treeEdges, int treeValue, int pivotEdge, int cycleEdge) {
			int currentIDX = -1;
			//System.out.println("_localBestN.length: " + _localBestN.length);
			if (_maxSolutionIDX < _localBestN.length) {
				_localBestN[_maxSolutionIDX] = new GRASPLocalSolution(treeEdges, treeValue, pivotEdge, cycleEdge);
				_heap.insert(_maxSolutionIDX);
				currentIDX = _maxSolutionIDX;
				_maxSolutionIDX++;
			} else {
				//System.out.println("ACA 1");
				int root = _heap.getRoot();
				//System.out.println("ACA 2");
				if (_localBestN[root].getValue() > treeValue) {
				//sacar la raiz y sustituir su lugar por el árbol actual
					_heap.extract(); 
					_localBestN[root] = new GRASPLocalSolution(treeEdges, treeValue, pivotEdge, cycleEdge);
					_heap.insert(root);
					currentIDX = root;
				}
			}
			if (currentIDX != -1) {
				if (_localBest == null || _localBest.getValue() > _localBestN[currentIDX].getValue()) {
					_localBest = _localBestN[currentIDX];
				}
			}
		}
		
		public GRASPLocalSolution getLocalBest() {
			return _localBest;
		}
	}

	public static abstract class GRASPStrategy {
		
		protected GRASPSolution _globalBest;

		public GRASPSolution execute(int maxIter) throws Exception {
			int iter = 0;
			while (iter < maxIter) {
				processNeighborhood();
				System.out.println("iter: " + iter + " value: " + _globalBest.getValue());
				iter++;
			}
			return getGlobalBest();
		}

		public GRASPSolution getGlobalBest() {
			return _globalBest;
		}
		
		protected abstract int evaluateNeighbor();

		protected abstract void processNeighborhood() throws Exception;
	}

	public static class GRASPStrategyBestN extends GRASPStrategy {
		private WeightedGraph _graph;
		private SpanningTreesMatsui _matsui;
		private int _bestN;
		
		public GRASPStrategyBestN(Graph g, int bestN) {
			_graph = (WeightedGraph)g;
			_matsui = new SpanningTreesMatsui(g);
			_bestN = bestN;
		}
		
		protected int evaluateNeighbor() {
			int value = 0;
			VecInt edgeWeights = _graph.getEdgeWeights();
			VecInt treeEdges = _matsui.getTreeEdges();
			for (int i = 0; i < treeEdges.size(); i++) {
				int iE = _matsui.getEdgeIdx(treeEdges.get(i));
				value += edgeWeights.get(iE);
			}
			return value;
		}
		
		protected void processNeighborhood() throws Exception {
			GRASPLocalCandidatesBestN localBestN = new GRASPLocalCandidatesBestN(_bestN);
			VecInt pivotEdges = _matsui.getPivotEdges();
			//_matsui.getTreeEdges().dump();
			System.out.println("#pivotEdges: " + pivotEdges.size());
			for (int i = 0; i < pivotEdges.size(); i++) {
				int pivotEdge = pivotEdges.get(i);
				VecInt cycleEdges = _matsui.getCycleEdges(pivotEdge);
				for (int j = 0; j < cycleEdges.size(); j++) {
					// System.out.println("i: " + i + " j:" + j);
					int cycleEdge = cycleEdges.get(j); 
					_matsui.setEdge(cycleEdge, pivotEdge);
					int neighborValue = evaluateNeighbor();
					//System.out.println("neighborValue: " + neighborValue);
					localBestN.addNeighbor(_matsui.getTreeEdges(), neighborValue, pivotEdge, cycleEdge);
					_matsui.setEdge(cycleEdge, cycleEdge);
				}
			}
			GRASPLocalSolution localBest = localBestN.getLocalBest();
			if (localBest != null) {
				_matsui.setEdge(localBest.getCycleEdge(), localBest.getPivotEdge()); // setear el árbol al mejor local
				if (_globalBest == null || _globalBest.getValue() > localBest.getValue())
					_globalBest = localBest;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		/* for (int i = 0; i < 10; i++) {
			System.out.println(ThreadLocalRandom.current().nextInt(1, 10 + 1));
		} */
		WeightedGraph g = new WeightedGraph(100);
		int nV = g.getNumberOfVertices();
		for (int i = 0; i < nV; i++) {
			for (int j = i+1; j < nV; j++) {
				int v = i % 2 == 1 && j % 2 == 1 ? 1 :	// los dos impares -> 1
						i % 2 == 0 && j % 2 == 0 ? 3 :	// los dos pares -> 
						2;								// si no -> 2
				//System.out.println("i: " + i + " j: " + j + " v:" + v);
				g.insertEdge(i, j, v);
			}
		}
		//System.out.println("g.getNumberOfEdges(): " + g.getNumberOfEdges());
		GRASPStrategyBestN s = new GRASPStrategyBestN(g, 5);
		s.execute(80).dump();
	}

}
