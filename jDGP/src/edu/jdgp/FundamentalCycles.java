package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PartitionUnionFind;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.PairHeap.MaxComparator;
import edu.jdgp.SpanningTreesMatsuiCollector.TotalPathLengthHistogram;

// Algoritmos del paper: "Algorithms for Generating Fundamental Cycles in Graph" (355984.355988.pdf)

public class FundamentalCycles {
	private Graph _graph;
	private VecInt[] _spanningTree; //lista de adyacencia
	private VecInt _vertex2Label;
	private VecInt _label2Vertex;
	private VecInt _parent;
	int _nV, _nE, _label; 
	int _treeLevels; //estima la estrechez del árbol
	VecInt _spanningTreeEdges; //se usa en MBFS
	VecBool _isTreeEdges;

	public FundamentalCycles (Graph graph) {
		_graph = graph;
		_nV = graph.getNumberOfVertices();
		_nE = graph.getNumberOfEdges();
		_label = 0;
		_spanningTree = new VecInt[_nV];
		_vertex2Label = new VecInt(_nV,0);
		_label2Vertex = new VecInt(_nV,0);
		_parent = new VecInt(_nV,0);
		_isTreeEdges = new VecBool(_nE,false);
	}
	
	private int getMaxDegreeVertex(int[] degrees){
		int idx = -1;
		int max = -1;
		for (int i = 0; i < degrees.length; i++) {
			if (degrees[i] > max) {
				max = degrees[i];
				idx = i;
			}
		}
		return idx;
	}
	
	public void SDS() {
		int[] degrees = _graph.getDegrees();
		int _root = getMaxDegreeVertex(degrees);
		//System.out.println(idx);
		
		int queueIdx = 0;
		VecBool visited = new VecBool(_nV, false);	
		VecInt queue = new VecInt(_nV);
		addVertex(_root);
		queue.pushBack(_root);
		visited.set(_root, true);
		_parent.set(0, -1); //setear el parent de la raiz del arbol en -1
		int treeSize = 1;
		//System.out.println("ACA <0> " + _label);
		PairHeap heap = new PairHeap(degrees[_root], new MaxComparator());
		//System.out.println("queue.vecLen(): " + queue.vecLen()); 		
		while (treeSize < _nV) {
			//System.out.println("queue.size(): " + queue.size() + " queueIdx: " + queueIdx);
			int v = queue.get(queueIdx++);
			int parentLabel = _vertex2Label.get(v);
			VecInt vertexEdges = _graph.getVertexEdges(v);
			//ordenar los vecinos por grado
			for (int j = 0; j < vertexEdges.size(); j++) {
				int iE = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(v, iE);
				if (!visited.get(neighbor)) {
					heap.insert(degrees[neighbor], neighbor);
					_isTreeEdges.set(iE,true);
				}
			}
			//recorrer vecinos
			while (!heap.empty()) {
				int[] pair = heap.extract();
				int neighbor = pair[1];
				//if (!visited.get(neighbor)) {
					//System.out.println("ACA <2." + j + ">");
				if (_spanningTree[parentLabel] == null) {
					_spanningTree[parentLabel] = new VecInt(2);
				}
				int childLabel = addVertex(neighbor);
				_spanningTree[parentLabel].pushBack(childLabel);
				_parent.set(childLabel, parentLabel);
				treeSize++;
				queue.pushBack(neighbor);
				visited.set(neighbor, true);
				//}
			}
		}
	}
	
