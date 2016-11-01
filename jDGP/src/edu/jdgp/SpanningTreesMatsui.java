package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.EdgeRelabelGraph;
import edu.jdgp.GraphTarjanRead;

/*
 * Implementacion de Matsui para generar todos los árboles 
 * generadores de un grafo conexi. Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class SpanningTreesMatsui {
 	private Graph _graph;
 	
	public SpanningTreesMatsui(Graph g) {
		_graph = g;
	}

	public void dump() {
	}

	public static void main(String[] args) throws Exception {
		Graph g1 = new Graph(4);
		g1.insertEdge(0, 1);
		g1.insertEdge(0, 2);
		g1.insertEdge(1, 3);
		g1.insertEdge(1, 2);
		g1.insertEdge(0, 3);
		g1.insertEdge(2, 3);
		EdgeRelabelGraph m = new EdgeRelabelGraph(g1);
		m.dump();
	}
}
