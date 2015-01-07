package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class PolygonMeshHelper {

	public static VecFloat translateEdges(PolygonMesh mesh) throws Exception {
		Graph g = mesh._graph;
		int numEdges = g.getNumberOfEdges();
		VecFloat translatedEdges = new VecFloat(numEdges * 3); // por cada eje trasladado hay 3 coordenadas
		for (int i = 0; i < numEdges; i++) {
			int iV0 = g.getVertex0(i);
			int iV1 = g.getVertex1(i);
			for (int j = 0; j < 3; j++) {
				translatedEdges.pushBack(mesh.getVertexCoord(iV1, j) - mesh.getVertexCoord(iV0, j));
			}
		}
		return translatedEdges;
	}

	public static VecFloat facesNormals(PolygonMesh mesh) throws Exception {
		return facesNormals(mesh, translateEdges(mesh));
	}
	
	// n_f
	public static VecFloat facesNormals(PolygonMesh mesh, VecFloat translatedEdges) throws Exception {
		int iE0, iE1;
		int faces = mesh.getNumberOfFaces();
		VecFloat normalizedFacesNormals = new VecFloat(faces * 3);
		
		// System.out.println("_calculateFacesNormals faces: " + faces);
		
		for (int i = 0; i < faces; i++) {
			int iC = mesh.getFaceFirstCorner(i+1);
			iE0 = mesh.getEdge(iC);
			iE1 = mesh.getEdge(mesh.getNextCorner(iC));
			// calculo la normal a la cara (ie. el producto vectorial de los 2 ejes de la cara)
			// [iE0[1] * iE1[2] - iE0[2] * iE1[1], 
			//  - iE0[0] * iE1[2] + iE0[2] * iE1[0],
			//  iE0[0] * iE1[1] - iE0[1] * iE1[0]]
			// System.out.println("iE0[1]: " + _translatedEdges.get(iE0 + 1) + " iE1[2]: " + _translatedEdges.get(iE1 + 2) + " iE0[2]: " + _translatedEdges.get(iE0 + 2) + " iE1[1]: " + _translatedEdges.get(iE1 + 2)); 
			float coord0 = translatedEdges.get(iE0 * 3 + 1) * translatedEdges.get(iE1 * 3 + 2) - 
					translatedEdges.get(iE0 * 3 + 2) * translatedEdges.get(iE1 * 3 + 1);
			float coord1 = translatedEdges.get(iE0 * 3 + 2) * translatedEdges.get(iE1 * 3) - 
					translatedEdges.get(iE0 * 3) * translatedEdges.get(iE1 * 3 + 2);
			float coord2 = translatedEdges.get(iE0 * 3) * translatedEdges.get(iE1 * 3 + 1) - 
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

	public static VecFloat edgesNorms(PolygonMesh mesh) throws Exception {
		return edgesNorms(mesh, translateEdges(mesh));
	}
	
	// d_ij
	public static VecFloat edgesNorms(PolygonMesh mesh, VecFloat translatedEdges) {
		Graph g = mesh._graph;
		int numEdges = g.getNumberOfEdges();
		VecFloat edgesNorms = new VecFloat(numEdges);		
		for (int i = 0; i < numEdges; i++) {
			float norm = 0;
			for (int j = 0; j < 3; j++) {
				float coord = translatedEdges.get(i * 3 + j);
				norm += Math.pow(coord, 2);
			}
			edgesNorms.pushBack((float)Math.sqrt(norm));
		}
		return edgesNorms;
	}
	
	public static VecFloat normalizedEdges(PolygonMesh mesh) throws Exception {
		return normalizedEdges(mesh, translateEdges(mesh));
	}
	
	// e_ij
	public static VecFloat normalizedEdges(PolygonMesh mesh, VecFloat translatedEdges) {
		Graph g = mesh._graph;
		int numEdges = g.getNumberOfEdges();
		VecFloat normalizedEdges = new VecFloat(numEdges);		
		for (int i = 0; i < numEdges; i++) {
			float norm2 = norm2(i,translatedEdges);
			for (int j = 0; j < 3; j++) {
				float coord = translatedEdges.get(i * 3 + j);
				normalizedEdges.pushBack(coord/norm2);
			}			
		}
		return normalizedEdges;
	}

	public static VecFloat normalize(int iE, VecFloat translatedEdges) {
		VecFloat normalizedEdge = new VecFloat(3);
		float norm2 = norm2(iE,translatedEdges);
		for (int i = 0; i < 3; i++) {
			float coord = translatedEdges.get(iE * 3 + i);
			normalizedEdge.pushBack(coord/norm2);
		}
		return normalizedEdge;		
	}

	public static float norm2(int vecIdx, VecFloat vecSet) {
		return (float)sqrNorm2(vecIdx, vecSet);
	}
	
	public static float sqrNorm2(int vecIdx, VecFloat vecSet) {
		int index = 3 * vecIdx;
		float norm = vecSet.get(index) * vecSet.get(index) +
					vecSet.get(index+1) * vecSet.get(index+1) +
						vecSet.get(index+2) * vecSet.get(index+2);
		return norm;
	}
	
	public static float innerProduct(int vecIdx1, int vecIdx2, VecFloat vecSet) {
		int idx1 = 3* vecIdx1;
		int idx2 = 3* vecIdx2;
		float innerProd = vecSet.get(idx1) * vecSet.get(idx2) +
							vecSet.get(idx1+1) * vecSet.get(idx2+1) +
								vecSet.get(idx1+2) * vecSet.get(idx2+2);
		return innerProd;
	}

	public static void main(String[] args) {
		  try {
			// SurfaceFlattenerHelper.translateEdges(SurfaceFlattenerTestCases.buildPyramid()).dump();					
			  PolygonMeshHelper.facesNormals(SurfaceFlattenerTestCases.buildPyramid()).dump();
		 } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
