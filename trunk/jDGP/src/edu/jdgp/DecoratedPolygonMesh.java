package edu.jdgp;

import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecInt;

public class DecoratedPolygonMesh {
	private PolygonMesh _mesh;
	private VecInt _norm2;
	
	public DecoratedPolygonMesh(PolygonMesh mesh) {
		_mesh = mesh;
		_norm2();
	}

	private void _norm2() {
		_norm2 = new VecInt(_mesh._graph.getNumberOfEdges(), 0);
		for (int i = 0; i < _norm2.size(); i++) {
			
		}
	}
}
