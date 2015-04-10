package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SpanningTree;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;

public class FormIntegrator {
	private Graph _graph;
	private SparseMatrix _edgesMatrix;
	
	public FormIntegrator(Graph g) throws Exception {
		_graph = g;
		buildMatrix();
	}
	
	private void buildMatrix() throws Exception {
		int numEdges = _graph.getNumberOfEdges();
		SpanningTree spanningTree = new SpanningTree(_graph);
		VecInt loopEdges = new VecInt(numEdges, 1); // ejes que hay que agregarle a la matriz que devuelve spanningTree
		VecInt treeEdges = spanningTree.getTreeEdges();
		for (int i = 0; i < treeEdges.size(); i++) {
			loopEdges.set(treeEdges.get(i), 0);
		}
		_edgesMatrix = spanningTree.getTree();
		_edgesMatrix.resize(numEdges); // redimensionar la matriz para agregar los loop edges
		int row = _graph.getNumberOfVertices() - 1; //inicializar en la fila posterior a la ultima fila del arbol
		for (int i = 0; i < numEdges; i++) {
			if (loopEdges.get(i) == 1) {				
				int v0Label = spanningTree.vertex2Label(_graph.getVertex0(i));
				int v1Label = spanningTree.vertex2Label(_graph.getVertex1(i));
				_edgesMatrix.set(row, Math.min(v0Label, v1Label), -1);
				_edgesMatrix.set(row, Math.max(v0Label, v1Label), 1);
				row++;				
			}
		}
	}
	
	public SparseMatrix getEdgesMatrix() {
		return _edgesMatrix;
	}
	
	public static void main(String[] args) throws Exception {
		Graph g = new Graph(4);	  
		g.insertEdge(0, 1);
		g.insertEdge(0, 2);
		g.insertEdge(0, 3);
		g.insertEdge(1, 2);
		g.insertEdge(1, 3);
		g.insertEdge(2, 3);
		FormIntegrator s = new FormIntegrator(g);
		SparseMatrix m = s.getEdgesMatrix();
		m.fullDump();
	}
}
