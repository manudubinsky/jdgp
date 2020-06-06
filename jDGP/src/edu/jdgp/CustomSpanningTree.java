package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.PartitionUnionFind;

public class CustomSpanningTree {
	public static class WeightedGraph extends Graph  { 
		private VecInt _edgeWeights;
		private int _totalWeight;
		
		public WeightedGraph(int N) {
			super(N);
			_totalWeight = 0;
		}

		public WeightedGraph(Graph graph, VecInt weights) {
			super();
			_edges = graph._edges;
			_vertexEdges = graph._vertexEdges;
			_nV = graph._nV;
			_nE = graph._nE;
			_edgeWeights = weights;
		}

		public void erase() {
			super.erase();
			_edgeWeights = new VecInt(getNumberOfVertices()); // estimativamente lo creo del tamano de la cantidad de nodos 
		}

		public int insertEdge(int iV0, int iV1, int weight) {
			_totalWeight += weight;
			_edgeWeights.pushBack(weight);
			return super.insertEdge(iV0, iV1);
		}

		public VecInt getEdgeWeights() {
			return _edgeWeights;
		}

		public int getTotalWeight() {
			return _totalWeight;
		}
		
		public void dump() {
			for (int i = 0; i < _nE; i++) {
				int iV0 = _edges.get(2 * i);
				int iV1 = _edges.get(2 * i + 1);
				System.out.println(iV0 + " -> " + iV1 + " (" + _edgeWeights.get(i)  + ")");
			} 
		}	
	}

	public static class IntHeap { 
		private VecInt binaryTree;
		private IntComparator comparator;
		private boolean debug;
		
		public IntHeap(IntComparator comparator) {
			binaryTree = new VecInt(8);
			this.comparator = comparator;
			debug = false;
		}
		
		public int getRoot() {
			return binaryTree.size() > 0 ? binaryTree.get(0) : -1;
		}
		
		public void insert(int n) {
			binaryTree.pushBack(n);
			shiftUp(binaryTree.size()-1);			
		}
		
		public int extract() {
			int root = binaryTree.get(0);
			int lastElem = binaryTree.getPopBack();
			//if (lastElem == 62) debug = true;
			if (debug) {
				System.out.println("***********************");
				dump();
				System.out.println("extract " +
								"lastElem: " + lastElem + "(" + comparator.getElemValue(lastElem) + ") " + 
								"root: " + root + "(" + comparator.getElemValue(root) + ") ");			
			}
			if (binaryTree.size() > 0) {
				binaryTree.set(0, lastElem);
				shiftDown(0);
			}
			if (debug) {
				dump();				
				System.out.println("***********************");
			}
			if (lastElem == 62) debug = false;
			return root;
		}
		
		private void shiftUp(int childIndex) {
			if (childIndex > 0) {
				int parentIndex = (childIndex - 1) >> 1;
				if (comparator.compare(binaryTree.get(childIndex), binaryTree.get(parentIndex)) == 1) {
					binaryTree.swap(childIndex, parentIndex);
					shiftUp(parentIndex);
				}
			}
		}

		/* devuelve el indice asociado al mayor de los tres valores en el arbol (nodo y sus 2 hijos)*/
		private int swapIndex(int nodeIdx) {
			int idx = nodeIdx;
			int leftIdx = 2 * nodeIdx + 1;
			int rightIdx = 2 * nodeIdx + 2;			
			int size = binaryTree.size();
			int nodeValue = binaryTree.get(nodeIdx);
			int leftCmp = leftIdx < size ? comparator.compare(binaryTree.get(leftIdx), nodeValue) : -1;
			int rightCmp = rightIdx < size ? comparator.compare(binaryTree.get(rightIdx), nodeValue) : -1;
			if (debug) {
				System.out.println("\t swapIndex idx = " + idx + " leftIdx = " + leftIdx + " rightIdx = " + rightIdx);
				System.out.println("\t swapIndex idx = " + binaryTree.get(idx) + " (" + comparator.getElemValue(binaryTree.get(idx)) + ") " +
									"leftIdx = " + binaryTree.get(leftIdx) + " (" + comparator.getElemValue(binaryTree.get(leftIdx)) + ") " +
									"rightIdx = " + binaryTree.get(rightIdx) + " (" + comparator.getElemValue(binaryTree.get(rightIdx)) + ") ");
				System.out.println("\t swapIndex leftCmp = " + leftCmp + " rightCmp = " + rightCmp);
			}
			if (leftCmp == 1 || rightCmp == 1) {
				switch (leftCmp - rightCmp) {
					case  2:
					case  1: idx = leftIdx; // left > node && right <= node (ie: 1 - (-1))
							 break;
					case  0: 				// left > node && right > node (ie: 1 - 1)
							 idx = comparator.compare(binaryTree.get(leftIdx), binaryTree.get(rightIdx)) > 0 ?
										leftIdx : rightIdx;
							 break;
					case -1:
					case -2: idx = rightIdx; // left <= node && right > node (ie: -1 - 1)
							 break;
				}
			}
			return idx;
		}
		
		private void shiftDown(int nodeIndex) {
			int idx = swapIndex(nodeIndex);
			if (idx != nodeIndex) {
				binaryTree.swap(nodeIndex, idx);
				shiftDown(idx);
			}
		}

