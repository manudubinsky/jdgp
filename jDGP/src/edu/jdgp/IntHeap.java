package edu.jdgp;

import edu.jdgp.PairHeap.MinComparator;

public class IntHeap { 
	private int[] binaryTree;
	int indice;
	
	public IntHeap(int N) {
		binaryTree = new int[N];
		indice = 0;
	}
	
	public void insert(int n) {
		binaryTree[indice] = n;
		shiftUp(indice);
		indice++;
		// System.out.println ("indice: " + indice);
	}
	
	public int extract() {
		int root = binaryTree[0];
		int lastElem = binaryTree[--indice];
		//System.out.println(root + " " + lastElem);
		if (indice > 0) {
			binaryTree[0] = lastElem;
			shiftDown(0);
		}
		return root;
	}
	
	public boolean empty() {
		return indice == 0;
	}
	
	private void shiftUp(int childIndex) {
		if (childIndex > 0) {
			int parentIndex = (childIndex - 1) >> 1;
			if (binaryTree[childIndex] < binaryTree[parentIndex]) {
				// System.out.println("ACA!: " + binaryTree[childIndex] + " " + binaryTree[parentIndex]);
				int aux = binaryTree[childIndex];
				binaryTree[childIndex] = binaryTree[parentIndex];
				binaryTree[parentIndex] = aux;
				// System.out.println(binaryTree[childIndex] + " " + binaryTree[parentIndex]);
				shiftUp(parentIndex);
			}
		}
	}

	private int cmp(int v1, int v2) {
		return v1 < v2 ? 1 : 
			v1 == v2 ? 0 : -1;

	}
	
	/* devuelve el indice asociado al mayor de los tres valores en el arbol (nodo y sus 2 hijos)*/
	private int swapIndex(int nodeIdx) {		
		int idx = nodeIdx;
		int leftIdx = 2 * nodeIdx + 1;
		int rightIdx = 2 * nodeIdx + 2;			
		int size = indice;
		// System.out.println("ACA!! " + nodeIdx + " " + leftIdx + " " + rightIdx + " " + size);
		int nodeValue = binaryTree[nodeIdx];
		int leftCmp = leftIdx < size ? cmp(binaryTree[leftIdx],nodeValue) : -1;
		int rightCmp = rightIdx < size ? cmp(binaryTree[rightIdx], nodeValue) : -1;
		if (leftCmp == 1 || rightCmp == 1) {
			switch (leftCmp - rightCmp) {
				case  2:
				case  1: idx = leftIdx; // left > node && right <= node (ie: 1 - (-1))
						 break;
				case  0: 				// left > node && right > node (ie: 1 - 1)
						 idx = cmp(binaryTree[leftIdx], binaryTree[rightIdx]) > 0 ?
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
			int aux = binaryTree[nodeIndex];
			binaryTree[nodeIndex] = binaryTree[idx];
			binaryTree[idx] = aux;
			shiftDown(idx);
		}
	}

	public void dump() {
		System.out.println("empty: " + empty());
		for (int i = 0; i < indice; i++) {
			System.out.print(binaryTree[i] + " ");
		}
		System.out.println();
	}

	/*
	public static void main(String[] args) {
		IntHeap h = new IntHeap(6);
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

		h.insert(5);
		h.insert(7);
		h.insert(3);

		h.dump();
		h.extract();
		h.dump();
		h.extract();
		h.dump();
		h.extract();

	}
*/
	public static void main(String[] args) {
		int n = 10;
		IntHeap heap = new IntHeap(n);
		for (int i = 0; i < n; i++) {
			heap.insert(n-1-i);
		}
		for (int i = 0; i < n; i++) {
			heap.dump();
			heap.extract();
		}

	}
}