	public void DDS() {
		int[] degrees = _graph.getDegrees();
		int _root = getMaxDegreeVertex(degrees);
		//System.out.println(idx);
		
		VecBool visited = new VecBool(_nV, false);	
		addVertex(_root);
		visited.set(_root, true);
		_parent.set(0, -1); //setear el parent de la raiz del arbol en -1
		int treeSize = 1;
		//System.out.println("ACA <0> " + _label);
		PairHeap heap = new PairHeap(_nV, new MaxComparator());
		heap.insert(degrees[_root], _root);
		//System.out.println("queue.vecLen(): " + queue.vecLen()); 		
		while (treeSize < _nV) {
			//System.out.println("queue.size(): " + queue.size() + " queueIdx: " + queueIdx);
			//heap.dump();
			int[] pair = heap.extract();
			int v = pair[1];
			//System.out.println("v: " + v);			
			int parentLabel = _vertex2Label.get(v);
			VecInt vertexEdges = _graph.getVertexEdges(v);
			//ordenar los vecinos por grado
			for (int j = 0; j < vertexEdges.size(); j++) {
				int iE = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(v, iE);
				if (!visited.get(neighbor)) {
					heap.insert(degrees[neighbor], neighbor);
					int childLabel = addVertex(neighbor);
					_parent.set(childLabel, parentLabel);
					treeSize++;
					if (_spanningTree[parentLabel] == null) {
						_spanningTree[parentLabel] = new VecInt(2);
					}
					_spanningTree[parentLabel].pushBack(childLabel);
					_isTreeEdges.set(iE, true);
					visited.set(neighbor, true);
				}				
			}
		}		
	}
	
	public void UE() {
		int[] degrees = _graph.getDegrees();
		int _root = getMaxDegreeVertex(degrees);
		//System.out.println(idx);
		
		VecBool visited = new VecBool(_nV, false);
		VecBool processed = new VecBool(_nV, false);
		addVertex(_root);
		visited.set(_root, true);
		_parent.set(0, -1); //setear el parent de la raiz del arbol en -1
		int treeSize = 1;
		//System.out.println("ACA <0> " + _label);
		PairHeap heap = new PairHeap(_nV, new MaxComparator());
		heap.insert(degrees[_root], _root);
		//System.out.println("queue.vecLen(): " + queue.vecLen()); 		
		while (treeSize < _nV) {
			//System.out.println("queue.size(): " + queue.size() + " queueIdx: " + queueIdx);
			//heap.dump();
			int[] pair = heap.extract();
			int v = pair[1];
			processed.set(v, true);
			//System.out.println("v: " + v);			
			int parentLabel = _vertex2Label.get(v);
			VecInt vertexEdges = _graph.getVertexEdges(v);
			//ordenar los vecinos por grado
			for (int j = 0; j < vertexEdges.size(); j++) {
				int iE = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(v, iE);
				degrees[neighbor]--;
				if (!visited.get(neighbor)) {
					heap.insert(degrees[neighbor], neighbor);
					int childLabel = addVertex(neighbor);
					_parent.set(childLabel, parentLabel);
					treeSize++;
					if (_spanningTree[parentLabel] == null) {
						_spanningTree[parentLabel] = new VecInt(2);
					}
					_spanningTree[parentLabel].pushBack(childLabel);
					_isTreeEdges.set(iE, true);
					visited.set(neighbor, true);					
				} else {
					if (!processed.get(neighbor))  //si todavia no se proceso, le bajo el grado en el heap
						heap.change(degrees[neighbor], neighbor);					
				}
			}
		}
	}

	public void MBFS() throws Exception {
		_spanningTreeEdges = new VecInt(_nV-1);
		int[] degrees = _graph.getDegrees();
		PairHeap heapVertexDegrees = new PairHeap(_nV, new MaxComparator());
		for (int i = 0; i < degrees.length; i++) {
			heapVertexDegrees.insert(degrees[i], i);
		}		
		int[] pair = heapVertexDegrees.getRoot();
		int _root = pair[1];
		PartitionUnionFind parts = new PartitionUnionFind(_nV);

		while (_spanningTreeEdges.size() < _nV-1) {
			//System.out.println("queue.size(): " + queue.size() + " queueIdx: " + queueIdx);
			//heapVertexDegrees.dump();
			pair = heapVertexDegrees.extract();
			int v = pair[1];
			//System.out.println("v: " + v);			
			VecInt vertexEdges = _graph.getVertexEdges(v);
			PairHeap heapEdgesDegrees = new PairHeap(vertexEdges.size(), new MaxComparator());
			//ordenar los vecinos por grado
			for (int j = 0; j < vertexEdges.size(); j++) {
				int iE = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(v, iE);
				if (parts.checkHasToJoin(v, neighbor)) {
					heapEdgesDegrees.insert(degrees[v] + degrees[neighbor], iE);
				}
			}
			while (!heapEdgesDegrees.empty()) {
				pair = heapEdgesDegrees.extract();
				int iE = pair[1];
				int neighbor = _graph.getNeighbor(v, iE);
				//parts.dump();
				if (parts.checkJoin(v, neighbor)) {
					_spanningTreeEdges.pushBack(iE);
					_isTreeEdges.set(iE, true);
				}
			}
		}
		treeEdges2Parents();
	}

