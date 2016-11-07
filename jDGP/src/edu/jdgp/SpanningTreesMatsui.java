package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.EdgeRelabelGraph;
import edu.jdgp.DGP.PartitionUnionFind;
import edu.jdgp.TreeMatsui;
import edu.jdgp.GraphTarjanRead;

/*
 * Implementacion de Matsui para generar todos los árboles 
 * generadores de un grafo conexi. Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class SpanningTreesMatsui {
 	private Graph _graph;
 	private TreeMatsui _tree;
 	private EdgeRelabelGraph _relabelGraph;
 	// para evitar la recursion, se procesa con stacks
 	private VecInt _pivotEdgesStack; //stack de ejes pivot
 	private VecInt _cycleEdgesStack; //stack de ejes del ciclo inducido por ejes pivot
 	
	public SpanningTreesMatsui(Graph g) {
		_graph = g;
		_relabelGraph = new EdgeRelabelGraph(g);
		_tree = new TreeMatsui(_relabelGraph);
	}

	public void pushPivotEdges() throws Exception {
		PartitionUnionFind label = _tree.label();
		for (int i = _tree.getBottom()+1; i < _graph.getNumberOfEdges(); i++) {
			int iE = _relabelGraph.getGraphIdx(i);
			if (label.find(_graph.getVertex0(iE)) != label.find(_graph.getVertex1(iE))) {
			//vertices en particiones distintas => el eje es pivot
				_pivotEdgesStack.pushBack(i);
			}
		}
	}
	
	
	public void pushCycleEdges(int iE) throws Exception {
		VecInt cycleEdges = _tree.getCycleEdges(pivotEdge);
		for (int i = 0; i < cycleEdges.size(); i++) {
			_cycleEdgesStack.pushBack(cycleEdges.get(i));
		}
	}
	
	public void allSpanningTrees() throws Exception {
		pushPivotEdges();
		int pivotEdge = _pivotEdgesStack.pop();
		pushCycleEdges(pivotEdge);
		int cycleEdge = _cycleEdgesStack.pop();
		_tree.set(cycleEdge,pivotEdge);
	}

	public void dump() {
	}

	public static void main(String[] args) throws Exception {
		Graph g = new Graph(5);
		g.insertEdge(0, 1);//0
		g.insertEdge(0, 2);//1
		g.insertEdge(0, 3);//2
		g.insertEdge(0, 4);//3
		g.insertEdge(1, 2);//4
		g.insertEdge(2, 3);//5
		g.insertEdge(3, 4);//6
		SpanningTreesMatsui m = new SpanningTreesMatsui(g);
		m.allSpanningTrees();
	}
}
