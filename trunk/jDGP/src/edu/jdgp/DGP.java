package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:36:21 taubin>
//------------------------------------------------------------------------

import  java.util.*;

public class DGP extends DGP_h
{
  //////////////////////////////////////////////////////////////////////
  public static class VecInt  implements VecInt_h
  {
	private int _vecLen;
	private int _size;
	private int[] _vec;

	public VecInt() {
		_vecLen = 1024;
		_reset();
	}

	public VecInt(int N) {
		_vecLen = N;
		_reset();
	}
	
	private void _reset() {
		_vec = new int[_vecLen];
		_size = 0;
	}
	
	public void erase() {
		_size = 0;
	}

	public int size() {
		return _size;
	}

	public void pushBack(int v) {
		if (_size == _vecLen)
			_resize();
		_vec[_size++] = v;
	}

	private void _resize() {
		System.out.println("_resize: _vecLen: " + _vecLen);
		int[] newVec = new int[_vecLen*2];
		for (int i = 0; i < _vecLen; i++) {
			newVec[i] = _vec[i];
		}
		_vec = newVec;
		_vecLen *= 2;
	}
	
	public int get(int j) throws ArrayIndexOutOfBoundsException {
		if (j < 0 || j >= _size)
				throw new ArrayIndexOutOfBoundsException();
		return _vec[j];
	}

	public int getFront() throws ArrayIndexOutOfBoundsException {
		if (_size == 0)
			throw new ArrayIndexOutOfBoundsException();
		return _vec[0];
	}

	public int getBack() throws ArrayIndexOutOfBoundsException {
		if (_size == 0)
			throw new ArrayIndexOutOfBoundsException();
		return _vec[_size-1];
	}

	public void popBack() throws ArrayIndexOutOfBoundsException {
		if (_size == 0)
			throw new ArrayIndexOutOfBoundsException();
		_size--;
	}

	public void set(int j, int vj) throws ArrayIndexOutOfBoundsException {
		if (j < 0 || j >= _size)
			throw new ArrayIndexOutOfBoundsException();
		_vec[j] = vj;
	}

	public void swap(VecInt_h other) throws Exception {
	}
	
	public void dump() {
		for (int i = 0; i < _vecLen; i++) {
			System.out.print(" " + _vec[i]);
		}
		System.out.println();
	}
  }

/*
  public static void main(String[] args) {

	  VecFloat v = new VecFloat();
	for (int i = 0; i < 5000; i++) {		
		v.pushBack(i + 0.0f);
	}
	System.out.println("v[0]: " + v.get(0) + " v[1024]: " + v.get(1024) + " v[2048]: " + v.get(2048) + " v[4096]: " + v.get(4096));
  }
*/
  
  //////////////////////////////////////////////////////////////////////
  public static class VecFloat  implements VecFloat_h
  {
		private int _vecLen;
		private int _size;
		private float[] _vec;

	public VecFloat() {
		_vecLen = 1024;
		_reset();		
	}
	
	public VecFloat(int N) {
		_vecLen = N;
		_reset();				
	}

	private void _reset() {
		_vec = new float[_vecLen];
		_size = 0;
	}

	public void erase() {
		_size = 0;
	}

	public int size() {
		return _size;
	}

	public void pushBack(float v) {
		if (_size == _vecLen)
			_resize();
		_vec[_size++] = v;
	}

	private void _resize() {
		System.out.println("_resize: _vecLen: " + _vecLen);
		float[] newVec = new float[_vecLen*2];
		for (int i = 0; i < _vecLen; i++) {
			newVec[i] = _vec[i];
		}
		_vec = newVec;
		_vecLen *= 2;
	}

	public float get(int j) throws ArrayIndexOutOfBoundsException {
		if (j < 0 || j >= _size)
			throw new ArrayIndexOutOfBoundsException();
		return _vec[j];
	}

	public float getFront() throws ArrayIndexOutOfBoundsException {
		if (_size == 0)
			throw new ArrayIndexOutOfBoundsException();
		return _vec[0];
	}

	public float getBack() throws ArrayIndexOutOfBoundsException {
		if (_size == 0)
			throw new ArrayIndexOutOfBoundsException();
		return _vec[_size-1];
	}

	public void popBack() throws ArrayIndexOutOfBoundsException {
		if (_size == 0)
			throw new ArrayIndexOutOfBoundsException();
		_size--;
	}

