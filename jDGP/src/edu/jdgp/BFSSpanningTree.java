package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SpanningTree;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecBool;

// 1) Construcción de la matriz M = [L,B;A,I] (ver apuntes Gabriel)
// 2) Resolución del sistema lineal M [x;y] = v
// Pensar si la matriz B puede ser esparsa por filas o columnas 
// en función de si se puede hacer una buena elección de árbol inicial

public class BFSSpanningTree {
	private Graph _graph;
	private VecInt[] _spanningTree; //lista de adyacencia
	private VecInt _vertex2Label;
	private VecInt _label2Vertex;
	private VecInt _parent;
	int _nV, _nE, _label, _root; 
	int _treeLevels; //estima la estrechez del árbol

	public BFSSpanningTree (Graph graph, int root) throws Exception {
		_graph = graph;
		_nV = graph.getNumberOfVertices();
		_nE = graph.getNumberOfEdges();
		_root = root;
	}
	
	private int addVertex(int iV) {
		_vertex2Label.set(iV, _label);
		_label2Vertex.set(_label, iV);
		return _label++;
	}
	
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

	public VecInt[] getTree () throws Exception {
		if (_spanningTree == null) {
			_label = 0;
			_spanningTree = new VecInt[_nV];
			_vertex2Label = new VecInt(_nV,0);
			_label2Vertex = new VecInt(_nV,0);
			_parent = new VecInt(_nV,0);
			build(_root);
		}
		return _spanningTree;
	}

	public int label2Vertex(int label) {
		return _label2Vertex.get(label);
	}

	public int vertex2Label(int iV) {
		return _vertex2Label.get(iV);
	}

	public int getTreeLevels() {
		return _treeLevels;
	}

	public VecInt getParents() {
		return _parent; 
	}

	public void dump() {
		if (_spanningTree != null) {
			System.out.println("maxHops: " + _treeLevels);
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
		}
	}
	
/*
	public VecInt getTreeEdges() {
		return _treeEdges;
	}
*/

	public static void main(String[] args) throws Exception  {
		Graph g = Graph.buildCompleteBipartite(10,12);
		BFSSpanningTree t = new BFSSpanningTree(g, 0);
		t.getTree();
		t.dump();
	}

}
