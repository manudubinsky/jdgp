package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SpanningTree;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.WeightedGraphBuilder;
import edu.jdgp.WeightedGraphBuilder.ExactForm;
import edu.jdgp.BFSSpanningTree;

// 1) Construcción de la matriz M = [L,B;A,I] (ver apuntes Gabriel)
// 2) Resolución del sistema lineal M [x;y] = v
// Pensar si la matriz B puede ser esparsa por filas o columnas 
// en función de si se puede hacer una buena elección de árbol inicial

/* TODO 
 * Hay que mapear las filas de la matriz a los indices de los ejes
 * 		1) Para eso primero hay que recorrer los nodos del árbol y mapear
 * 		las primeras n-1 filas de la matriz a los ejes del árbol
 * 		2) Para los demás ejes de los nodos (en orden) hay que mapear las 
 * 		restantes filas a los loop-edges
 * 		3) Para construir B (la parte superior derecha) de la matriz uso el vector "_parent"
*/

public class FormIntegratorSimple {
	private WeightedGraph _graph;
	private SparseMatrixInt _M;
	
	public FormIntegratorSimple(WeightedGraph graph) throws Exception {
		_graph = graph;
		
	}
	
	private BFSSpanningTree buildTree() throws Exception {
			return new BFSSpanningTree(_graph, 0);
	}
	
	/* 
	 * Cada columna de B esta en correspondencia con los loop-edges.
	 * Sea iE = (iV0, iV1) un loop-edge, entonces para construir la columna de B 
	 * asociada a iE hay que subir por los caminos que llevan a iV0 y a iV1 hasta 
	 * la raiz hasta encontrar el primer nodo en comun entre los caminos.
	 * Aquellos ejes en el camino de iV0 se setean en 1 y los ejes asociados 
	 * a iV1 se setean en -1 en la matriz M
	 */ 
	private void buildColumnB(BFSSpanningTree tree, VecInt treeEdgesInverseMap, int col, int iV0, int iV1) {
		int parent, child, value;
		VecInt parents = tree.getParents();
		while (iV0 != iV1) {
			if (iV0 > iV1) {
				//System.out.println("ACA iV0");
				child = iV0;
				parent = parents.get(iV0);
				value = 1;
				iV0 = parent;
			} else {
				//System.out.println("ACA iV1");
				child = iV1;
				parent = parents.get(iV1);
				value = -1;				
				iV1 = parent;
			}
			//necesitamos saber la fila asociada al tree edge parent->child
			//System.out.println(parent + " " + child);
			int iE = _graph.getEdge(tree.label2Vertex(parent), tree.label2Vertex(child));
			int row = treeEdgesInverseMap.get(iE);
			_M.set(row, col, value);
		}
	}
	
	public void buildM() throws Exception { //M = [L,B;A,I]; M es de tamaño (m x m)
		int nE = _graph.getNumberOfEdges();
		_M = new SparseMatrixInt(nE);
		BFSSpanningTree tree = buildTree();
		VecBool visitedEdges = new VecBool(nE,false);
		VecInt treeEdgesInverseMap = new VecInt(nE,-1); //mapea cada tree-edge al indice de la fila de M
		//construccion de L
		VecInt[] _treeData = tree.getTree();
		int levels = tree.getTreeLevels();
		int edgeCount = 0;
		for (int i = 0; i < levels; i++) {
			VecInt currentLevel = _treeData[i];
			int iV0 = tree.label2Vertex(i); // indice del padre en el grafo
			for (int j = 0; j < currentLevel.size(); j++) {
				int child = currentLevel.get(j);
				int iV1 = tree.label2Vertex(child); // index del hijo en el grafo
				int iE = _graph.getEdge(iV0, iV1); // indice del eje (iV0, iV1)
				visitedEdges.set(iE, true); // marcar el eje como visitado
				if (i > 0)
					_M.set(edgeCount, i-1, -1); // src del eje dirigido
				_M.set(edgeCount, child-1, 1); // dst del eje dirigido
				treeEdgesInverseMap.set(iE,edgeCount);
				edgeCount++;
			}
		}
		//construccion de A
		for (int i = 0; i < _graph.getNumberOfVertices(); i++) {
			int iV = tree.label2Vertex(i); // indice del nodo en el grafo
			VecInt edges = _graph.getVertexEdges(iV);
			for (int j = 0; j < edges.size(); j++) {
				int iE = edges.get(j);
				if (!visitedEdges.get(iE)) { //si no es un eje del árbol => es un loop-edge
					visitedEdges.set(iE, true);
					int neighborLabel = tree.vertex2Label(_graph.getNeighbor(iV, iE));
					if (i > 0)
						_M.set(edgeCount, i-1, -1);
					_M.set(edgeCount, neighborLabel-1, 1);
					buildColumnB(tree, treeEdgesInverseMap, edgeCount, i, neighborLabel);
					edgeCount++;
				}
			}
		}
		//construccion de I
		for (int i = _graph.getNumberOfVertices()-1; i < nE; i++) {
			_M.set(i, i, 1);
		}
		_M.fullDump();
	}
	
	public static void main(String[] args) throws Exception  {
		Graph g = Graph.buildCompleteGraph(5);
		ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
		FormIntegratorSimple integrator = new FormIntegratorSimple(form.getWeightedGraph());
		integrator.buildM();
	}


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
*/
