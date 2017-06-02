package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;

/*
 * Implementacion de Graph específico para Tarjan-Rad
 * (generar todos los árbles generadores de un grafo
 * Referencia:
 * 		"Backtrack Algorithm for Listing Spanning Trees.pdf"
 * */
public class GraphTarjanRead extends Graph {
	private VecBool availableEdges;
 	private VecBool visitedNodes;
 	
	public GraphTarjanRead(int n) {
		super(n);
		availableEdges = new VecBool(n); //el tamaño se inicializa arbitrariamente
	}
	
	public void disableEdge(int edgeIdx) {
		availableEdges.set(edgeIdx, false);
	}
	
	public void enableEdge(int edgeIdx) {
		availableEdges.set(edgeIdx, true);
	}
	
	// devuelve el siguiente eje disponible a partir el eje 
	public int getNextAvailableEdge(int edgeIdx) {
		boolean found = false;
		while (!found && edgeIdx < availableEdges.size()) {
			if (availableEdges.get(edgeIdx))
				found = true;
			else
				edgeIdx++;
		}
		
		// System.out.println("getNextAvailableEdge edgeIdx: " + (edgeIdx-1));
		return found ? edgeIdx : -1;
	}
	
	private boolean hasPath(int source, int target) {
		if (source == target) {
			return true;
		} else {
			VecInt vertexEdges = getVertexEdges(source);
			for (int i = 0; i < vertexEdges.size(); i++) {
				int iE = vertexEdges.get(i);
				if (availableEdges.get(iE)) {
					int neighbor = getNeighbor(source, iE);
					if (!visitedNodes.get(neighbor)) {
						visitedNodes.set(neighbor,true);
						if (hasPath(neighbor, target))
							return true;
					}
				}
			}
			return false;
		}
	}
	
	// devuelve true/false si antes de agregar el eje ya hay un camino en el PST
	public boolean edgeInduceCycle(int edgeIdx) {
		if (!availableEdges.get(edgeIdx)) {
			visitedNodes = new VecBool(getNumberOfVertices(), false);
			return hasPath(getVertex0(edgeIdx), getVertex1(edgeIdx));
		}
		return false;
	} 
	
	// devuelve los bridge edges a partir de del eje 
	public VecInt getBridgeEdges(int edgeIdx) {
		VecInt bridgeEdges = new VecInt(2);
		return bridgeEdges;
	}
	
	public int insertEdge(int iV0, int iV1) {
		availableEdges.pushBack(true);
		return super.insertEdge(iV0, iV1);
	}
	
	// específico para crear el PST inicial de Tarjan-Read
	public static GraphTarjanRead clone(GraphTarjanRead g) {
		GraphTarjanRead copy = new GraphTarjanRead(g.getNumberOfVertices());		
		for (int i = 0; i < g.getNumberOfEdges(); i++) {
			copy.insertEdge(g.getVertex0(i), g.getVertex1(i));
		}
		copy.availableEdges = new VecBool(g.getNumberOfEdges(), false);
		return copy;
	}

	public void dump() {
		System.out.println("availableEdges");
		availableEdges.dump();
		for (int i = 0; i < getNumberOfEdges(); i++) {
			if (availableEdges.get(i)) {
				int iV0 = getVertex0(i);
				int iV1 = getVertex1(i);
				System.out.println(i + ": " + iV0 + " -> " + iV1);
			}
		}
	}

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

	public static void main(String[] args) throws Exception {
		GraphTarjanRead g1 = new GraphTarjanRead(4);
		g1.insertEdge(0, 1);
		g1.insertEdge(0, 2);
		g1.insertEdge(1, 2);
		GraphTarjanRead g2 = GraphTarjanRead.clone(g1);
		g2.dump();
		g2.enableEdge(0);
		//g2.enableEdge(1);
		g2.dump();
		System.out.println("g2.edgeInduceCycle(2): " + g2.edgeInduceCycle(2));
/*		g.insertEdge(0, 3);
		g.insertEdge(1, 3);
		g.insertEdge(2, 3);
*/
		
	}

}
