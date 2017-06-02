package edu.jdgp;

import edu.jdgp.DGP.PartitionUnionFind;
import edu.jdgp.DGP.VecInt;

public class SimpleFormIntegrator {
	int _nV;
	int _nE;
	int[][] _edges;      // vector de pares (i,j)
	float[] _edgeValues; // mapeo de los _edges en sus valores;
	int[] _tree;         // vector de indices de _edges
	int[] _vertex2Label; // vector que mapea cada vertice en su label
	int[][] _labelData;  //por cada vertice (indexado por label): 
							 // [nro. de vertice (inverso de _vertex2Label), 
							 //  label del padre]
	
	public SimpleFormIntegrator(int nV, int nE, int[][] edges, float[] edgeValues) throws Exception {
		_nV = nV;
		_nE = nE;		
		_edges = edges;
		_edgeValues = edgeValues;
		_vertex2Label = new int[_nV];
		_labelData = new int[_nV][2];
		for (int i = 0; i < 0; i++) {
			_vertex2Label[i] = -1;
		}
		buildTree();
	}

	// arma el árbol generador
	private void buildTree() throws Exception {
		VecInt[] tree = new VecInt[_nV];
		PartitionUnionFind unionFind = new PartitionUnionFind(_nV);
		for (int i = 0; i < _nE && unionFind.getNumberOfParts() > 1; i++) {			
			if (unionFind.hasToJoin(_edges[i][0], _edges[i][1])) { // particiones distintas => agregar el eje al arbol generador
				int iV0 = _edges[i][0];
				int iV1 = _edges[i][1];
				if (tree[iV0] == null) tree[iV0] = new VecInt(2);
				if (tree[iV1] == null) tree[iV1] = new VecInt(2);
				tree[iV0].pushBack(i);
				tree[iV1].pushBack(i);
			}
			System.out.println(i);
		}
	}
	
	public void integrate() {
		
	}

	public static void main(String[] args) {
		int nV = 4;
		int nE = 6;
		int[][] edges = new int[6][2];
		float[] edgeValues = new float[6];
		
		//tetraedro
		edges[0][0] = 0; edges[0][1] = 1; edgeValues[0] = 1;
		edges[1][0] = 0; edges[1][1] = 2; edgeValues[1] = 1;
		edges[2][0] = 0; edges[2][1] = 3; edgeValues[2] = 1;
		edges[3][0] = 1; edges[3][1] = 2; edgeValues[3] = 1;
		edges[4][0] = 1; edges[4][1] = 3; edgeValues[4] = 1;
		edges[5][0] = 2; edges[5][1] = 3; edgeValues[5] = 1;

		try {
			SimpleFormIntegrator integrator = new SimpleFormIntegrator(nV, nE, edges, edgeValues);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
