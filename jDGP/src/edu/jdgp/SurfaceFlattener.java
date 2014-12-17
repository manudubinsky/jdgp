package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class SurfaceFlattener {
	private PolygonMesh _mesh;
	private Graph _graph;
	private VecFloat _translatedEdges; //coordenadas de los ejes trasladados al origen 
	// private VecFloat _normalizedFacesNormals;
	
	public SurfaceFlattener(PolygonMesh mesh) throws Exception {
		_mesh = mesh;
		_graph = mesh._graph;
		translateEdges();
	}

	private void translateEdges() throws Exception {
		int numEdges = _graph.getNumberOfEdges();
		_translatedEdges = new VecFloat(numEdges * 3); // por cada eje trasladado hay 3 coordenadas
		for (int i = 0; i < numEdges; i++) {
			int iV0 = _graph.getVertex0(i);
			int iV1 = _graph.getVertex1(i);
			for (int j = 0; j < 3; j++) {
				_translatedEdges.pushBack(_mesh.getVertexCoord(iV1, j) - _mesh.getVertexCoord(iV0, j));
			}
		}
		// _translatedEdges.dump();
	}
	
	// n_f
	private VecFloat facesNormals() throws Exception {
		int iE0, iE1;
		int faces = _mesh.getNumberOfFaces();
		VecFloat normalizedFacesNormals = new VecFloat(faces * 3);
		
		// System.out.println("_calculateFacesNormals faces: " + faces);
		
		for (int i = 0; i < faces; i++) {
			int iC = _mesh.getFaceFirstCorner(i+1);
			iE0 = _mesh.getEdge(iC);
			iE1 = _mesh.getEdge(_mesh.getNextCorner(iC));
			// calculo la normal a la cara (ie. el producto vectorial de los 2 ejes de la cara)
			// [iE0[1] * iE1[2] - iE0[2] * iE1[1], 
			//  - iE0[0] * iE1[2] + iE0[2] * iE1[0],
			//  iE0[0] * iE1[1] - iE0[1] * iE1[0]]
			// System.out.println("iE0[1]: " + _translatedEdges.get(iE0 + 1) + " iE1[2]: " + _translatedEdges.get(iE1 + 2) + " iE0[2]: " + _translatedEdges.get(iE0 + 2) + " iE1[1]: " + _translatedEdges.get(iE1 + 2)); 
			float coord0 = _translatedEdges.get(iE0 * 3 + 1) * _translatedEdges.get(iE1 * 3 + 2) - 
					_translatedEdges.get(iE0 * 3 + 2) * _translatedEdges.get(iE1 * 3 + 1);
			float coord1 = _translatedEdges.get(iE0 * 3 + 2) * _translatedEdges.get(iE1 * 3) - 
					_translatedEdges.get(iE0 * 3) * _translatedEdges.get(iE1 * 3 + 2);
			float coord2 = _translatedEdges.get(iE0 * 3) * _translatedEdges.get(iE1 * 3 + 1) - 
					_translatedEdges.get(iE0 * 3 + 1) * _translatedEdges.get(iE1 * 3);
			// System.out.println("iF: " + (i+1) + " iE0: " + iE0 + " iE1: " + iE1 + " coord0: " + coord0 + " coord1: " + coord1 + " coord2: " + coord2);
			float norm2 = (float)Math.sqrt(Math.pow(coord0, 2) + Math.pow(coord1, 2) + Math.pow(coord2, 2));
			normalizedFacesNormals.pushBack(coord0/norm2);
			normalizedFacesNormals.pushBack(coord1/norm2);
			normalizedFacesNormals.pushBack(coord2/norm2);
		}
		// _normalizedFacesNormals.dump();
		return normalizedFacesNormals;
	}
	
	// d_ij
	private VecFloat edgesNorms() {
		int numEdges = _graph.getNumberOfEdges();
		VecFloat edgesNorms = new VecFloat(numEdges);		
		for (int i = 0; i < numEdges; i++) {
			float norm = 0;
			for (int j = 0; j < 3; j++) {
				float coord = _translatedEdges.get(i * 3 + j);
				norm += Math.pow(coord, 2);
			}
			edgesNorms.pushBack(norm);
		}
		return edgesNorms;
	}
	
	// e_ij
	private VecFloat normalizedEdges() {
		int numEdges = _graph.getNumberOfEdges();
		VecFloat normalizedEdges = new VecFloat(numEdges);		
		for (int i = 0; i < numEdges; i++) {
			float norm2 = _norm2(i);
			for (int j = 0; j < 3; j++) {
				float coord = _translatedEdges.get(i * 3 + j);
				normalizedEdges.pushBack(coord/norm2);
			}			
		}
		return normalizedEdges;
	}

	private VecFloat initialValue() {
		VecFloat initialValue = new VecFloat(_mesh._nV + _mesh.getNumberOfFaces() + _graph.getNumberOfEdges()); // \bar{x},\bar{n},\bar{e}
		
		return initialValue;
	}

	private void m1(SparseMatrix m) {
		
	}

	private void m2(SparseMatrix m) {
		
	}

	private void m3(SparseMatrix m) {
		
	}

	private SparseMatrix gradientMatrix() {
		SparseMatrix m = new SparseMatrix(_mesh._nV + _mesh.getNumberOfFaces() + _graph.getNumberOfEdges()); // \bar{x},\bar{n},\bar{e}
		m1(m);
		m2(m);
		m3(m);
		return m;
	}
	
	public void flatten(int iterations) throws Exception {
		VecFloat currentValue = initialValue();
		SparseMatrix gradient =  gradientMatrix();
		for (int i = 0; i < iterations; i++) {
			currentValue.add(gradient.multiplyByVector(currentValue));
		}
	}
	
	public float _norm2(int iE) {
		float norm = 0;
		for (int i = 0; i < 3; i++) {
			float coord = _translatedEdges.get(iE * 3 + i);
			norm += Math.pow(coord, 2);
		}
		return (float)Math.sqrt(norm);
	}

	public VecFloat _nomalize(int iE) {
		VecFloat normalizedEdge = new VecFloat(3);
		float norm2 = _norm2(iE);
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
			SurfaceFlattener  pm = new SurfaceFlattener(new PolygonMesh(coord, coordIndex));
/*			
			for (int i = 0; i < 6; i++) {
				System.out.println("edge: " + i + " norm2: " + pm.norm2(i));
				pm.nomalize(i).dump();
			}
*/			
			
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

}