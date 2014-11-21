package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class DecoratedPolygonMesh {
	private PolygonMesh _mesh;
	private VecFloat _translatedEdges; //coordenadas de los ejes trasladados al origen 
	
	public DecoratedPolygonMesh(PolygonMesh mesh) throws Exception {
		_mesh = mesh;
		_translateEdges();
	}

	private void _translateEdges() throws Exception {
		Graph g = _mesh._graph;
		int numEdges = g.getNumberOfEdges();
		_translatedEdges = new VecFloat(numEdges * 3); // por cada eje trasladado hay 3 coordenadas
		for (int i = 0; i < numEdges; i++) {
			int iV0 = g.getVertex0(i);
			int iV1 = g.getVertex1(i);
			for (int j = 0; j < 3; j++) {
				_translatedEdges.pushBack(_mesh.getVertexCoord(iV1, j) - _mesh.getVertexCoord(iV0, j));
			}
		}
	}
	
	public float norm2(int iE) {
		float norm = 0;
		for (int i = 0; i < 3; i++) {
			float coord = _translatedEdges.get(iE * 3 + i);
			norm += Math.pow(coord, 2);
		}
		return (float)Math.sqrt(norm);
	}
	
	public VecFloat nomalize(int iE) {
		VecFloat normalizedEdge = new VecFloat(3);
		float norm2 = norm2(iE);
		for (int i = 0; i < 3; i++) {
			float coord = _translatedEdges.get(iE * 3 + i);
			normalizedEdge.pushBack(coord/norm2);
		}
		return normalizedEdge;
		
	}
	
	 /*	  
	  point [
	          1.633 -0.943 -0.667 # V0
	          0.000  0.000  2.000 # V1
	         -1.633 -0.943 -0.667 # V2
	          0.000  1.886 -0.667 # V3
	          0.000  0.000 -0.667 # V4
	  ]
	}
	coordIndex [
	  0 1 2 -1 # F0
	  3 4 2 -1 # F1
	]
	*/

	  public static void main(String[] args) {
		  VecFloat coord = new VecFloat(16);
		  coord.pushBack(1.633f);
		  coord.pushBack(-0.943f);
		  coord.pushBack(-0.667f);
		  coord.pushBack(0.000f);
		  coord.pushBack(0.000f);
		  coord.pushBack(2.000f);
		  coord.pushBack(-1.633f);
		  coord.pushBack(-0.943f);
		  coord.pushBack(-0.667f);
		  coord.pushBack(0.000f);
		  coord.pushBack(1.886f);
		  coord.pushBack(-0.667f);
		  coord.pushBack(0.000f);
		  coord.pushBack(0.000f);
		  coord.pushBack(-0.667f);
		   
		  VecInt coordIndex = new VecInt(10);
		  coordIndex.pushBack(0);
		  coordIndex.pushBack(1);
		  coordIndex.pushBack(2);
		  coordIndex.pushBack(-1);
		  coordIndex.pushBack(3);
		  coordIndex.pushBack(4);
		  coordIndex.pushBack(2);
		  coordIndex.pushBack(-1);

		  try {
			DecoratedPolygonMesh  pm = new DecoratedPolygonMesh(new PolygonMesh(coord, coordIndex));
			
			for (int i = 0; i < 6; i++) {
				System.out.println("edge: " + i + " norm2: " + pm.norm2(i));
				pm.nomalize(i).dump();
			}			
			
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

}