	public void set(int j, float vj) throws ArrayIndexOutOfBoundsException {
		if (j < 0 || j >= _size)
			throw new ArrayIndexOutOfBoundsException();
		_vec[j] = vj;
	}

	public void swap(VecFloat_h other) throws Exception {
		// TODO Auto-generated method stub
		
	}

  }

  //////////////////////////////////////////////////////////////////////
/*
  public static void main(String[] args) {
	  try {
		Partition p = new Partition(10);
		p.dump();
		p.join(1,2);
		p.dump();
		p.join(3,4);
		p.dump();
		p.join(2,5);
		p.dump();
		p.join(2,4);
		p.dump();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
*/

  public static class Partition implements Partition_h
  { /*
		La implementacion la hago con dos estructuras:
		1) un vector int[N] en el que cada posicion representa un elem. del 
		   conjunto y el contenido de cada posicion es la particion a la que 
		   pertenece
		2) un vector VecInt[N] en el que cada posicion representa una 
		   particion y el contenido son los elementos de esa particion 
	*/
	  private int[] _elems;
	  private VecInt[] _parts;
	  private int _numElems;
	  private int _numParts;
	  private VecInt _emptyParts;
	  
	  public Partition(int n) throws Exception {
		  reset(n);
	  }
	  
	public void reset(int n) throws Exception {
		_numElems = n;
		_numParts = n;
		_elems = new int[n];
		_parts = new VecInt[n];
		_emptyParts = new VecInt(n / 10);
		for (int i = 0; i < _elems.length; i++) {
			_elems[i] = i;
			_parts[i] = new VecInt(1);			
			_parts[i].pushBack(i);
		}
	}

	public int getNumberOfElements() {
		return _numElems;
	}

	public int getNumberOfParts() {
		return _numParts;
	}

	public int find(int i) {
		if (i >= 0 && i < _numElems)
			return _elems[i];
		else
			return -1;
	}

	public int join(int i, int j) {
		// copio el vector de menor tamanio en el de mayor tamanio
		int source = getSize(i) <= getSize(j) ? i : j;
		int target = (source == i) ? j : i;
		int srcSize = _parts[source].size();
		for (int k = 0; k < srcSize; k++) {
			int elem = _parts[source].get(k);
			_parts[target].pushBack(elem); // agregar los elems en la nueva particion
			_elems[elem] = target; // setear la nueva particion del los elems
		}
		_parts[source] = new VecInt(1);
		_emptyParts.pushBack(source);
		return target;
	}

	public int getSize(int i) {
		if (i >= 0 && i < _numElems)
			return _parts[i].size();
		else
			return 0;
	}

	public void dump(){
		StringBuffer sb = new StringBuffer();
		sb.append("elems: "); 
		for (int i = 0; i < _elems.length; i++) {
			sb.append(" " + i + " = " + _elems[i] + " ");
		}
		System.out.println(sb);
		
		for (int i = 0; i < _parts.length; i++) {
			sb = new StringBuffer();
			sb.append("part: " + i);
			for (int j = 0; j < _parts[i].size(); j++) {
				sb.append(" " + _parts[i].get(j));
			}
			System.out.println(sb);				
		}
		
		sb = new StringBuffer();
		sb.append("emptyParts: ");
		for (int i = 0; i < _emptyParts.size(); i++) {
			sb.append(" " + _emptyParts.get(i));
		}
		System.out.println(sb);
	}
  }

  //////////////////////////////////////////////////////////////////////
  public class SplittablePartition
    extends Partition implements SplittablePartition_h
  {

	  public SplittablePartition(int n) throws Exception {
		  super(n);
	  }
	  
	public void split(int i) {
		// TODO Auto-generated method stub
		
	}

  }