	private void treeEdges2Parents() {
		VecInt[] adjMatrix = new VecInt[_nV];
		for (int i = 0; i < _nV - 1; i++) {
			int iE = _spanningTreeEdges.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			if (adjMatrix[iV0] == null) 
				adjMatrix[iV0] = new VecInt(2);
			if (adjMatrix[iV1] == null) 
				adjMatrix[iV1] = new VecInt(2);
			adjMatrix[iV0].pushBack(iV1);
			adjMatrix[iV1].pushBack(iV0);
		}
		//for (int i = 0; i < adjMatrix.length; i++) {
		//	adjMatrix[i].dump();
		//}
		int _root = 0;
		int index = 0;
		VecInt queue = new VecInt(_nV);
		addVertex(_root);
		VecBool visited = new VecBool(_nV, false);
		visited.set(_root, true);
		_parent.set(_root, -1); //setear el parent de la raiz del arbol en -1
		queue.pushBack(_root);
		while (index < _nV) {
			int parent = queue.get(index++);
			for (int i = 0; i < adjMatrix[parent].size(); i++) {
				int neighbor = adjMatrix[parent].get(i);
				if (!visited.get(neighbor)) {
					//System.out.println("ACA:" + neighbor);
					queue.pushBack(neighbor);
					addVertex(neighbor);
					visited.set(neighbor, true);
					//System.out.println("p: " + _vertex2Label.get(parent) + " n:" + _vertex2Label.get(neighbor));
					//System.out.println("ACA2; " + _parent.get(0));
					_parent.set( _vertex2Label.get(neighbor), _vertex2Label.get(parent));
				}
			}
		}
		//System.out.println("ACA; " + _parent.get(0));
		//_parent.dump();
	}
	
	private int addVertex(int iV) {
		_vertex2Label.set(iV, _label);
		_label2Vertex.set(_label, iV);
		return _label++;
	}

	public int label2Vertex(int label) {
		return _label2Vertex.get(label);
	}

	public int vertex2Label(int iV) {
		return _vertex2Label.get(iV);
	}

	public VecInt getParents() {
		return _parent; 
	}

	public void dump() {
		if (_spanningTree != null) {
			for (int i = 0; i < _spanningTree.length; i++) {
				if (_spanningTree[i] != null) {
					VecInt row = _spanningTree[i];
					StringBuffer s = new StringBuffer();
					for (int j = 0; j < row.size(); j++) {
						int elem = row.get(j);
						s.append(elem + " (" + _label2Vertex.get(elem) + ") ");
					}
					System.out.println(i + " (" + _label2Vertex.get(i) + "): " + s);
				}
			}
			_parent.dump();
			_isTreeEdges.dump();
		}
	}

	public void dumpEdges() {
		for (int i = 0; i < _spanningTreeEdges.size(); i++) {
			int iE = _spanningTreeEdges.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			System.out.println(iV0 + " - " + iV1);
		}
		_isTreeEdges.dump();
	}
	
/*
	
	private void build(int root) throws Exception {
		int queueIdx = 0;
		VecBool visited = new VecBool(_nV, false);	
		VecInt queue = new VecInt(_nV);
		addVertex(_root);
		queue.pushBack(_root);
		visited.set(_root, true);
		_parent.set(0, -1); //setear el parent de la raiz del arbol en -1
		//System.out.println("ACA <0> " + _label);
		while (queue.size() < _nV) {
			int v = queue.get(queueIdx++);
			VecInt vertexEdges = _graph.getVertexEdges(v);
			int parentLabel = _vertex2Label.get(v);
			for (int j = 0; j < vertexEdges.size(); j++) {
				//System.out.println("ACA <1." + j + ">");
				int edge = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(v, edge);
				if (!visited.get(neighbor)) {
					//System.out.println("ACA <2." + j + ">");
					if (_spanningTree[parentLabel] == null) {
						_spanningTree[parentLabel] = new VecInt(2);
						_treeLevels++;
					}
					int childLabel = addVertex(neighbor);
					_spanningTree[parentLabel].pushBack(childLabel);
					_parent.set(childLabel, parentLabel);
					queue.pushBack(neighbor);
					visited.set(neighbor, true);
				}
			}
		}
	}

	public int getTreeLevels() {
		return _treeLevels;
	}

	

	public VecInt getTreeEdges() {
		return _treeEdges;
	}
*/

