package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SpanningTree;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.WeightedGraphBuilder;
import edu.jdgp.WeightedGraphBuilder.ExactForm;


// 1) Construcción de la matriz M = [L,B;A,I] (ver apuntes Gabriel)
// 2) Resolución del sistema lineal M [x;y] = v
// Pensar si la matriz B puede ser esparsa por filas o columnas 
// en función de si se puede hacer una buena elección de árbol inicial

public class FormIntegratorSimple {

/* TODO 
 * Hay que mapear las filas de la matriz a los indices de los ejes
 * 		1) Para eso primero hay que recorrer los nodos del árbol y mapear
 * 		las primeras n-1 filas de la matriz a los ejes del árbol
 * 		2) Para los demás ejes de los nodos (en orden) hay que mapear las 
 * 		restantes filas a los loop-edges
 * 		3) Para construir B (la parte superior derecha) de la matriz uso el vector "_parent"
*/
}

/*
public class FormIntegratorSimple {
	private WeightedGraph _graph;
	private SpanningTree _tree;
	private SparseMatrix _M;
	private SparseMatrix _L;
	private SparseMatrix _A;
	private SparseMatrix _B;
	
	public FormIntegratorSimple(WeightedGraph graph) throws Exception {
		_graph = graph;
		_tree = new SpanningTree(_graph);
		buildL();
		buildA();
	}

	private void buildL() {
		SparseMatrix tree = _tree.getTree();
		int rows = tree.getRows(); // rows devuelve el ultimo indice
		int cols = tree.getCols();
		_L = new SparseMatrix(rows);
		for (int i = 0; i < rows; i++) {
			for (int j = 1; j < cols; j++) {
				if (tree.get(i,j) != 0) {
					_L.set(i, j-1, tree.get(i,j));
				}
			}
		}
		//_L.dump();
	}

	private void buildA() {
		int numEdges = _graph.getNumberOfEdges();
		int numVertices = _graph.getNumberOfVertices();
		_A = new SparseMatrix(numEdges - numVertices + 1);
		VecInt treeEdges = _tree.getTreeEdges();
		VecInt loopEdges = new VecInt(numEdges, 1); 
		for (int i = 0; i < treeEdges.size(); i++) {
			loopEdges.set(treeEdges.get(i), 0);
		}
		int row = 0;
		for (int i = 0; i < numEdges; i++) {
			if (loopEdges.get(i) == 1) {				
				int v0Label = _tree.vertex2Label(_graph.getVertex0(i));
				int v1Label = _tree.vertex2Label(_graph.getVertex1(i));
				_A.set(row, Math.min(v0Label, v1Label) - 1, -1);
				_A.set(row, Math.max(v0Label, v1Label) - 1, 1);
				row++;
			}
		}
		//_A.dump();
		//System.out.println(numEdges + " " + numVertices);
		//System.out.println(_L.getRows() + " " + _L.getCols());
		//ystem.out.println(_A.getRows() + " " + _A.getCols());
		_L.dump();
		_A.dump();
	}
	
	public void integrate(SparseMatrix edgesWeights) {
		
	}

	public SparseMatrix getM() {
		return _M;
	}

	public static void main(String[] args) {		
		try {
			WrlReader reader = new WrlReader("/home/manuel/doctorado/jdgp/jDGP/img/ateneav.wrl");
			Graph g = reader.getMesh().getGraph();
			ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);

			FormIntegratorSimple integrator = new FormIntegratorSimple(form.getWeightedGraph());
			//integrator.getEdgesMatrix().fullDump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
+/
