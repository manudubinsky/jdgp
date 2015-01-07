package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;

public class SurfaceFlattener {

	private PolygonMesh _mesh;
	private Graph _graph;
	
	public SurfaceFlattener(PolygonMesh mesh) throws Exception {
		_mesh = mesh;
		_graph = mesh._graph;
	}

	public void flatten(int iterations, float lambda) throws Exception {
		SurfaceFlattenerStep1 step1 = new SurfaceFlattenerStep1(); // modificar las normales a las caras
		SurfaceFlattenerStep2 step2 = new SurfaceFlattenerStep2(); // modificar los nodos de acuerdo a las normales del step1
		boolean hasToContinue = true;
		SurfaceFlattenerParams params = new SurfaceFlattenerParams(lambda, 20);
		while (params.hasToContinue()) {
			VecFloat facesNormals = step1.execute(_mesh, params);
			_mesh._coord = step2.execute(_mesh, facesNormals, params);
		}
	}

}
