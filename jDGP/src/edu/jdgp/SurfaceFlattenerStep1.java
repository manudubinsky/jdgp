package edu.jdgp;

import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

// Calcula un conjunto de vectores normales a las caras tales que los angulos entre
// las normales de caras adyacentes sean m�s cercanos a 0 
public class SurfaceFlattenerStep1 {
	private PolygonMesh _mesh;
	private VecFloat _translatedEdges;
	
	private void init(PolygonMesh mesh) throws Exception {
		_mesh = mesh;
		_translatedEdges = PolygonMeshHelper.translateEdges(_mesh);
	}	
	
	private VecFloat initialValue() throws Exception {
		VecFloat initialValue = new VecFloat(3 * _mesh.getNumberOfFaces());
		VecFloat faceNormals = PolygonMeshHelper.facesNormals(_mesh,_translatedEdges);
		for (int i = 0; i < faceNormals.size(); i++) {
			initialValue.pushBack(faceNormals.get(i));
		}
		return initialValue;
	}

	private void neighborNormalsCondition(SparseMatrix m) throws Exception {
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

	private SparseMatrix linearCondition() throws Exception {
		SparseMatrix m = new SparseMatrix(3 * _mesh.getNumberOfFaces());
		neighborNormalsCondition(m);
		return m;
	}

	// considero el gradiente de la condicion que preserva las normas de las normales a las caras: $\sum_{f \in F} (||n_f||^2-1)^2$ 
	private VecFloat unitaryNormalsCondition(VecFloat faceNormals) {
		VecFloat gradient = new VecFloat(faceNormals.size());
		int faces = _mesh.getNumberOfFaces();
		for (int i = 0; i < faces; i++) {
			for (int j = 0; j < 3; j++) {
				float norm = PolygonMeshHelper.sqrNorm2(i,faceNormals);
				gradient.pushBack((norm - 1) * 2 * faceNormals.get(3 * i + j));
			}
		}
		return gradient;
	}

	public VecFloat execute(PolygonMesh mesh, SurfaceFlattenerParams params) throws Exception {
		// mesh._coord.dump("ACA!!!");
		init(mesh);
		VecFloat facesNormals = initialValue();
		SparseMatrix gradient =  linearCondition();
		float lambda = params.getLambda();
		while (params.hasToContinue()) {
			facesNormals.add(gradient.multiplyByVectorAndScalar(facesNormals, lambda));
			facesNormals.addMultiple(unitaryNormalsCondition(facesNormals), lambda);
		}
		return facesNormals;
	}
	
}
