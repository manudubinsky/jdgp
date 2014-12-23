package edu.jdgp;

import java.util.Iterator;

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

	private VecFloat initialValue() throws Exception {
		// System.out.println("initialValue() dim: " + 3 * (_mesh._nV + _mesh.getNumberOfFaces() + _graph.getNumberOfEdges()));

		VecFloat initialValue = new VecFloat(3 * (_mesh._nV + _mesh.getNumberOfFaces() + _graph.getNumberOfEdges())); // \bar{x},\bar{n},\bar{e}
		for (int i = 0; i < _mesh.getNumberOfVertices(); i++) {
			for (int j = 0; j < 3; j++) {
				initialValue.pushBack(_mesh.getVertexCoord(i, j));
			}
		}
		VecFloat faceNormals = facesNormals();
		for (int i = 0; i < faceNormals.size(); i++) {
			initialValue.pushBack(faceNormals.get(i));
		}
		VecFloat normalizedEdges = normalizedEdges();
		for (int i = 0; i < normalizedEdges.size(); i++) {
			initialValue.pushBack(normalizedEdges.get(i));
		}
		return initialValue;
	}
	
	/*
	$v_h$ tiene el valor $|h^{*}|$
	$v_i$ tiene el valor $-1$ para cada $i \in h^{*}$
	$e_{ih}$ tiene el valor $-d_{ih}$ si el eje es del tipo $(i,h)$ y $d_{ih}$ si el eje es del tipo $(h,j)$
	*/	
	private void m1(SparseMatrix m, VecFloat edgesNorms) throws Exception {
		// x_h
		int e_ijBasis = _graph.getNumberOfVertices() + _mesh.getNumberOfFaces();
		for (int i = 0; i < _graph.getNumberOfVertices(); i++) {
			VecInt vertexEdges = _graph.getVertexEdges(i);
			vertexEdges.dump();
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
				System.out.println("i: " + i + " k: " + k + " row: " + row);
				m.add(row, iV0 * 3 + k, edgeNorm); // $v_i$ tiene el valor $d_{ij}$
				m.add(row, iV1 * 3 + k, - edgeNorm); // $v_j$ tiene el valor $-d_{ij}$
				m.add(row, row, edgeNorm * edgeNorm); // $e_{ij}$ tiene el valor $d_{ij}^2$
			}
		}
	}

	private void m2(SparseMatrix m, VecFloat edgesNorms) {
		
	}

	private void m3(SparseMatrix m, VecFloat edgesNorms) {
		
	}

	private SparseMatrix gradientMatrix() throws Exception {
		VecFloat edgesNorms = edgesNorms();
		SparseMatrix m = new SparseMatrix(3 * (_mesh._nV + _mesh.getNumberOfFaces() + _graph.getNumberOfEdges())); // \bar{x},\bar{n},\bar{e}
		m1(m, edgesNorms);
		m2(m, edgesNorms);
		m3(m, edgesNorms);
		return m;
	}
	
	public void flatten(int iterations, float lambda) throws Exception {
		VecFloat currentValue = initialValue();
		// currentValue.dump();
		SparseMatrix gradient =  gradientMatrix();
		gradient.dump();
		for (int i = 0; i < iterations; i++) {
			currentValue.add(gradient.multiplyByVectorAndScalar(currentValue, lambda));
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

	public VecFloat _normalize(int iE) {
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
			pm.flatten(10, 0.01f);
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