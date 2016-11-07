package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.GraphTarjanRead;

/*
 * Implementacion de Tarjan-Rad para generar todos los árbles 
 * generadores de un grafo. Referencia:
 * 		"Backtrack Algorithm for Listing Spanning Trees.pdf"
 * */
public class SpanningTreesTarjanRead {
	private GraphTarjanRead _graph;
	private GraphTarjanRead _PST;
	private int _treeCnt;
	
	public SpanningTreesTarjanRead(GraphTarjanRead g) {
		_graph = g;
		_PST = GraphTarjanRead.clone(g);
	}

	//devuelve los cycle edges inducidos en el _PST al incluir el eje
	private VecInt includeEdge(int edgeIdx) {
		_graph.disableEdge(edgeIdx);
		_PST.enableEdge(edgeIdx);
		
		VecInt cycleEdges = new VecInt(4); //arbitrariamente se elige un tamaño
		int i = edgeIdx;
		while (i >= 0) {
			i = _graph.getNextAvailableEdge(i + 1);
			//System.out.println("ACA3 i: " + i);
			if (i >= 0 && _PST.edgeInduceCycle(i)) {
				//System.out.println("ACA3 entre!!");
				_graph.disableEdge(i);
				cycleEdges.pushBack(i);
			}			
		}
		return cycleEdges;
	}

	private void rollbackIncludeEdge(int edgeIdx, VecInt cycleEdges) {
		//System.out.println("rollbackIncludeEdge edgeIdx: " + edgeIdx); cycleEdges.dump();
		_graph.enableEdge(edgeIdx);
		_PST.disableEdge(edgeIdx);
		
		for (int i = 0; i < cycleEdges.size(); i++) {
			_graph.enableEdge(cycleEdges.get(i));
		}		
	}

	//devuelve los bridge edges del grafo al excluir el eje 
	private VecInt excludeEdge(int edgeIdx) {
		_graph.disableEdge(edgeIdx);
		_PST.disableEdge(edgeIdx);
		
		VecInt bridgeEdges = _graph.getBridgeEdges(edgeIdx+1); //devuelve los ejes bridges a partir del eje actual
		for (int i = 0; i < bridgeEdges.size(); i++) {
			_PST.enableEdge(bridgeEdges.get(i));
			_graph.disableEdge(bridgeEdges.get(i));
		}		
		return bridgeEdges;
	}

	private void rollbackExcludeEdge(int edgeIdx, VecInt bridgeEdges) {
		_graph.enableEdge(edgeIdx);
		_PST.disableEdge(edgeIdx);
		
		for (int i = 0; i < bridgeEdges.size(); i++) {
			_PST.disableEdge(bridgeEdges.get(i));
			_graph.enableEdge(bridgeEdges.get(i));
		}		
	}
	
	private void allSpanningTreesInternal(int edgesPST, int edgeIdx) {
		//System.out.println("allSpanningTreesInternal edgesPST: " + edgesPST + " edgeIdx: " + edgeIdx);
		if (edgesPST == _PST.getNumberOfVertices() - 1) {
			// _PST.dump();
			_treeCnt++;
		} else if (edgeIdx < _PST.getNumberOfEdges()) {
			// 1 - Considerar agregar el primer eje que sea factible
			edgeIdx = _graph.getNextAvailableEdge(edgeIdx);
			//System.out.println("ACA edgeIdx: " + edgeIdx);
			if (edgeIdx >= 0) {
			// en este punto edgeIdx es un eje factible
				VecInt edges = includeEdge(edgeIdx); // devuelve los cycle edges
				//System.out.println("ACA2 edgeIdx: " + edgeIdx);
				allSpanningTreesInternal(edgesPST + 1, edgeIdx + 1);
				rollbackIncludeEdge(edgeIdx, edges);
				edges = excludeEdge(edgeIdx); // devuelve los bridge edges
				allSpanningTreesInternal(edgesPST, edgeIdx + 1);
				rollbackExcludeEdge(edgeIdx, edges);
			}
		}
	}
	
	public int getTreeCnt() {
		return _treeCnt;
	}
	
	public void allSpanningTrees() {
		_treeCnt = 0;
		allSpanningTreesInternal(0,0);
		//System.out.println("_treeCnt: " + _treeCnt);
	}


	public static void main(String[] args) throws Exception {
		long startTime = System.currentTimeMillis();		
		SpanningTreesTarjanRead s;
		int n = 9;
		System.out.println("Complete Graphs");
		for (int i = 2; i < n; i++) {
			s = new SpanningTreesTarjanRead(GraphTarjanRead.buildCompleteGraph(i));
			s.allSpanningTrees();			
			int treeCnt = s.getTreeCnt();
			System.out.println(i + ": " + ((double)treeCnt == Math.pow((double)i, (double)i-2) ? "OK" : "ERROR") + " (" + treeCnt + ")");
		}
		System.out.println("Cycle Graphs");
		for (int i = 3; i < n; i++) {
			s = new SpanningTreesTarjanRead(GraphTarjanRead.buildCycleGraph(i));
			s.allSpanningTrees();			
			int treeCnt = s.getTreeCnt();			
			System.out.println(i + ": " + (treeCnt == i ? "OK" : "ERROR") + " (" + treeCnt + ")");
		}
		n = 5;
		int m = 7;
		System.out.println("Complete Bipartite Graphs");
		for (int i = 2; i < n; i++) {
			for (int j = i; j < m; j++) {
				s = new SpanningTreesTarjanRead(GraphTarjanRead.buildCompleteBipartite(i,j));
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
