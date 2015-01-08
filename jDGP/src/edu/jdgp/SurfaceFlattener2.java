package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class SurfaceFlattener2 {

	private PolygonMesh _mesh;
	private Graph _graph;
	private VecFloat _translatedEdges; //coordenadas de los ejes trasladados al origen 
	// private VecFloat _normalizedFacesNormals;
	
	public SurfaceFlattener2(PolygonMesh mesh) throws Exception {
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

	private VecFloat initialValue() throws Exception {
		// System.out.println("initialValue() dim: " + 3 * _mesh.getNumberOfFaces());
		VecFloat initialValue = new VecFloat(3 * _mesh.getNumberOfFaces());
		VecFloat faceNormals = facesNormals();
		for (int i = 0; i < faceNormals.size(); i++) {
			initialValue.pushBack(faceNormals.get(i));
		}
		return initialValue;
	}

	private void m(SparseMatrix m) throws Exception {
		int faces = _mesh.getNumberOfFaces();
		for (int i = 0; i < faces; i++) {
			VecInt neighbors = _mesh.getFaceNeighbors(i+1);
			if (neighbors.size() > 0) {
				for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z	
					m.add(3 * i + k, 3 * i + k, neighbors.size()); // $coordenadas de $n_h$ tiene el valor $|h^*|$
				}
				for (int j = 0; j < neighbors.size(); j++) {
					for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z
						m.add(3 * i + k, 3 * (neighbors.get(j) - 1) + k, -1); // $coordenadas de $n_f$ donde $f \in h^*$ tiene el valor $-1$
					}
				}
			}
		}
	}

	private float sqrNorm2(int iF, VecFloat faceNormals) {
		int index = 3 * iF;
		float norm = faceNormals.get(index) * faceNormals.get(index) +
					faceNormals.get(index+1) * faceNormals.get(index+1) +
						faceNormals.get(index+2) * faceNormals.get(index+2);
		return norm;
	}
	
	private float innerProduct(int iF1, int iF2, VecFloat faceNormals) {
		int idx1 = 3* iF1;
		int idx2 = 3* iF2;
		float innerProd = faceNormals.get(idx1) * faceNormals.get(idx2) +
							faceNormals.get(idx1+1) * faceNormals.get(idx2+1) +
								faceNormals.get(idx1+2) * faceNormals.get(idx2+2);
		return innerProd;
	}
	
	private void stats(int step, VecFloat faceNormals) throws Exception {
		int faces = _mesh.getNumberOfFaces();
		System.out.println("**********************************************************************");
		System.out.println("Step: " + step);
		
		for (int i = 0; i < faces; i++) {			
			System.out.println("****************");
			float norm = sqrNorm2(i, faceNormals);
			System.out.println("face: " + i + " norm^2: " + norm);			
			VecInt neighbors = _mesh.getFaceNeighbors(i+1);			
 			for (int j = 0; j < neighbors.size(); j++) {
 				int neighbor = neighbors.get(j) - 1;
 				float normNeighbor = sqrNorm2(neighbor, faceNormals); 				
 				float innerProd = innerProduct(i, neighbor, faceNormals);
 				float sqrt = (float)Math.sqrt(norm * normNeighbor);
 				// System.out.println("innerProd: " + innerProd + " sqrt: " + sqrt);
				System.out.println("alfa(" + i + " " + neighbor + "): " + 180 * Math.acos(innerProd/sqrt)/Math.PI);
			}
		}
	}
	
	private SparseMatrix gradientMatrix() throws Exception {
		SparseMatrix m = new SparseMatrix(3 * _mesh.getNumberOfFaces());
		m(m);
		return m;
	}
	
	// considero el gradiente de la condicion que preserva las normas de las normales a las caras: $\sum_{f \in F} (||n_f||^2-1)^2$ 
	private VecFloat unitaryNormalsCondition(VecFloat faceNormals) {
		VecFloat gradient = new VecFloat(faceNormals.size());
		int faces = _mesh.getNumberOfFaces();
		for (int i = 0; i < faces; i++) {
			for (int j = 0; j < 3; j++) {
				float norm = sqrNorm2(i,faceNormals);
				gradient.pushBack((norm - 1) * 2 * faceNormals.get(3 * i + j));
			}
		}
		return gradient;
	}
	
	public void flatten(int iterations, float lambda) throws Exception {
		_mesh._coord.dump();
		VecFloat currentValue = initialValue();
		// currentValue.dump();
		SparseMatrix gradient =  gradientMatrix();
		// gradient.dump();
		for (int i = 0; i < iterations; i++) {
			//stats(i, currentValue);
			currentValue.add(gradient.multiplyByVectorAndScalar(currentValue, lambda));			
			currentValue.addMultiple(unitaryNormalsCondition(currentValue), lambda);
		}
		currentValue.dump();
	}

	  /*	  
    point [
       1.633 -0.943 -0.667 # V0
       0.000  0.000  2.000 # V1
      -1.633 -0.943 -0.667 # V2
       0.000  1.886 -0.667 # V3
    ]
  }
  coordIndex [
    0 1 2 -1 # F0
    3 1 0 -1 # F1
    2 1 3 -1 # F2
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
	   
	  VecInt coordIndex = new VecInt(20);
	  coordIndex.pushBack(0);
	  coordIndex.pushBack(1);
	  coordIndex.pushBack(2);
	  coordIndex.pushBack(-1);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(1);
	  coordIndex.pushBack(0);
	  coordIndex.pushBack(-1);
	  coordIndex.pushBack(2);
	  coordIndex.pushBack(1);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(-1);

		  try {
			SurfaceFlattener2  pm = new SurfaceFlattener2(new PolygonMesh(coord, coordIndex));
			pm.flatten(200, -0.01f);
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
