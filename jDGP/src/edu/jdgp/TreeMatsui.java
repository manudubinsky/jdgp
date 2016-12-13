package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.EdgeRelabelGraph;
import edu.jdgp.DGP.PartitionUnionFind;

/*
 * Implementacion de Tree específico para Matsui
 * (generar todos los árbles generadores de un grafo conexo
 * Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class TreeMatsui {
	private EdgeRelabelGraph _relabelGraph;
	private Graph _graph;
	private VecInt _treeEdges;
	private VecBool _availableEdges; //es necesario poder saber si un eje pertenece al árbol
	private int top;
	private int bottom;
	
	public TreeMatsui(EdgeRelabelGraph relabelGraph) {
		_relabelGraph = relabelGraph;
		_graph = relabelGraph.getGraph();
		_treeEdges = new VecInt(_graph.getNumberOfVertices()-1,-1);
		_availableEdges = new VecBool(_graph.getNumberOfEdges(),false);		

		//el árbol inicial son los labels 0..n-2
		for (int i = 0; i < _treeEdges.size(); i++) {
			_treeEdges.set(i,i);
			_availableEdges.set(_relabelGraph.getGraphIdx(i),true);
		}
		top = 0;
		bottom = _treeEdges.size() - 1;
	}
	
	//sustituye el indice de un eje con otro (para obtener un hijo)
	public void set(int idx, int newEdgeIdx) {
		//System.out.println("idx: " + idx + " newEdgeIdx: " + newEdgeIdx);
		int oldEdgeIdx = _treeEdges.get(idx);
		_availableEdges.set(_relabelGraph.getGraphIdx(oldEdgeIdx), false);
		_availableEdges.set(_relabelGraph.getGraphIdx(newEdgeIdx), true);
		_treeEdges.set(idx, newEdgeIdx);
		if (top == -1) { // es el primer elemento
			top = bottom = newEdgeIdx;
		} else {
			if (oldEdgeIdx == top || oldEdgeIdx == bottom) {
			// si el valor anterior es bottom o top hay que buscar los nuevos bottom y top
				top = bottom = -1;
				for (int i = 0; i < _treeEdges.size(); i++) {
					int value = _treeEdges.get(i);
					if (value >= 0) {
						if (value < top || top == -1)
							top = value;
						if (value > bottom || bottom == -1)
							bottom = value;
					}
				}
			}
			if (newEdgeIdx < top) //si el nuevo valor es top
				top = newEdgeIdx;
			if (newEdgeIdx > bottom) //si el nuevo valor es bottom
				bottom = newEdgeIdx;
		}
	}
	
	public VecInt getTreeEdges() {
		return _treeEdges;
	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return bottom;
	}

	//simplificacion de la funcion "label" que sugiere el paper
	public PartitionUnionFind label() throws Exception {
		PartitionUnionFind label = new PartitionUnionFind(_graph.getNumberOfVertices());
		for (int i = 0; i < _treeEdges.size(); i++) {
			int iE = _treeEdges.get(i);
			if (iE >= _treeEdges.size()) {
			// si es un indice > n-2 hay que agregar el eje
				int graphIdx = _relabelGraph.getGraphIdx(iE);
				label.join(_graph.getVertex0(graphIdx),_graph.getVertex1(graphIdx));
			}
		}
		return label;
	}
	
	private boolean findPath(int fromVertex, int toVertex, VecInt edgeList) {
		//System.out.println("fromVertex: " + fromVertex + " toVertex: " + toVertex);
		if (fromVertex == toVertex)
			return true;
		else {
			boolean found = false;
			VecInt vertexEdges = _graph.getVertexEdges(fromVertex);
			for (int i = 0; i < vertexEdges.size() && !found; i++) {
				int iE = vertexEdges.get(i);
				//System.out.println("fromVertex: " + fromVertex + " toVertex: " + toVertex + " iE: " + iE);
				if (_availableEdges.get(iE)) {
					//System.out.println("fromVertex: " + fromVertex + " toVertex: " + toVertex + " iE: " + iE + " entre");
					_availableEdges.set(iE, false);
					int neighbor = _graph.getNeighbor(fromVertex, iE);
					if (findPath(neighbor, toVertex, edgeList)) {
						//System.out.println("fromVertex: " + fromVertex + " toVertex: " + toVertex + " iE: " + iE + " entre 2");
						found = true;
						if (_relabelGraph.getLabel(iE) < _graph.getNumberOfVertices()-1) {
							//System.out.println("fromVertex: " + fromVertex + " toVertex: " + toVertex + " iE: " + iE + " entre 3");
							edgeList.pushBack(_relabelGraph.getLabel(iE));
						}
					}
					_availableEdges.set(iE, true);
				}
			}
			return found;
		}
	}
	
	//para un eje pivot e, devuelve la lista de los indices de _treeEdges 
	//que pueden sustituirse por e para obtener un hijo
	//si no devuelve una lista vacía
	public VecInt getCycleEdges(int iE) throws Exception {
		VecInt edgeList = new VecInt(2);
		int graphEdgeIdx = _relabelGraph.getGraphIdx(iE);
		findPath(_graph.getVertex0(graphEdgeIdx), _graph.getVertex1(graphEdgeIdx), edgeList);
		return edgeList;
	}
	
	public void dump() {
		System.out.println("top: " + top + " bottom: " + bottom);
		_graph.dump();
		_treeEdges.dump();
		_availableEdges.dump();
	}

	public static void main(String[] args) throws Exception {
		TreeMatsui t;
		int n = 9; //9
		System.out.println("Complete Graphs");
		for (int i = 3; i < n; i++) {
			boolean ok = true;
			t = new TreeMatsui(new EdgeRelabelGraph(Graph.buildCompleteGraph(i)));
			for (int j = i-1; j < i*(i-1)/2 && ok; j++) {
				ok = ok && (t.getCycleEdges(j).size() == 2);
			}
			System.out.println(i + ": " + (ok ? "OK" : "ERROR"));
		}
		System.out.println("Cycle Graphs");
		for (int i = 3; i < n; i++) {
			t = new TreeMatsui(new EdgeRelabelGraph(Graph.buildCycleGraph(i)));
			System.out.println(i + ": " + (t.getCycleEdges(i-1).size() == i-1 ? "OK" : "ERROR"));
		}
		n = 5;
		int m = 7;
		System.out.println("Complete Bipartite Graphs");
		for (int i = 2; i < n; i++) {
			for (int j = i; j < m; j++) {
				boolean ok = true;
				t = new TreeMatsui(new EdgeRelabelGraph(Graph.buildCompleteBipartite(i,j)));
				for (int k = i+j-1; k < i*j; k++) {
					ok = ok && (t.getCycleEdges(k).size() == 3); //los ciclos en un bipartito miden 4
				}
				System.out.println("[" + i + "," + j + "]: " + (ok ? "OK" : "ERROR"));
			}
		}
	}

/*
	public static void main(String[] args) throws Exception {
		Graph g = new Graph(5);
		g.insertEdge(0, 1);//0
		g.insertEdge(0, 2);//1
		g.insertEdge(0, 3);//2
		g.insertEdge(0, 4);//3
		g.insertEdge(1, 2);//4
		g.insertEdge(2, 3);//5
		g.insertEdge(3, 4);//6
		TreeMatsui t = new TreeMatsui(new EdgeRelabelGraph(g));
		t.dump();
		t.label().dump();
		t.getCycleEdges(4).dump();//[0,1]
		t.getCycleEdges(5).dump();//[1,2]
		t.getCycleEdges(6).dump();//[2,3]
		t.set(2,6);
		t.label().dump();
		t.getCycleEdges(2).dump();//[3,6]
	}
*/
}