  //////////////////////////////////////////////////////////////////////
  /*	  
  0 1 2 -1 # F0
  3 1 0  3 -1 # F1
  2 1 3 -1 # F2
  2 3 0 -1 # F3
*/
/*
  public static void main(String[] args) {
	  VecInt coordIndex = new VecInt(20);
	  coordIndex.pushBack(0);
	  coordIndex.pushBack(1);
	  coordIndex.pushBack(2);
	  coordIndex.pushBack(-1);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(1);
	  coordIndex.pushBack(0);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(-1);
	  coordIndex.pushBack(2);
	  coordIndex.pushBack(1);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(-1);
	  coordIndex.pushBack(2);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(0);
	  coordIndex.pushBack(-1);

	  try {
		Faces f = new Faces(3, coordIndex);
		System.out.println("vertices: " + f.getNumberOfVertices());
		System.out.println("faces: " + f.getNumberOfFaces());
		System.out.println("corners: " + f.getNumberOfCorners());
		System.out.println("face 2 size: " + f.getFaceSize(2));
		System.out.println("face 2 #corners: " + f.getNumberOfCorners(2));
		System.out.println("face 4 vertex 1: " + f.getFaceVertex(4, 1));
		
		System.out.println("getCornerFace(0): " + f.getCornerFace(0));
		System.out.println("getCornerFace(4): " + f.getCornerFace(4));
		System.out.println("getCornerFace(7): " + f.getCornerFace(7));
		System.out.println("getCornerFace(13): " + f.getCornerFace(13));
		System.out.println("getCornerFace(15): " + f.getCornerFace(15));
		System.out.println("getCornerFace(8): " + f.getCornerFace(8));
		
		System.out.println("getNextCorner(0): " + f.getNextCorner(0));
		System.out.println("getNextCorner(1): " + f.getNextCorner(1));
		System.out.println("getNextCorner(2): " + f.getNextCorner(2));
		System.out.println("getNextCorner(3): " + f.getNextCorner(3));
		System.out.println("getNextCorner(4): " + f.getNextCorner(4));
	  } catch (Exception e) {
		e.printStackTrace();
	  }
  }
*/  
  public static class Faces implements Faces_h
  {
	  private int _numVertices;
	  protected VecInt _coordIndex;
	  private VecInt _facesIndex;
	  
	// throws exception if(nV<0), if(coordIndex==null),
	// and if an element iV of coordIndex is iV<-1 or iV>=nV
	public Faces(int nV, VecInt coordIndex) throws Exception {
		if (nV < 0)
			throw new Exception("nV < 0");
		if (coordIndex == null)
			throw new Exception("coordIndex == null");
		
		_numVertices = nV;
		_coordIndex = coordIndex;
		_facesIndex = new VecInt(128);
				
		int coordSize = _coordIndex.size();
		int idx = 0;
		while (idx < coordSize) {
			int coordValue = _coordIndex.get(idx);
			while (coordValue != -1) {				
				if (coordValue < -1 || coordValue > _numVertices)
					throw new Exception("coordIndex[" + idx + "] = " + coordValue + " out of range");
				coordValue = _coordIndex.get(++idx);
			}
			_facesIndex.pushBack(idx);
			idx++;
		}
	}
	
	public int getNumberOfVertices() {
		return _numVertices;
	}

	public int getNumberOfFaces() {
		return _facesIndex.size();
	}

	public int getNumberOfCorners() { // including the -1's
		return _coordIndex.size();
	}
	
	public int getNumberOfCorners(int iF) {
		int beginIdx = iF == 1 ? 0 : (_facesIndex.get(iF - 2) + 1);
		int endIdx = _facesIndex.get(iF - 1);
		int size = endIdx - beginIdx;
		return size;
	}

	public int getFaceSize(int iF) throws Exception {
		if (iF > getNumberOfFaces())
			throw new Exception("Invalid face number");
		return getNumberOfCorners(iF);
	}

	public int getFaceFirstCorner(int iF) throws Exception {
		return getFaceVertex(iF, 1);
	}

	public int getFaceVertex(int iF, int j) throws Exception {
		if (iF > getNumberOfFaces())
			throw new Exception("Invalid face number");

		if (getNumberOfCorners(iF) < j)
			throw new Exception("Invalid face size");
		
		int beginIdx = iF == 1 ? 0 : (_facesIndex.get(iF - 2) + 1);		
		return beginIdx + j - 1;
	}

	public int getCornerFace(int iC) { // aca se podria hace busqueda binaria
		int face = -1;
		int i = 0;		
		int facesSize = getNumberOfFaces();
		boolean found = false;
		while (i < facesSize && !found) {
			if (_facesIndex.get(i) > iC) {
				face = i + 1;
				found = true;
			} else if (_facesIndex.get(i) == iC)
				found = true; // es un -1
			i++;
		}
		return face;
	}
	
	public int getNextCorner(int iC) throws Exception {
		int corner = -1;
		int face = getCornerFace(iC);
		// System.out.println("DBG face: " + face);
		if (face != -1) {
			int faceSize = getFaceSize(face);
			// System.out.println("DBG faceSize: " + faceSize);
			int firstCorner = getFaceFirstCorner(face);
			// System.out.println("DBG firstCorner: " + firstCorner);
			corner = ((iC - firstCorner) + 1) % faceSize + firstCorner;
		}
		return corner;
	}
	
  }


