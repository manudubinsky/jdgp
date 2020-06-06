package edu.jdgp;

import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecObject;

public class PairHeap { 
	private int[] data;
	private int[] pair;
	private int[] dataIdx; //para modificar un valor: indice inverso con el valor de "pair" para saber la posicion en el heap
	int index;
	private IntComparator comparator;
	private boolean debug;
	
	public PairHeap(int n, IntComparator comparator) {
		data = new int[n];
		pair = new int[n];
		dataIdx = new int[n];
		index = 0;
		this.comparator = comparator;
		debug = false;
	}
	
	private void setData(int pos, int d, int p) { //d -> data, p -> pair
		data[pos] = d;
		pair[pos] = p;
		if (p < dataIdx.length)
			dataIdx[p] = pos;
	}

	private void setData(int pos, int[] d_p) {
		setData(pos, d_p[0], d_p[1]);		
	}

	private int[] getData(int pos) {
		int[] ret = {data[pos], pair[pos]};
		return ret;
	}

	public int[] getRoot() {
		return data.length > 0 ? getData(0) : null;
	}
	
	private void swap(int child, int parent) {
		int[] childPair= getData(child);
		int[] parentPair= getData(parent);
		setData(child, parentPair);
		setData(parent, childPair);
	}
	
	public void insert(int d, int p) {
		setData(index, d, p);
		shiftUp(index);
		index++;
	}	

	public void change(int d, int p) {
		if (p < index) {			
			int oldIndex = dataIdx[p];
			data[oldIndex] = d;
			//System.out.println("d: " + d + " p: " + p + " oldIndex: " + oldIndex);
			shiftUp(oldIndex); //pruebo para arriba 
			shiftDown(oldIndex); //y luego para abajo
		}			
	}
	
	private void dumpPair(int[] p) {
		System.out.println("*** pair: p[0]: " + p[0] + " p[1]: " + p[1]);
	}
	
	public int[] extract() {
		int[] root = getData(0);
		int[] lastElem = getData(--index);
		//dumpPair(root);
		//dumpPair(lastElem);
		if (index > 0) {
			setData(0, lastElem);
			shiftDown(0);
		}
		return root;
	}
	
	private void shiftUp(int childIndex) {
		if (childIndex > 0) {
			int parentIndex = (childIndex - 1) >> 1;
			if (comparator.compare(data[childIndex], data[parentIndex]) == 1) {
				swap(childIndex, parentIndex);
				shiftUp(parentIndex);
			}
		}
	}

	/* devuelve el indice asociado al mayor de los tres valores en el arbol (nodo y sus 2 hijos)*/
	private int swapIndex(int nodeIdx) {
		int idx = nodeIdx;
		int leftIdx = 2 * nodeIdx + 1;
		int rightIdx = 2 * nodeIdx + 2;			
		//int size = data.length;
		int nodeValue = data[nodeIdx];
		int leftCmp = leftIdx < index ? comparator.compare(data[leftIdx], nodeValue) : -1;
		int rightCmp = rightIdx < index ? comparator.compare(data[rightIdx], nodeValue) : -1;
		if (debug) {
			System.out.println("\t swapIndex idx = " + idx + " leftIdx = " + leftIdx + " rightIdx = " + rightIdx);
			System.out.println("\t swapIndex idx = " + data[idx] + " (" + comparator.getElemValue(data[idx]) + ") " +
								"leftIdx = " + data[leftIdx] + " (" + comparator.getElemValue(data[leftIdx]) + ") " +
								"rightIdx = " + data[rightIdx] + " (" + comparator.getElemValue(data[rightIdx]) + ") ");
			System.out.println("\t swapIndex leftCmp = " + leftCmp + " rightCmp = " + rightCmp);
		}
		if (leftCmp == 1 || rightCmp == 1) {
			switch (leftCmp - rightCmp) {
				case  2:
				case  1: idx = leftIdx; // left > node && right <= node (ie: 1 - (-1))
						 break;
				case  0: 				// left > node && right > node (ie: 1 - 1)
						 idx = comparator.compare(data[leftIdx], data[rightIdx]) > 0 ?
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
		boolean dbg = data[nodeIndex] == 9;
		int idx = swapIndex(nodeIndex);			
		if (idx != nodeIndex) {
			//if (dbg)
			//	System.out.println("nodeIndex: " + nodeIndex + "("+ data[nodeIndex] +") idx: " + idx + "("+data[idx]+")");
			swap(nodeIndex, idx);
			shiftDown(idx);
		}
	}

	public boolean empty() {
		return index == 0;
	}

	public void dump() {
		System.out.println("empty: " + empty());
		for (int i = 0; i < index; i++) {
			System.out.print(data[i] + " (" + pair[i] + ") ");
		}
		System.out.println();
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

	public static void main(String[] args) {
		int n = 10;
		PairHeap heap = new PairHeap(n, new MinComparator());
//		for (int i = 0; i < n; i++) {
//			heap.insert(n-1-i, i);
//		}
//		for (int i = 0; i < n; i++) {
//			heap.dump();
//			heap.extract();
//		}

		heap = new PairHeap(n, new MaxComparator());
		for (int i = 0; i < n; i++) {
			heap.insert(i, i);
		}
		
		heap.dump();
		
		heap.change(2, 5);
			
		for (int i = 0; i < n; i++) {
			heap.dump();
			heap.extract();
		}
		
	}
}
