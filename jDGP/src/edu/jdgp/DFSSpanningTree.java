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

public class DFSSpanningTree {
	private Graph _graph;
	private VecInt[] _spanningTree; //lista de adyacencia
	private VecInt _vertex2Label;
	private VecInt _label2Vertex;
	private VecInt _parent;
	private VecInt _loopEdges;
	int _nV, _nE, _label, _root; 

	public DFSSpanningTree (Graph graph, int root) throws Exception {
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
		_loopEdges = new VecInt(_nE-_nV+1);
		VecBool visited = new VecBool(_nV, false);	
		VecInt stack = new VecInt(_nV);		
		int parentLabel = -1;
		stack.pushBack(_root);	//en el stack van los nodos y los parents
		stack.pushBack(parentLabel);
		//System.out.println("ACA <0> " + _label);
		while (stack.size() > 0) {
			parentLabel = stack.getPopBack();
			int v = stack.getPopBack();			
			//System.out.println(v);
			if (!visited.get(v)) {
				visited.set(v, true);
				int nodeLabel = addVertex(v);
				_parent.set(nodeLabel, parentLabel);
				if (parentLabel >= 0) {					
					if (_spanningTree[parentLabel] == null)
						_spanningTree[parentLabel] = new VecInt(2);					
					_spanningTree[parentLabel].pushBack(nodeLabel);
				}
				VecInt vertexEdges = _graph.getVertexEdges(v);
				for (int j = 0; j < vertexEdges.size(); j++) {
					int edge = vertexEdges.get(j);
					int neighbor = _graph.getNeighbor(v, edge);
					if (!visited.get(neighbor)) {
						stack.pushBack(neighbor);
						stack.pushBack(nodeLabel);
					} else if (neighbor != _label2Vertex.get(parentLabel)) {
						_loopEdges.pushBack(edge);
					}					
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
			_loopEdges.dump();
		}
	}

/*
	public VecInt getTreeEdges() {
		return _treeEdges;
	}
*/

	// cuenta la cantidad de ciclos (inducidos por los loop-edges) a los que pertenece cada eje
	public VecInt getEdgeCyclesCount() {
		VecInt count = new VecInt(_nE, 0);
		for (int i = 0; i < _loopEdges.size(); i++) {
			int iE = _loopEdges.get(i);			
			count.inc(iE);
			int iV0 = _vertex2Label.get(_graph.getVertex0(iE));
			int iV1 = _vertex2Label.get(_graph.getVertex1(iE));			
			while (iV0 != iV1) {
				if (iV0 < iV1) {
					iE = _graph.getEdge(_label2Vertex.get(iV1), _label2Vertex.get(_parent.get(iV1)));
					iV1 = _parent.get(iV1);
				} else {
					iE = _graph.getEdge(_label2Vertex.get(iV0), _label2Vertex.get(_parent.get(iV0)));
					iV0 = _parent.get(iV0);
				}
				count.inc(iE);
			}			
		}		
		return count;
	}
	
	public static void main(String[] args) throws Exception  {
		Graph g = Graph.buildCompleteGraph(5);
		g.dump();
		//Graph g = Graph.buildCompleteBipartite(5,7);
		DFSSpanningTree t = new DFSSpanningTree(g, 0);
		t.getTree();
		t.dump();
		t.getEdgeCyclesCount().dump();
	}

}
