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
 	private VecInt _forbiddenEdges; //Esto resulta necesario para no repetir cycleEdges 
 	private int _treeCnt;
 	private int _call;
 	
	public SpanningTreesMatsui(Graph g) {
		_graph = g;
		_relabelGraph = new	 EdgeRelabelGraph(g);
		_tree = new TreeMatsui(_relabelGraph);
		_treeCnt = 0;
		_call = 0;
		_forbiddenEdges = new VecInt(30); //valor inicial arbitrario
	}

	public VecInt getPivotEdges() throws Exception {
		VecInt pivotEdges = new VecInt(10);
		PartitionUnionFind label = _tree.label();
		for (int i = _tree.getBottom()+1; i < _graph.getNumberOfEdges(); i++) {
			int iE = _relabelGraph.getGraphIdx(i);
			if (label.find(_graph.getVertex0(iE)) != label.find(_graph.getVertex1(iE))) {
			//vertices en particiones distintas => el eje es pivot
				pivotEdges.pushBack(i);
			}
		}
		return pivotEdges;
	}
	
	public VecInt getCycleEdges(int pivotEdge) throws Exception {
		return _tree.getCycleEdges(pivotEdge);
	}
	
	private void processSpanningTree() {
		// System.out.println("ACA: " + _treeCnt++);
		_treeCnt++;
	}	
	
	public void allSpanningTrees() throws Exception {
		processSpanningTree();
		//_tree.getTreeEdges().dump();
		VecInt pivotEdges = getPivotEdges();
		//System.out.println(_call + " pivotEdges.size: " + pivotEdges.size());
		if (pivotEdges.size() > 0) {
			for (int i = 0; i < pivotEdges.size(); i++) {
				int pivotEdge = pivotEdges.get(i);
				VecInt cycleEdges = getCycleEdges(pivotEdge);
				//System.out.println(_call + " ANTES i: " + i + " pivotEdge: " + pivotEdge + " cycleEdges.size: " + cycleEdges.size());
				//pivotEdges.dump();
				//cycleEdges.dump();
				int _forbiddenEdgesCount = 0;
				for (int j = 0; j < cycleEdges.size(); j++) {
					int cycleEdge = cycleEdges.get(j); 
					if (!_forbiddenEdges.contains(cycleEdge)) {
						//System.out.println(_call + " entre!");
						//System.out.println(_call + " pivotEdge: " + pivotEdge + " cycleEdge: " + cycleEdge);
						_tree.set(cycleEdge, pivotEdge);
						_call++;
						allSpanningTrees();
						_call--;
						_tree.set(cycleEdge, cycleEdge);
						_forbiddenEdges.pushBack(cycleEdge); // ACA!!!
						_forbiddenEdgesCount++;
					}
				}
				//ACA debería liberar los forbidden edges de esta iteracion
				_forbiddenEdges.popBackN(_forbiddenEdgesCount);
			}
		}
	}

	public int getTreeCnt() {
		return _treeCnt;
	}
	
	public void dump() {
	}

	public static void main(String[] args) throws Exception {
		SpanningTreesMatsui s;
		long startTime = System.currentTimeMillis();		
		int n = 9;
		System.out.println("Complete Graphs");
		for (int i = 2; i < n; i++) {
			s = new SpanningTreesMatsui(GraphTarjanRead.buildCompleteGraph(i));
			s.allSpanningTrees();			
			int treeCnt = s.getTreeCnt();
			System.out.println(i + ": " + ((double)treeCnt == Math.pow((double)i, (double)i-2) ? "OK" : "ERROR") + " (" + treeCnt + ")");
		}
		System.out.println("Cycle Graphs");
		for (int i = 3; i < n; i++) {
			s = new SpanningTreesMatsui(GraphTarjanRead.buildCycleGraph(i));
			s.allSpanningTrees();			
			int treeCnt = s.getTreeCnt();			
			System.out.println(i + ": " + (treeCnt == i ? "OK" : "ERROR") + " (" + treeCnt + ")");
		}
		n = 5;
		int m = 7;
		System.out.println("Complete Bipartite Graphs");
		for (int i = 2; i < n; i++) {
			for (int j = i; j < m; j++) {
				s = new SpanningTreesMatsui(GraphTarjanRead.buildCompleteBipartite(i,j));
				s.allSpanningTrees();
				int treeCnt = s.getTreeCnt();
				System.out.println(i + ": " + j + 
						((double)treeCnt == Math.pow((double)i, (double)j-1) * 
						          Math.pow((double)j, (double)i-1) ? " OK" : " ERROR") + " (" + treeCnt + ")");
			}
		}
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("totalTime: " + totalTime);
	}
}