  //////////////////////////////////////////////////////////////////////
  /*
   N = 5
   1 - 2
   2 - 3
   3 - 4
   4 - 5
   1 - 5
   */
  /*
  public static void main(String[] args) {
	  Graph g = new Graph(5);
	  System.out.println("insertEdge(1, 2): " + g.insertEdge(1, 2));
	  System.out.println("insertEdge(2, 3): " + g.insertEdge(2, 3));
	  System.out.println("insertEdge(3, 4): " + g.insertEdge(3, 4));
	  System.out.println("insertEdge(4, 5): " + g.insertEdge(4, 5));
	  System.out.println("insertEdge(1, 5): " + g.insertEdge(1, 5));
	  
	  System.out.println("insertEdge(1, 5): " + g.insertEdge(1, 5));

	  System.out.println("getEdge(3, 4): " + g.getEdge(3, 4));
	  
	  System.out.println("getVertex0(2): " + g.getVertex0(2));
	  System.out.println("getVertex1(2): " + g.getVertex1(2));
	  
	  System.out.println("insertEdge(1, 5): " + g.insertEdge(3, 4));
	  System.out.println("insertEdge(1, 5): " + g.insertEdge(4, 5));
  }
  */
  
  public static class Graph implements Graph_h
  { /*
  	La implementacion la hago con un VecInt (_edges) que contenga a los pares de vertices de cada arista y 
  	un VecInt[N] (_v0Edges) que para cada vertice indique los indices de los ejes en los que dicho vertice es el v0 (esto se basa
  	en el hecho de que los pares de vertices (v0-v1) que definen un eje estan ordenados (ie v0 < v1)) 
   */
	  private VecInt _edges;
	  private VecInt[] _v0Edges;
	  private int _nV;
	  private int _nE;
	  
	  
	  public Graph() {
		  _nV = 0;
	  }
	  
	  public Graph(int N) {
		  _nV = N;
		  erase();
	  }
	  
	public void erase() {
		_edges = new VecInt(_nV); // estimativamente lo creo del tamaño de la cantidad de nodos 
		_v0Edges = new VecInt[_nV];
		_nE = 0;
	}

	public int getNumberOfVertices() {
		return _nV;
	}

	public int getNumberOfEdges() {
		return _nE;
	}

	private boolean _inRange(int iV0, int iV1) {
		return (0 <= iV0 && iV1 < _nV && (iV1 - iV0) > 0);
	}
	
	public int getEdge(int iV0, int iV1) {
		if (iV0 > iV1) {
			int swap = iV0;
			iV0 = iV1;
			iV1 = swap;
		}

		int index = -1;				
		if (_inRange(iV0, iV1)) { // que los nodos esten en el rango 1.._V
			if (_v0Edges[iV0] != null) {
				int i = 0;
				boolean found = false;
				while (i < _v0Edges[iV0].size() && !found) {
					int idx = _v0Edges[iV0].get(i);
					if (_edges.get(idx * 2 + 1) == iV1) {
						found = true;
						index = idx;
					}
					i++;
				}
			}			
		}
		
		return index;
	}

	public int insertEdge(int iV0, int iV1) {
		// System.out.println("insertEdge() iV0: " + iV0 + " iV1: " + iV1);
		if (iV0 > iV1) {
			int swap = iV0;
			iV0 = iV1;
			iV1 = swap;
		}

		if (getEdge(iV0, iV1) != -1 || !_inRange(iV0, iV1)) // si ya existe el eje o nodos fuera de rango, devolver -1 
			return -1;
			
		_edges.pushBack(iV0);
		_edges.pushBack(iV1);

		if (_v0Edges[iV0] == null)
			_v0Edges[iV0] = new VecInt(2);
				
		int index = _nE++;
		_v0Edges[iV0].pushBack(index);
		
		return index;
	}

	public int getVertex0(int iE) {
		return (0 < iE && iE < _nE) ? _edges.get(iE * 2) : -1;
	}

	public int getVertex1(int iE) {
		return (0 < iE && iE < _nE) ? _edges.get(iE * 2 + 1) : -1;
	}

	public void dump() {
		int size = _edges.size();
		int i = 0;
		while (i < size) {
			int iV0 = _edges.get(i++);
			int iV1 = _edges.get(i++);
			System.out.println(iV0 + " -> " + iV1);
		}
	}
  }

