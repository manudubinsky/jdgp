package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;

/*
 * Implementacion de Graph específico para Matsui
 * (generar todos los árbles generadores de un grafo conexo
 * Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class GraphMatsui extends Graph {
	private VecInt _edgeLabels; //relabel de los ejes: (e1,...,eN-1,...,eM); los primeros N-1 ejes son los del árbol generador inicial
 	
	public GraphMatsui(int n) {
		super(n);
	}
	
	//spanning tree inicial: BFS a partir del nodo 0 (podría ser de otro modo)
	public void buildInitialTree() {
		_edgeLabels = new VecInt(getNumberOfEdges(), -1);
		VecBool visitedEdges = new VecBool(getNumberOfEdges(), false);
		int treeEdgeIdx = 0; //indice de ejes del árbol generador
		int loopEdgeIdx = getNumberOfVertices() - 1; //indice de ejes loop
		VecBool visitedNodes = new VecBool(getNumberOfVertices(), false);
		VecInt queue = new VecInt(getNumberOfVertices());
		queue.pushBack(0);
		int currentNodeIdx = 0;
		while (currentNodeIdx < getNumberOfVertices()) {
			int iV = queue.get(currentNodeIdx);
			if (!visitedNodes.get(iV)) {
				visitedNodes.set(iV, true);
				VecInt vertexEdges = getVertexEdges(iV);
				for (int i = 0; i < vertexEdges.size(); i++) {
					int iE = vertexEdges.get(i);
					if (!visitedEdges.get(iE)) {
						visitedEdges.set(iE,true);
						int neighbor = getNeighbor(iV, iE);
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
			int iV = _edgeLabels.get(i);
			System.out.println(iV + ": " + getVertex0(iV) + " -> " + getVertex1(iV));
		}
	}

/*
	public static GraphTarjanRead buildCompleteGraph(int n) {
		GraphTarjanRead g = new GraphTarjanRead(n);
		for (int i = 0; i < n-1; i++) {
			for (int j = i+1; j < n; j++) {
				g.insertEdge(i, j);
			}
		}
		return g;
	}

	public static GraphTarjanRead buildCycleGraph(int n) {
		GraphTarjanRead g = new GraphTarjanRead(n);
		for (int i = 0; i < n; i++) {
			g.insertEdge(i, (i + 1) % n);
		}
		return g;
	}

	public static GraphTarjanRead buildCompleteBipartite(int n, int m) {
		GraphTarjanRead g = new GraphTarjanRead(n+m);
		for (int i = 0; i < n; i++) {
			for (int j = n; j < n + m; j++) {
				g.insertEdge(i, j);
			}
		}
		return g;
	}
*/

	public static void main(String[] args) throws Exception {
		GraphMatsui g1 = new GraphMatsui(4);
		g1.insertEdge(0, 1);
		g1.insertEdge(0, 2);
		g1.insertEdge(1, 3);
		g1.insertEdge(1, 2);
		g1.insertEdge(0, 3);
		g1.insertEdge(2, 3);
		g1.buildInitialTree();
		g1.dump();
	}

}
