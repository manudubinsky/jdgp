package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrix;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class SurfaceFlattener3 {
	private PolygonMesh _mesh;
	private Graph _graph;
	private VecFloat _translatedEdges; //coordenadas de los ejes trasladados al origen
	private VecFloat _facesNormals;
	
	public SurfaceFlattener3(PolygonMesh mesh, VecFloat facesNormals) throws Exception {
		_mesh = mesh;
		_graph = mesh._graph;
		_facesNormals = facesNormals;
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
			edgesNorms.pushBack((float)Math.sqrt(norm));
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

	private float _norm2(int iE) {
		float norm = 0;
		for (int i = 0; i < 3; i++) {
			float coord = _translatedEdges.get(iE * 3 + i);
			norm += Math.pow(coord, 2);
		}
		return (float)Math.sqrt(norm);
	}

	private VecFloat initialValue() throws Exception {
		// System.out.println("initialValue() dim: " + 3 * (_mesh._nV + _mesh.getNumberOfFaces() + _graph.getNumberOfEdges()));

		VecFloat initialValue = new VecFloat(3 * (_mesh._nV + _graph.getNumberOfEdges()));
		for (int i = 0; i < _mesh.getNumberOfVertices(); i++) {
			for (int j = 0; j < 3; j++) {
				initialValue.pushBack(_mesh.getVertexCoord(i, j));
			}
		}
		VecFloat normalizedEdges = normalizedEdges();
		for (int i = 0; i < normalizedEdges.size(); i++) {
			initialValue.pushBack(normalizedEdges.get(i));
		}
		return initialValue;
	}

	private void m1(SparseMatrix m, VecFloat edgesNorms) throws Exception {
		// x_h
		int e_ijBasis = _graph.getNumberOfVertices();
		for (int i = 0; i < _graph.getNumberOfVertices(); i++) {
			VecInt vertexEdges = _graph.getVertexEdges(i);
			// vertexEdges.dump();
			for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z
				m.add(i * 3 + k, i * 3 + k, vertexEdges.size()); // $v_h$ tiene el valor $|h^{*}|$
			}
			for (int j = 0; j < vertexEdges.size(); j++) {
				int iE = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(i, iE);
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
		for (int i = 0; i < _graph.getNumberOfEdges(); i++) {			
			float edgeNorm = edgesNorms.get(i);
			int iV0= _graph.getVertex0(i);
			int iV1= _graph.getVertex1(i);
			
			for (int k = 0; k < 3; k++) { // para cada coordenada x,y,z				
				int row = 3 * (e_ijBasis + i) + k;
				//System.out.println("i: " + i + " k: " + k + " row: " + row);
				m.add(row, iV0 * 3 + k, edgeNorm); // $v_i$ tiene el valor $d_{ij}$
				m.add(row, iV1 * 3 + k, - edgeNorm); // $v_j$ tiene el valor $-d_{ij}$
				m.add(row, row, edgeNorm * edgeNorm); // $e_{ij}$ tiene el valor $d_{ij}^2$
			}
		}
	}

	private void m2(SparseMatrix m, VecFloat edgesNorms) throws Exception {
		int faces = _mesh.getNumberOfFaces();
		for (int i = 0; i < faces; i++) {
			VecInt edges = _mesh.getFaceEdges(i+1);
			for (int j = 0; j < edges.size(); j++) {
				int iE = edges.get(j);				
				int iV0 = _mesh.getVertex0(iE);
				int iV1 = _mesh.getVertex1(iE);
				for (int k = 0; k < 3; k++) {
					float absValue = _facesNormals.get(3*i+k)/edgesNorms.get(iE);
					//float absValue = _facesNormals.get(3*i+k);
					absValue *= absValue;
					m.add(3 * iV0 + k, 3 * iV0 + k, absValue);
					m.add(3 * iV0 + k, 3 * iV1 + k, -absValue);
					m.add(3 * iV1 + k, 3 * iV1 + k, absValue);
					m.add(3 * iV1 + k, 3 * iV0 + k, -absValue);
				}				
			}
		}
	}
	
	private SparseMatrix gradientMatrix() throws Exception {
		VecFloat edgesNorms = edgesNorms();
		SparseMatrix m = new SparseMatrix(3 * (_mesh._nV + _graph.getNumberOfEdges()));
		// m1(m, edgesNorms);
		m2(m, edgesNorms);
		return m;
	}

	private float sqrNorm2(int iF, VecFloat currentValue) {
		int index = 3 * iF;
		float norm = currentValue.get(index) * currentValue.get(index) +
					currentValue.get(index+1) * currentValue.get(index+1) +
						currentValue.get(index+2) * currentValue.get(index+2);
		return norm;
	}
	
	// considero el gradiente de la condicion que preserva las normas de los e_ij: $\sum_{(i,j) \in E} (||e_{ij}||^2-1)^2$ 
	private VecFloat unitaryCondition(VecFloat currentValue) {
		VecFloat gradient = new VecFloat(currentValue.size(), 0);
		int e_ijBasis = _graph.getNumberOfVertices();
		int edges = _graph.getNumberOfEdges();
		for (int i = 0; i < edges; i++) {
			for (int j = 0; j < 3; j++) {
				float norm = sqrNorm2(e_ijBasis + i,currentValue);
				//System.out.println("e_ijBasis + i: " + e_ijBasis + i + " norm: " + norm);
				gradient.set(3 * (e_ijBasis + i) + j,
								(norm - 1) * 2 * currentValue.get(3 * (e_ijBasis + i) + j));
			}
		}
		return gradient;
	}

	// n_f
	private VecFloat facesNormals(VecFloat translatedEdges) throws Exception {
		int iE0, iE1;
		int faces = _mesh.getNumberOfFaces();
		VecFloat normalizedFacesNormals = new VecFloat(faces * 3);
		
		// System.out.println("_calculateFacesNormals faces: " + faces);
		
		for (int i = 0; i < faces; i++) {
			int iC = _mesh.getFaceFirstCorner(i+1);
			iE0 = _mesh.getEdge(iC);
			iE1 = _mesh.getEdge(_mesh.getNextCorner(iC));
			float coord0 = translatedEdges.get(iE0 * 3 + 1) * translatedEdges.get(iE1 * 3 + 2) - 
					translatedEdges.get(iE0 * 3 + 2) * translatedEdges.get(iE1 * 3 + 1);
			float coord1 = translatedEdges.get(iE0 * 3 + 2) * translatedEdges.get(iE1 * 3) - 
					translatedEdges.get(iE0 * 3) * translatedEdges.get(iE1 * 3 + 2);
			float coord2 = translatedEdges.get(iE0 * 3) * _translatedEdges.get(iE1 * 3 + 1) - 
					translatedEdges.get(iE0 * 3 + 1) * translatedEdges.get(iE1 * 3);
			// System.out.println("iF: " + (i+1) + " iE0: " + iE0 + " iE1: " + iE1 + " coord0: " + coord0 + " coord1: " + coord1 + " coord2: " + coord2);
			float norm2 = (float)Math.sqrt(Math.pow(coord0, 2) + Math.pow(coord1, 2) + Math.pow(coord2, 2));
			normalizedFacesNormals.pushBack(coord0/norm2);
			normalizedFacesNormals.pushBack(coord1/norm2);
			normalizedFacesNormals.pushBack(coord2/norm2);
		}
		// _normalizedFacesNormals.dump();
		return normalizedFacesNormals;
	}

	private void stats(int step, VecFloat currentValue) throws Exception {
		int edges = _graph.getNumberOfEdges();
		System.out.println("**********************************************************************");
		System.out.println("Step: " + step);
		VecFloat edgesNorms = edgesNorms();
		VecFloat translatedEdges = new VecFloat(3*edges, 0);
		for (int i = 0; i < edges; i++) {			
			System.out.println("****************");			
			int iV0 = _mesh.getVertex0(i);
			int iV1 = _mesh.getVertex1(i);
			System.out.println("edge: " + i + " iV0: " + iV0 + " iV1: " + iV1);
			float norm = 0;
			for (int j = 0; j < 3; j++) {
				norm += Math.pow(currentValue.get(3 * iV1 + j) - currentValue.get(3 * iV0 + j), 2);
			}
			norm = (float)Math.sqrt(norm);
			for (int j = 0; j < 3; j++) {
				translatedEdges.set(3*i+j, currentValue.get(3 * iV1 + j) - currentValue.get(3 * iV0 + j)/norm);
			}
			System.out.println("edge: " + i + " original: " + edgesNorms.get(i) + " current: " + norm + " diff: " + (edgesNorms.get(i) - norm));
		}
		int faces = _mesh.getNumberOfFaces();
		VecFloat faceNormals = facesNormals(translatedEdges);		
		for (int i = 0; i < faces; i++) {
			System.out.println("****************");
			float innerProd = 0;
			for (int j = 0; j < 3; j++) {
				innerProd += faceNormals.get(3*i+j) * _facesNormals.get(3*i+j);
			}
			System.out.println("face: " + i + " alfa: " + 180 * Math.acos(innerProd)/Math.PI);
			/*
			float norm = 0;
			float normalNorm = 0;
			for (int j = 0; j < 3; j++) {
				normalNorm += Math.pow(faceNormals.get(3*i+j),2);
				norm += Math.pow(faceNormals.get(3*i+j) - _facesNormals.get(3*i+j),2);
			}
			norm = (float)Math.sqrt(norm);
			normalNorm = (float)Math.sqrt(normalNorm);
			System.out.println("face: " + i + " norm: " + norm + " normalNorm: " + normalNorm);
			*/
		}
	}

	public void flatten(int iterations, float lambda) throws Exception {
		VecFloat currentValue = initialValue();
		// currentValue.dump();
		SparseMatrix gradient =  gradientMatrix();
		
		// unitaryCondition(currentValue);
		//gradient.dump();
		for (int i = 0; i < iterations; i++) {
			stats(i, currentValue);
			currentValue.add(gradient.multiplyByVectorAndScalar(currentValue, lambda));
			//currentValue.addMultiple(unitaryCondition(currentValue), lambda);
		}
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

	  VecFloat normals = new VecFloat(9);
	  normals.pushBack(0.84899485f);
	  normals.pushBack(-0.49361727f);
	  normals.pushBack( 0.18519874f);
	  normals.pushBack(0.85200334f);
	  normals.pushBack(-0.48840618f);
	  normals.pushBack(0.18519843f);
	  normals.pushBack(0.8509167f);
	  normals.pushBack(-0.4912525f);
	  normals.pushBack(0.18270037f);
	  
	  
		  try {
			SurfaceFlattener3  pm = new SurfaceFlattener3(new PolygonMesh(coord, coordIndex),normals);
			pm.flatten(100, -0.01f);
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