  //////////////////////////////////////////////////////////////////////
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
       2 3 0 -1 # F3
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
	  coordIndex.pushBack(2);
	  coordIndex.pushBack(3);
	  coordIndex.pushBack(0);
	  coordIndex.pushBack(-1);

	  try {
		PolygonMesh pm = new PolygonMesh(coord, coordIndex);
		for (int i = 0; i < 5; i++) {
			System.out.println("edgeFaces edge: " + (i+1) + " #edgeFaces: " + pm.getNumberOfEdgeFaces(i+1));
		}
	 } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  public static class PolygonMesh
    extends Faces implements PolygonMesh_h
  {
	  private int _nV;
	  private Graph _graph;
	  private VecFloat _coord;
	  private Vector<VecInt> _edgeFaces; // le asocia a cada eje sus Faces incidentes
	  
	  public PolygonMesh(VecFloat coord, VecInt  coordIndex) throws Exception {
		super(coord.size()/3, coordIndex);
		_nV = coord.size()/3;
		_coord = coord;
		_edgeFaces = new Vector<VecInt>(); ;
		_buildGraph();
	  }
	  
	private void _buildGraph() throws Exception {
		System.out.println("_buildGraph() begin... _nV: " + _nV);
		_graph = new Graph(_nV);
		// para cada Face agregar los ejes en el grafo
		System.out.println("_buildGraph() num faces: " + getNumberOfFaces());
		for (int i = 0; i < getNumberOfFaces(); i++) {
			int faceNum = i+1;
			int firstCorner = getFaceFirstCorner(faceNum);
			int currentCorner = firstCorner;
			int nextCorner = getNextCorner(currentCorner);
			while (nextCorner != firstCorner) {
				// System.out.println("_buildGraph() insertEdge face: " + i + " currentCorner: " + currentCorner + " nextCorner: " + nextCorner);
				_insertEdge(i, currentCorner, nextCorner);
				currentCorner = nextCorner;
				nextCorner = getNextCorner(currentCorner);
			}
			// System.out.println("_buildGraph() insertEdge face: " + i + " firstCorner: " + firstCorner + " currentCorner: " + currentCorner);
			_insertEdge(faceNum, firstCorner, currentCorner); //agregar el eje del primero al ultimo nodo de la Face
		}
		_graph.dump();
	}
	
	private void _insertEdge(int iF, int iC0, int iC1) {
		// _edgeFaces se base en que _graph.insertEdge(iV0, iV1) inserta de forma correlativa (ie.: primero el 1, despues el 2, etc.) 
		int iV0 = _coordIndex.get(iC0);
		int iV1 = _coordIndex.get(iC1);
		int edge = _graph.insertEdge(iV0, iV1);
		if (edge == -1) {
			edge = _graph.getEdge(iV0, iV1);
		}		
		// aca hay que agregar la face al _edgeFaces
		if (edge == _edgeFaces.size()) {
			_edgeFaces.add(edge, new VecInt(1));
		}
		_edgeFaces.get(edge).pushBack(iF); //agregar la Face asociada al eje
	}
	
	public float getVertexCoord(int iV, int j) throws Exception {		
		return _coord.get(iV * 3 + j);
	}

	public float getCornerCoord(int iC, int j) throws Exception {
		int iV = _coordIndex.get(iC);
		return getVertexCoord(iV, j);
	}

	public int getEdge(int iC) {		
		try {
			int iV0 = _coordIndex.get(iC);
			int iV1 = _coordIndex.get(getNextCorner(iC));
			return iV0 < iV1 ? _graph.getEdge(iV0,iV1) : _graph.getEdge(iV1,iV0);
		} catch (ArrayIndexOutOfBoundsException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -1;
	}

	public int getEdge(int iV0, int iV1) {
		return _graph.getEdge(iV0, iV1);
	}

	public int getVertex0(int iE) {
		return _graph.getVertex0(iE);
	}

	public int getVertex1(int iE) {
		return _graph.getVertex1(iE);
	}

	public int getNumberOfEdgeFaces(int iE) {		
		return _edgeFaces.get(iE-1).size();
	}

	public int getEdgeFace(int iE, int j) {
		return _edgeFaces.get(iE-1).get(j);
	}

	public boolean isRegular() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean hasBoundary() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBoundaryVertex(int iV) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRegularVertex(int iV) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSingularVertex(int iV) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isBoundaryEdge(int iE) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isRegularEdge(int iE) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSingularEdge(int iE) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
  }

}