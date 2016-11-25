package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.CustomSpanningTree.IntComparator;
import edu.jdgp.CustomSpanningTree.IntHeap;
import edu.jdgp.CustomSpanningTree.MaxComparator;
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
		private GRASPSolution _localBest;
		private GRASPSolution[] _localBestN;
		private int _maxSolutionIDX; 
		private IntHeap _heap;
		
		public GRASPLocalCandidatesBestN(int bestN) {
			_heap = new IntHeap(new GRASPSolutionComparator(_localBestN, new MaxComparator()));
			_maxSolutionIDX = 0;
		}
		
		public void addNeighbor(VecInt treeEdges, int treeValue) {
			int currentIDX = -1;
			if (_maxSolutionIDX < _localBestN.length) {
				_localBestN[_maxSolutionIDX] = new GRASPSolution(treeEdges, treeValue);
				_heap.insert(_maxSolutionIDX);
				currentIDX = _maxSolutionIDX;
				_maxSolutionIDX++;
			} else {
				int root = _heap.getRoot();
				if (_localBestN[root].getValue() > treeValue) {
				//sacar la raiz y sustituir su lugar por el árbol actual
					_heap.extract(); 
					_localBestN[root] = new GRASPSolution(treeEdges, treeValue);
					_heap.insert(root);
					currentIDX = root;
				}
			}
			if (currentIDX != -1 && _localBest.getValue() > _localBestN[currentIDX].getValue())
				_localBest = _localBestN[currentIDX];
		}
		
		public GRASPSolution getLocalBest() {
			return _localBest;
		}
	}

	public static abstract class GRASPStrategy {
		protected SpanningTreesMatsui _matsui;
		protected GRASPSolution _globalBest;

		public GRASPSolution execute(int maxIter) throws Exception {
			int iter = 0;
			while (iter < maxIter) {
				processNeighborhood();
				iter++;
			}
			return getGlobalBest();
		}

		public GRASPSolution getGlobalBest() {
			return _globalBest;
		}
		
		protected int evaluateNeighbor() {
			return 1;
		}

		protected abstract void processNeighborhood() throws Exception;
	}

	public static class GRASPStrategyBestN extends GRASPStrategy {
		private int _bestN;
		
		public GRASPStrategyBestN(Graph g, int bestN) {
			_matsui = new SpanningTreesMatsui(g);
			_bestN = bestN;
		}
		
		protected void processNeighborhood() throws Exception {
			GRASPLocalCandidatesBestN localBestN = new GRASPLocalCandidatesBestN(_bestN);
			VecInt pivotEdges = _matsui.getPivotEdges();
			for (int i = 0; i < pivotEdges.size(); i++) {
				int pivotEdge = pivotEdges.get(i);
				VecInt cycleEdges = _matsui.getCycleEdges(pivotEdge);
				for (int j = 0; j < cycleEdges.size(); j++) {
					int cycleEdge = cycleEdges.get(j); 
					_matsui.setEdge(cycleEdge, pivotEdge);
					int neighborValue = evaluateNeighbor();
					localBestN.addNeighbor(_matsui.getTreeEdges(), neighborValue);
					_matsui.setEdge(cycleEdge, cycleEdge);
				}
			}
			GRASPSolution localBest = localBestN.getLocalBest();
			if (_globalBest.getValue() > localBest.getValue())
				_globalBest = localBest;
		}
	}
	
	public static void main(String[] args) throws Exception {
		for (int i = 0; i < 10; i++) {
			System.out.println(ThreadLocalRandom.current().nextInt(1, 10 + 1));
		}
	}

}
