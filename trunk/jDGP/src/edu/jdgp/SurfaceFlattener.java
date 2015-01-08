package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class SurfaceFlattener {

	private PolygonMesh _mesh;
	private Graph _graph;
	
	public SurfaceFlattener(PolygonMesh mesh) throws Exception {
		_mesh = mesh;
		_graph = mesh._graph;
	}

	private void statsNormals(int step, VecFloat faceNormals) throws Exception {
		int faces = _mesh.getNumberOfFaces();
		System.out.println("**********************************************************************");
		System.out.println("Step: " + step);
		
		for (int i = 0; i < faces; i++) {			
			System.out.println("****************");
			float norm = PolygonMeshHelper.sqrNorm2(i, faceNormals);
			System.out.println("face: " + i + " norm^2: " + norm);			
			VecInt neighbors = _mesh.getFaceNeighbors(i+1);			
 			for (int j = 0; j < neighbors.size(); j++) {
 				int neighbor = neighbors.get(j) - 1;
 				float normNeighbor = PolygonMeshHelper.sqrNorm2(neighbor, faceNormals); 				
 				float innerProd = PolygonMeshHelper.innerProduct(i, neighbor, faceNormals);
 				float sqrt = (float)Math.sqrt(norm * normNeighbor);
 				// System.out.println("innerProd: " + innerProd + " sqrt: " + sqrt);
				System.out.println("alfa(" + i + " " + neighbor + "): " + 180 * Math.acos(innerProd/sqrt)/Math.PI);
			}
		}
	}
	
	private void statsEdges(int step) throws Exception {
		int edges = _graph.getNumberOfEdges();
		System.out.println("**********************************************************************");
		System.out.println("Step: " + step);
		VecFloat edgesNorms = PolygonMeshHelper.edgesNorms(_mesh);
		for (int i = 0; i < edges; i++) {			
			System.out.println("****************");			
			int iV0 = _mesh.getVertex0(i);
			int iV1 = _mesh.getVertex1(i);
			System.out.println("edge: " + i + " iV0: " + iV0 + " iV1: " + iV1 + " norm: " + edgesNorms.get(i));
		}
	}
	
	public void flatten(float lambda) throws Exception {
		//_mesh._coord.dump();
		SurfaceFlattenerStep1 step1 = new SurfaceFlattenerStep1(); // modificar las normales a las caras
		SurfaceFlattenerStep2 step2 = new SurfaceFlattenerStep2(); // modificar los nodos de acuerdo a las normales del step1
		SurfaceFlattenerParams paramsMain = new SurfaceFlattenerParams(lambda, 500);
		SurfaceFlattenerParams paramsStep1 = new SurfaceFlattenerParams(lambda, 10);
		SurfaceFlattenerParams paramsStep2 = new SurfaceFlattenerParams(lambda, 10);
		while (paramsMain.hasToContinue()) {
			VecFloat facesNormals = step1.execute(_mesh, paramsStep1.reset());
			statsNormals(paramsMain.getCurrentIter(), facesNormals);
			// facesNormals.dump("faceNormals");
			_mesh._coord = step2.execute(_mesh, facesNormals, paramsStep2.reset());
			statsEdges(paramsMain.getCurrentIter());
			//_mesh._coord.dump();
		}
	}

	public static void main(String[] args) {
		try {
			SurfaceFlattener sf = new SurfaceFlattener(SurfaceFlattenerTestCases.buildPyramid());
			sf.flatten(-0.01f);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