		/*
		public void dump() {
			int i = 1;
			int j = 0;
			while (j < binaryTree.size()) {
				i <<= 1;
				for (; j < i-1 && j < binaryTree.size(); j++) {
					System.out.print(binaryTree.get(j) + "(" + comparator.getElemValue(binaryTree.get(j)) + ")  ");
				}
				System.out.println();
			}
		}
		*/
		public void dump() {
			//System.out.println("empty: " + empty());
			for (int i = 0; i < binaryTree.size(); i++) {
				System.out.print(binaryTree.get(i) + " ");
			}
			System.out.println();
		}

	}

	public static interface IntComparator {
		public int compare(int v1, int v2);
		public int getElemValue(int v);
	}
	
	public static abstract class IntComparatorImpl implements IntComparator {
		public int getElemValue(int v) {
				return v;
		}
	}
	
	public static class MaxComparator extends IntComparatorImpl {
			public int compare(int v1, int v2) {
				return v1 > v2 ? 1 : 
							v1 == v2 ? 0 : -1;
			}
	}

	public static class MinComparator extends IntComparatorImpl {
			public int compare(int v1, int v2) {
				return v1 < v2 ? 1 : 
							v1 == v2 ? 0 : -1;
			}
	}
	
	public static class ReferenceComparator implements IntComparator {
		private VecInt values;
		private IntComparator comparator;
		
		public ReferenceComparator(VecInt values, IntComparator comparator) {
			this.values = values;
			this.comparator = comparator;
		}
		
		public int compare(int v1, int v2) {
			return comparator.compare(values.get(v1), values.get(v2));
		}
		
		public int getElemValue(int v) {
			return values.get(v);
		}
	}

	public static class WeightedSpanningTreeBuilder {
		public static WeightedGraph build(WeightedGraph graph, IntComparator comparator) throws Exception {
			WeightedGraph spanningTree = new WeightedGraph(graph.getNumberOfVertices());
			PartitionUnionFind unionFind = new PartitionUnionFind(graph.getNumberOfVertices());
			VecInt weights = graph.getEdgeWeights();
			IntHeap heap = new IntHeap(new ReferenceComparator(weights, comparator));
			for (int i = 0; i < weights.size(); i++) {
				heap.insert(i);
			}
			//heap.dump();
			while (unionFind.getNumberOfParts() > 1) {
				//System.out.println("unionFind.getNumberOfParts: " + unionFind.getNumberOfParts());
				int iE = heap.extract();
				int iV0 = graph.getVertex0(iE);
				int iV1 = graph.getVertex1(iE);
				if (unionFind.checkJoin(iV0, iV1)) { // particiones distintas => agregar el eje al arbol generador
					//System.out.println("iV0: " + iV0 + " iV1: " + iV1 +" edge: " + iE + " weight: " + weights.get(iE));
					spanningTree.insertEdge(iV0, iV1, weights.get(iE));
				}
			}
			return spanningTree;
		}
	}

//1497 + 1000 = 2497
	/*
	public static void main(String[] args) throws Exception {
		WeightedGraph g = new WeightedGraph(100);
		// WeightedGraph g = new WeightedGraph(3);
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
		WeightedGraph tree = WeightedSpanningTreeBuilder.build(g, new MinComparator());
		System.out.println("tree.getTotalWeight(): " + tree.getTotalWeight());
	}
*/
/*
	public static void main(String[] args) throws Exception {
		WeightedGraph g = new WeightedGraph(5);	  
		System.out.println("insertEdge(0, 1): " + g.insertEdge(0, 1, 1));
		System.out.println("insertEdge(1, 2): " + g.insertEdge(1, 2, 2));
		System.out.println("insertEdge(2, 3): " + g.insertEdge(2, 3, 3));
		System.out.println("insertEdge(3, 4): " + g.insertEdge(3, 4, 4));
		System.out.println("insertEdge(0, 4): " + g.insertEdge(0, 4, 5));
		g.dump();
		WeightedGraph tree = WeightedSpanningTreeBuilder.build(g, new MaxComparator());
		tree.dump();
	}

	public static void main(String[] args) {
		VecInt v = new VecInt(6);
		v.pushBack(1);
		v.pushBack(3);
		v.pushBack(5);
		v.pushBack(4);
		v.pushBack(2);
		v.pushBack(8);
		ReferenceComparator refCmp = new ReferenceComparator(v, new MaxComparator());
		IntHeap h = new IntHeap(refCmp);
		for (int i = 0; i < v.size(); i++) {
			h.insert(i);
		}
		v.dump();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
	}
*/
	
/*	
	public static void main(String[] args) {
		IntHeap h = new IntHeap(new IntComparator() {
			public int compare(int v1, int v2) {
				return v1 > v2 ? 1 : 
							v1 == v2 ? 0 : -1;
			}
		});
		h.insert(1);
		h.insert(3);
		h.insert(5);
		h.insert(4);
		h.insert(2);
		h.insert(8);
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
	}

	public static void main(String[] args) {
		WeightedGraph g = new WeightedGraph(5);
		System.out.println("insertEdge(0, 1): " + g.insertEdge(0, 1, 1));
		System.out.println("insertEdge(1, 2): " + g.insertEdge(1, 2, 2));
		System.out.println("insertEdge(2, 3): " + g.insertEdge(2, 3, 3));
		System.out.println("insertEdge(3, 4): " + g.insertEdge(3, 4, 4));
		System.out.println("insertEdge(0, 4): " + g.insertEdge(0, 4, 5));
		g.dump();
	}
*/

	public static void main(String[] args) {
		int n = 10;
		IntHeap heap = new IntHeap(new MinComparator());
		for (int i = 0; i < n; i++) {
			heap.insert(n-1-i);
		}
		for (int i = 0; i < n; i++) {
			heap.dump();
			heap.extract();
		}

	}
}

