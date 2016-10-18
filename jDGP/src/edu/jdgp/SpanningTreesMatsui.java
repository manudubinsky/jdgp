package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.GraphTarjanRead;

/*
 * Implementacion de Matsui para generar todos los árboles 
 * generadores de un grafo conexi. Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class SpanningTreesMatsui {
	private VecInt _edgeLabels; //relabel de los ejes: (e1,...,eN-1,...,eM); los primeros N-1 ejes son los del árbol generador inicial
 	private Graph _graph;
 	
	public SpanningTreesMatsui(Graph g) {
		_graph = g;
		relabelEdges();
	}
	
	//spanning tree inicial: BFS a partir del nodo 0 (podría ser de otro modo)
	private void relabelEdges() {
		_edgeLabels = new VecInt(_graph.getNumberOfEdges(), -1);
		VecBool visitedEdges = new VecBool(_graph.getNumberOfEdges(), false);
		int treeEdgeIdx = 0; //indice de ejes del árbol generador
		int loopEdgeIdx = _graph.getNumberOfVertices() - 1; //indice de ejes loop
		VecBool visitedNodes = new VecBool(_graph.getNumberOfVertices(), false);
		VecInt queue = new VecInt(_graph.getNumberOfVertices());
		queue.pushBack(0);
		int currentNodeIdx = 0;
		while (currentNodeIdx < _graph.getNumberOfVertices()) {
			int iV = queue.get(currentNodeIdx);
			if (!visitedNodes.get(iV)) {
				visitedNodes.set(iV, true);
				VecInt vertexEdges = _graph.getVertexEdges(iV);
				for (int i = 0; i < vertexEdges.size(); i++) {
					int iE = vertexEdges.get(i);
					if (!visitedEdges.get(iE)) {
						visitedEdges.set(iE,true);
						int neighbor = _graph.getNeighbor(iV, iE);
						if (!visitedNodes.get(neighbor)) {
						// si el vecino no fue visitado lo agrego a la queue y agrego el eje a los del árbol
							queue.pushBack(neighbor);
							_edgeLabels.set(treeEdgeIdx++,iE);
						} else {
						// si el nodo ya fue visitado agrego el eje a los ejes loop
							_edgeLabels.set(loopEdgeIdx++,iE);
						}
					}
				}
			}
			currentNodeIdx++;
		}
	}
	
	public void dump() {
		for (int i = 0; i < _edgeLabels.size(); i++) {
			int iE = _edgeLabels.get(i);
			System.out.println(iE + ": " + _graph.getVertex0(iE) + " -> " + _graph.getVertex1(iE));
		}
	}

	public static void main(String[] args) throws Exception {
		Graph g1 = new Graph(4);
		g1.insertEdge(0, 1);
		g1.insertEdge(0, 2);
		g1.insertEdge(1, 3);
		g1.insertEdge(1, 2);
		g1.insertEdge(0, 3);
		g1.insertEdge(2, 3);
		SpanningTreesMatsui m = new SpanningTreesMatsui(g1);
		m.dump();
	}

}
