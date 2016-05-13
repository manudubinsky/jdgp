package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.PartitionUnionFind;

public class CustomSpanningTree {
	public static class WeightedGraph extends Graph  { 
		private VecInt _edgeWeights;

		public WeightedGraph(int N) {
			super(N);
		}

		public void erase() {
			super.erase();
			_edgeWeights = new VecInt(getNumberOfVertices()); // estimativamente lo creo del tamano de la cantidad de nodos 
		}

		public int insertEdge(int iV0, int iV1, int weight) {			
			_edgeWeights.pushBack(weight);
			return super.insertEdge(iV0, iV1);
		}

		public VecInt getEdgeWeights() {
			return _edgeWeights;
		}
		
		public void dump() {
			for (int i = 0; i < _nE; i++) {
				int iV0 = _edges.get(2 * i);
				int iV1 = _edges.get(2 * i + 1);
				System.out.println(iV0 + " -> " + iV1 + " (" + _edgeWeights.get(i)  + ")");
			} 
		}	
	}
/*
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
	public static class IntHeap { 
		private VecInt binaryTree;
		private IntComparator comparator;
		
		public IntHeap(IntComparator comparator) {
			binaryTree = new VecInt(8);
			this.comparator = comparator;
		}
		
		public void insert(int n) {
			binaryTree.pushBack(n);
			shiftUp(binaryTree.size()-1);			
		}
		
		public int extract() {
			int root = binaryTree.get(0);
			int lastElem = binaryTree.getPopBack();
			if (binaryTree.size() > 0) {
				binaryTree.set(0, lastElem);
				shiftDown(0);
			}
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
			if (leftCmp == 1 || rightCmp == 1) {
				switch (leftCmp - rightCmp) {
					case  2: idx = leftIdx; // left > node && right < node (ie: 1 - (-1))
							 break;
					case  0: 				// left > node && right > node (ie: 1 - 1)
							 idx = comparator.compare(binaryTree.get(leftIdx), binaryTree.get(rightIdx)) > 0 ?
										leftIdx : rightIdx;
							 break;
					case -2: idx = rightIdx; // left < node && right > node (ie: -1 - 1)
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

		public void dump() {
			binaryTree.dump();
		}
	}

	public static interface IntComparator {
		public int compare(int v1, int v2);
	}

	public static class MaxComparator implements IntComparator {
			public int compare(int v1, int v2) {
				return v1 > v2 ? 1 : 
							v1 == v2 ? 0 : -1;
			}
	}

	public static class MinComparator implements IntComparator {
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
	}

/*	
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
*/

	public static class WeightedSpanningTreeBuilder {
		public static WeightedGraph build(WeightedGraph graph, IntComparator comparator) throws Exception {
			WeightedGraph spanningTree = new WeightedGraph(graph.getNumberOfVertices());
			PartitionUnionFind unionFind = new PartitionUnionFind(graph.getNumberOfVertices());
			VecInt weights = graph.getEdgeWeights();
			IntHeap heap = new IntHeap(new ReferenceComparator(weights, comparator));
			for (int i = 0; i < weights.size(); i++) {
				heap.insert(i);
			}			
			while (unionFind.getNumberOfParts() > 1) {
				System.out.println("unionFind.getNumberOfParts: " + unionFind.getNumberOfParts());
				int iE = heap.extract();
				int iV0 = graph.getVertex0(iE);
				int iV1 = graph.getVertex1(iE);
				if (unionFind.hasToJoin(iV0, iV1)) { // particiones distintas => agregar el eje al arbol generador
					spanningTree.insertEdge(iV0, iV1, weights.get(iE));
				}
			}
			return spanningTree;
		}
	}

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
	
}