	/*
	public static void main(String[] args) throws Exception  {
		int[] edges = {0,1,0,2};
		//Graph g = Graph.buildCompleteGraphExceptEdges(5, edges);
		Graph g = Graph.buildCycleGraph(5);
		FundamentalCycles t = new FundamentalCycles(g);
		t.SDS();
		//t.getTree();
		t.dump();
	}
	*/

	public int totalLength() {
		int len = 0;
		for (int iE = 0; iE < _isTreeEdges.size(); iE++) {
			boolean isLoop = !_isTreeEdges.get(iE); 
			if (isLoop) {
				int label0 = _vertex2Label.get(_graph.getVertex0(iE));
				int label1 = _vertex2Label.get(_graph.getVertex1(iE));
				while (label0 != label1) {
					int parent0 = _parent.get(label0);
					int parent1 = _parent.get(label1);
					if (parent0 > parent1) 
						label0 = parent0;
					else
						label1 = parent1;
					len++;
				}
			}
		}
		return len;
	}
	
	public static void main(String[] args) throws Exception {

// prueba 1 SDS vs DDS	
/* 		Graph g = new Graph(9);
		

		g.insertEdge(0, 1);
		g.insertEdge(0, 2);
		g.insertEdge(0, 3);
		g.insertEdge(0, 4);
		g.insertEdge(1, 5);
		g.insertEdge(2, 6);
		g.insertEdge(2, 7);
		g.insertEdge(5, 6);
		g.insertEdge(6, 8);
		//g.dump();
//		int[] deg = g.getDegrees();
//		for (int i = 0; i < deg.length; i++) {
//			System.out.println(i + " -> " + deg[i]);
//		}
		FundamentalCycles t = new FundamentalCycles(g);
		t.DDS();
		//t.getTree();
		t.dump();
		System.out.println("totalLength: " + t.totalLength());
*/
  		

// prueba 2 DDS vs UE
/*  		Graph g = new Graph(9);
		g.insertEdge(0, 1);
		g.insertEdge(0, 2);
		g.insertEdge(0, 3);
		g.insertEdge(0, 4);
		g.insertEdge(0, 5);
		g.insertEdge(1, 2);
		g.insertEdge(1, 3);
		g.insertEdge(1, 4);
		g.insertEdge(2, 3);
		g.insertEdge(3, 6);
		g.insertEdge(5, 6);
		g.insertEdge(5, 7);
		g.insertEdge(5, 8);
		FundamentalCycles t = new FundamentalCycles(g);
		t.UE();
		//t.getTree();
		t.dump();
		System.out.println("totalLength: " + t.totalLength());
*/
		//g.dump();
//		int[] deg = g.getDegrees();
//		for (int i = 0; i < deg.length; i++) {
//			System.out.println(i + " -> " + deg[i]);
//		}
		
//prueba 3 MBFS
		Graph g = new Graph(12);
		g.insertEdge(0, 1);
		g.insertEdge(0, 2);
		g.insertEdge(0, 3);
		g.insertEdge(0, 4);
		g.insertEdge(0, 5);
		
		g.insertEdge(1, 2);
		g.insertEdge(1, 3);
		g.insertEdge(2, 5);
		
		g.insertEdge(6, 7);
		g.insertEdge(6, 8);
		g.insertEdge(6, 9);
		g.insertEdge(6, 10);
		g.insertEdge(6, 11);

		g.insertEdge(7, 8);
		g.insertEdge(7, 11);
		g.insertEdge(8, 9);
		
		g.insertEdge(4, 10);

		FundamentalCycles t = new FundamentalCycles(g);
		t.MBFS();
		
		//t.getTree();
		//t.dump();
		t.dumpEdges();
		t.getParents().dump();
		System.out.println("totalLength: " + t.totalLength());

	}
}
