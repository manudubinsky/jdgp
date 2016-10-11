package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SpanningTree;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.PolygonMesh;

public class FormIntegrator {
	private PolygonMesh _mesh;
	private SparseMatrix _edgesMatrix;
	
	public FormIntegrator(PolygonMesh mesh) throws Exception {
		_mesh = mesh;
		buildMatrix();
	}
		
	public void buildMatrix() throws Exception {
		Graph _graph = _mesh.getGraph();
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
	
	public void integrate(SparseMatrix edgesWeights) {
		
	}

	public SparseMatrix getEdgesMatrix() {
		return _edgesMatrix;
	}

	public static void main(String[] args) {
		VecFloat coord = new VecFloat(16);
		coord.pushBackCoord(1.633f,-0.943f,-0.667f); //iV0
		coord.pushBackCoord(0.000f,0.000f,2.000f);   //iV1
		coord.pushBackCoord(-1.633f,-0.943f,-0.667f);//iV2
		coord.pushBackCoord(0.000f,1.886f,-0.667f);  //iV3

		VecInt coordIndex = new VecInt(20);
		coordIndex.pushBackTriFace(0,1,2);
		coordIndex.pushBackTriFace(3,1,0);
		coordIndex.pushBackTriFace(2,1,3);
		coordIndex.pushBackTriFace(2,3,0);

		SparseMatrix edgesWeights = new SparseMatrix(4);
		edgesWeights.set(0,1,1f);
		edgesWeights.set(0,2,1f);
		edgesWeights.set(0,3,1f);
		edgesWeights.set(1,2,1f);
		edgesWeights.set(1,3,1f);
		edgesWeights.set(2,3,1f);
		
		try {
			PolygonMesh pm = new PolygonMesh(coord, coordIndex);
			FormIntegrator integrator = new FormIntegrator(pm);
			integrator.getEdgesMatrix().fullDump();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
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
	*/
}
