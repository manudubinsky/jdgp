package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;

public class BoundedIntersectionInducedCycles {
	public static void main(String[] args) throws Exception {
		WrlReader reader = new WrlReader("/home/manuel/workspace/jdgp/img/elephav.wrl");
		PolygonMesh mesh = reader.getMesh();
		Graph g = mesh.getGraph();
		int sing = 0;
		int bound = 0;
		for (int i = 0; i < g.getNumberOfEdges(); i++) {
			if (mesh.isBoundaryEdge(i)) {
				bound++;
				System.out.println("boundary: " + i);
			} else if (mesh.isSingularEdge(i)) {
				sing++;
				System.out.println("singular: " + i + " cant: " + mesh.getEdgeFaces(i).size());
			} 
		}
		System.out.println("total: " + g.getNumberOfEdges() + " sing: " + sing + " bound:" + bound);
	}
}
