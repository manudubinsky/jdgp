package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

// Calcula nuevas posiciones de los nodos de acuerdo a las normales calculadas en el step1 
public class SurfaceFlattenerStep2 {
	private PolygonMesh _mesh;
	private VecFloat _translatedEdges;
	private VecFloat _facesNormals;
	
	private void init(PolygonMesh mesh, VecFloat facesNormals) throws Exception {
		_mesh = mesh;
		_translatedEdges = PolygonMeshHelper.translateEdges(_mesh);
		_facesNormals = facesNormals;
	}	

	private VecFloat initialValue() throws Exception {
		VecFloat initialValue = new VecFloat(3 * (_mesh._nV + _mesh._graph.getNumberOfEdges()));
		for (int i = 0; i < _mesh.getNumberOfVertices(); i++) {
			for (int j = 0; j < 3; j++) {
				initialValue.pushBack(_mesh.getVertexCoord(i, j));
			}
		}
		VecFloat normalizedEdges = PolygonMeshHelper.normalizedEdges(_mesh, _translatedEdges);
		for (int i = 0; i < normalizedEdges.size(); i++) {
			initialValue.pushBack(normalizedEdges.get(i));
		}
		return initialValue;
	}

	// tratar de mantener las normas de los ejes... 
	private void edgesNormsCondition(SparseMatrix m, VecFloat edgesNorms) throws Exception {
		Graph g = _mesh._graph;
		// x_h
		int e_ijBasis = g.getNumberOfVertices();
		for (int i = 0; i < g.getNumberOfVertices(); i++) {
			VecInt vertexEdges = g.getVertexEdges(i);
			// vertexEdges.dump();
			for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z
				m.add(i * 3 + k, i * 3 + k, vertexEdges.size()); // $v_h$ tiene el valor $|h^{*}|$
			}
			for (int j = 0; j < vertexEdges.size(); j++) {
				int iE = vertexEdges.get(j);
				int neighbor = g.getNeighbor(i, iE);
				if (neighbor != -1) {
					for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z
						m.add(i * 3 + k, neighbor * 3 + k, -1); // $v_i$ tiene el valor $-1$ para cada $i \in h^{*}$
						int edgeIndex = (e_ijBasis + iE) * 3 + k;
						m.add(i * 3 + k, edgeIndex, (neighbor < i ? -1 : 1) * edgesNorms.get(iE)); // $e_{ih}$ tiene el valor $-d_{ih}$ si el eje es del tipo $(i,h)$ y $d_{ih}$ si el eje es del tipo $(h,j)$						
					}
				} else
					throw new Exception("Invalid neighbor iV: " + i + " iE: " + j);
			}
			
		}
		
		// e_ij
		for (int i = 0; i < g.getNumberOfEdges(); i++) {			
			float edgeNorm = edgesNorms.get(i);
			int iV0= g.getVertex0(i);
			int iV1= g.getVertex1(i);
			
			for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z				
				int row = 3 * (e_ijBasis + i) + k;
				//System.out.println("i: " + i + " k: " + k + " row: " + row);
				m.add(row, iV0 * 3 + k, edgeNorm); // $v_i$ tiene el valor $d_{ij}$
				m.add(row, iV1 * 3 + k, - edgeNorm); // $v_j$ tiene el valor $-d_{ij}$
				m.add(row, row, edgeNorm * edgeNorm); // $e_{ij}$ tiene el valor $d_{ij}^2$
			}
		}
	}

	// tratar de que las normales sean perpendiculares a las caras...
	private void normalsOrthogonalityCondition(SparseMatrix m, VecFloat edgesNorms) throws Exception {
		int faces = _mesh.getNumberOfFaces();
		for (int i = 0; i < faces; i++) {
			VecInt edges = _mesh.getFaceEdges(i+1);
			for (int j = 0; j < edges.size(); j++) {
				int iE = edges.get(j);				
				int iV0 = _mesh.getVertex0(iE);
				int iV1 = _mesh.getVertex1(iE);
				for (int k = 0; k < 3; k++) {
					float coef = _facesNormals.get(3*i+k)/(float)Math.pow(edgesNorms.get(iE),2); // d_{ij}^{-2} * n_f
					for (int l = 0; l < 3; l++) {
						m.add(3 * iV0 + k, 3 * iV0 + l, coef * _facesNormals.get(3*i+l));
						m.add(3 * iV0 + k, 3 * iV1 + l, - coef * _facesNormals.get(3*i+l));
						m.add(3 * iV1 + k, 3 * iV0 + l, - coef * _facesNormals.get(3*i+l));
						m.add(3 * iV1 + k, 3 * iV1 + l, coef * _facesNormals.get(3*i+l));
					}
				}				
			}
		}
	}

	private SparseMatrix linearConditions() throws Exception {
		VecFloat edgesNorms = PolygonMeshHelper.edgesNorms(_mesh, _translatedEdges);
		SparseMatrix m = new SparseMatrix(3 * (_mesh._nV + _mesh._graph.getNumberOfEdges()));
		edgesNormsCondition(m, edgesNorms);
		normalsOrthogonalityCondition(m, edgesNorms);
		return m;
	}

	// considero el gradiente de la condicion que preserva las normas de los e_ij: $\sum_{(i,j) \in E} (||e_{ij}||^2-1)^2$ 
	private VecFloat cannonicalVectorsUnitaryCondition(VecFloat currentValue) {		
		Graph g = _mesh._graph;
		VecFloat gradient = new VecFloat(currentValue.size(), 0);
		int e_ijBasis = g.getNumberOfVertices();
		int edges = g.getNumberOfEdges();
		for (int i = 0; i < edges; i++) {
			for (int j = 0; j < 3; j++) {
				float norm = PolygonMeshHelper.sqrNorm2(e_ijBasis + i,currentValue);
				//System.out.println("e_ijBasis + i: " + e_ijBasis + i + " norm: " + norm);
				gradient.set(3 * (e_ijBasis + i) + j,
								(norm - 1) * 2 * currentValue.get(3 * (e_ijBasis + i) + j));
			}
		}
		return gradient;
	}

	public VecFloat execute(PolygonMesh mesh, VecFloat facesNormals, SurfaceFlattenerParams params) throws Exception {		
		init(mesh, facesNormals);
		VecFloat currentValue = initialValue();
		SparseMatrix gradient =  linearConditions();
		float lambda = params.getLambda();
		while (params.hasToContinue()) {
			currentValue.add(gradient.multiplyByVectorAndScalar(currentValue, lambda));
			//currentValue.dump("step 2 add " + params.getCurrentIter());
			currentValue.addMultiple(cannonicalVectorsUnitaryCondition(currentValue), lambda);
			//currentValue.dump("step 2 addMultiple " + params.getCurrentIter());
		}
		return currentValue.head(3 * _mesh.getNumberOfVertices());
	}

}
