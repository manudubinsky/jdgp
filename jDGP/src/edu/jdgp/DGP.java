package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:36:21 taubin>
//------------------------------------------------------------------------

import  java.util.*;

import com.sun.org.apache.xpath.internal.operations.Bool;

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

	public VecInt(int N, int initValue) {
		_vecLen = N;
		init(initValue);
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
		// System.out.println("_resize: _vecLen: " + _vecLen);
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

	public int getPopBack() {
		return _vec[--_size];		
	}
	
	public void set(int j, int vj) throws ArrayIndexOutOfBoundsException {
		if (j < 0 || j >= _size)
			throw new ArrayIndexOutOfBoundsException();
		_vec[j] = vj;
	}

	public void swap(VecInt_h other) throws Exception {
	}
	
	public void dump() {
		System.out.println("*****");
		System.out.println("dump() _vecLen: " + _vecLen + " _size: " + _size);
		for (int i = 0; i < _vecLen; i++) {
			System.out.print(" " + _vec[i]);
		}
		System.out.println("");
		System.out.println("*****");
	}
	
	public void init(int initValue) {
		_reset();
		for (int i = 0; i < _vecLen; i++) {
			pushBack(initValue);
		}
	}
	
	public static VecInt fromWrlVecInt(mesh.VecInt v) {
		int size = v.size();
		VecInt convertedVec = new VecInt(size);
		for (int i = 0; i < size; i++) {
			convertedVec.pushBack(v.get(i));
		}
		return convertedVec;
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

	public VecFloat(int N, float initValue) {
		_vecLen = N;
		init(initValue);
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
		//System.out.println("_resize: _vecLen: " + _vecLen);
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

	public void dump() {
		System.out.println("*****");
		System.out.println("dump() _vecLen: " + _vecLen + " _size: " + _size);
		for (int i = 0; i < _size; i++) {
			System.out.print(" " + _vec[i]);
		}
		System.out.println("");
		System.out.println("*****");
	}

	public void dump(String tag) {
		System.out.println("*****");
		System.out.println(tag + " dump() _vecLen: " + _vecLen + " _size: " + _size);
		for (int i = 0; i < _size; i++) {
			System.out.print(" " + _vec[i]);
		}
		System.out.println("");
		System.out.println("*****");
	}

	public void init(float initValue) {
		_reset();
		for (int i = 0; i < _vecLen; i++) {
			pushBack(initValue);
		}
	}

	public void add(VecFloat v) throws Exception {
		if (_size != v.size())
			throw new Exception("Dimensions differ! this.size:" + _size + " v.size: " + v.size());
		for (int i = 0; i < _size; i++) {
			float value = get(i);
			set(i, value + v.get(i));
		}
	}

	public void addMultiple(VecFloat v, float lamda) throws Exception {
		if (_size != v.size())
			throw new Exception("Dimensions differ! this.size:" + _size + " v.size: " + v.size());
		for (int i = 0; i < _size; i++) {
			float value = get(i);
			set(i, value + v.get(i) * lamda);
		}
	}

	private boolean inRange(int index) {
		return (index >= 0 && index < _size);
	}
	
	// devuelve el subvector: [from,from+count-1]
	public VecFloat subVec(int from, int count) throws Exception {		
		if (!inRange(from) || !inRange(from + count - 1))
			throw new Exception("Not in range. _size: " + _size + " from: " + from + " count: " + count + " (from+count-1): " + (from+count-1));
		VecFloat subVec = new VecFloat(count);
		for (int i = from; i < from+count; i++) {
			subVec.pushBack(get(i));
		}
		return subVec;
	}
	
	// descarta los elementos del final. conserva los elementos [0,pos-1] 
	public VecFloat head(int pos){
		_size = pos;
		return this;
	}

	public static VecFloat fromWrlVecFloat(mesh.VecFloat v) {
		int size = v.size();
		VecFloat convertedVec = new VecFloat(size);
		for (int i = 0; i < size; i++) {
			convertedVec.pushBack(v.get(i));
		}
		return convertedVec;
	}

	public static mesh.VecFloat toWrlVecFloat(VecFloat v) {
		int size = v.size();
		mesh.VecFloat convertedVec = new mesh.VecFloat(size);
		for (int i = 0; i < size; i++) {
			convertedVec.pushBack(v.get(i));
		}
		return convertedVec;
	}

	public static void toWrlVecFloat(VecFloat src, mesh.VecFloat target) {
		int size = src.size();
		for (int i = 0; i < size; i++) {
			target.set(i, src.get(i));
		}
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
/*
  public static void main(String[] args) {
	  try {
		PartitionUnionFind p = new PartitionUnionFind(10);
		p.dump();
		System.out.println("(0,1)");
		p.join(0,1);
		p.dump();
		System.out.println("(1,2)");
		p.join(1,2);
		p.dump();
		System.out.println("(3,4)");
		p.join(3,4);
		p.dump();
		System.out.println("(4,5)");
		p.join(4,5);
		p.dump();
		System.out.println("(5,6)");
		p.join(5,6);		
		p.dump();
		System.out.println("(6,7)");
		p.join(6,7);		
		p.dump();
		System.out.println("(6,8)");
		p.join(6,8);		
		p.dump();
		System.out.println("(7,9)");
		p.join(7,9);		
		p.dump();
		System.out.println("(5,1)");
		p.join(5,1);		
		p.dump();
		System.out.println(p.find(0));
		p.dump();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
*/
  
  public static class PartitionUnionFind implements Partition_h
  {
	  private int[] _elems;
	  private int[] _partSizes;
	  private int _numElems;
	  private int _numParts;	  
	  
	  public PartitionUnionFind(int n) throws Exception {
		  reset(n);
	  }
	  
	public void reset(int n) throws Exception {
		_numElems = n;
		_numParts = n;
		_elems = new int[n];
		_partSizes = new int[n];
		for (int i = 0; i < _elems.length; i++) {
			_elems[i] = i;
			_partSizes[i] = 1;
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
			if (_elems[_elems[i]] == _elems[i]) { // si tiene bien seteado el representante de la particion (ie.: la raiz del arbol)
				return _elems[i];
			} else {
				_elems[i] = find(_elems[i]); //le asigno el representante de la particion a toda la rama
				return _elems[i];				
			}
		else
			return -1;
	}

	public boolean hasToJoin(int i, int j) {
		// obtengo los representates de las particiones de i y j
		int iPart = find(i);
		int jPart = find(j);
		int joinPart = join(i,j);
		return (iPart != joinPart) || (jPart != joinPart);
	}

	public int join(int i, int j) {
		// obtengo los representates de las particiones de i y j
		int minSizePart = find(i);
		int maxSizePart = find(j);
		if (minSizePart != maxSizePart) { // si no estan en la misma particion...
			if (_partSizes[minSizePart] > _partSizes[maxSizePart]) { // si estan al reves, los invierto  
				int aux = minSizePart;
				minSizePart = maxSizePart;
				maxSizePart = aux;
			}
			_elems[minSizePart] = maxSizePart;
			_partSizes[maxSizePart] += _partSizes[minSizePart];
			_numParts--;
		}
		return maxSizePart;
	}

	public int getSize(int i) {
		if (i >= 0 && i < _numElems)
			return _partSizes[i];
		else
			return 0;
	}

	public void dump(){
		System.out.println("_numParts: " + _numParts);
		
		StringBuffer sb = new StringBuffer();
		sb.append("elems: "); 
		for (int i = 0; i < _elems.length; i++) {
			sb.append(" " + i + " = " + _elems[i] + " ");
		}
		System.out.println(sb);		
	}
  }
  //////////////////////////////////////////////////////////////////////
  public static void main(String[] args) throws Exception {
	Graph g = new Graph(4);	  
	g.insertEdge(0, 1);
	g.insertEdge(0, 2);
	//g.insertEdge(0, 3);
	g.insertEdge(1, 2);
	g.insertEdge(1, 3);
	g.insertEdge(2, 3);
	SpanningTree s = new SpanningTree(g);
	SparseMatrix m = s.getTree();
	m.fullDump();
	System.out.println("******************");
	m.resize(g.getNumberOfEdges());
	m.fullDump();
	s.getTreeEdges().dump();
  }
  
  public static class SpanningTree {
	  private SparseMatrix _spannigTree;
	  private VecInt _vertex2Label;
	  private VecInt _label2Vertex;
	  int _nV, _nE, _label;
	  private VecInt _treeEdges;
	  
	  public SpanningTree (Graph graph) throws Exception {
		  _label = 0;
		  _nV = graph.getNumberOfVertices();
		  _nE = graph.getNumberOfEdges();		  
		  _vertex2Label = new VecInt(_nV,-1);
		  _label2Vertex = new VecInt(_nV,-1);
		  _treeEdges = new VecInt(_nV-1);
		  _spannigTree = new SparseMatrix(_nV-1);		  
		  build(graph);
	  }
	  
	  private boolean addVertex(int iV) {
		  boolean addVertex = false;
		  if (_vertex2Label.get(iV) == -1) {
			  _vertex2Label.set(iV, _label);
			  _label2Vertex.set(_label, iV);
			  _label++;
			  addVertex = true;
		  }
		  return addVertex;
	  }
	  
	  private int minLabel(int v0, int v1) {
		  return _vertex2Label.get(v0) < _vertex2Label.get(v1) ? _vertex2Label.get(v0) : _vertex2Label.get(v1);
	  }

	  private int maxLabel(int v0, int v1) {
		  return _vertex2Label.get(v0) > _vertex2Label.get(v1) ? _vertex2Label.get(v0) : _vertex2Label.get(v1);
	  }

	  private void build(Graph graph) throws Exception {
		  PartitionUnionFind unionFind = new PartitionUnionFind(_nV);
		  int edgeIdx = 0;
		  int vertexIdx = 0;
		  VecInt vertexes = new VecInt(_nV);
		  vertexes.pushBack(0);
		  addVertex(0);
		  while (vertexIdx < vertexes.size() && unionFind.getNumberOfParts() > 1) {
			  int v = vertexes.get(vertexIdx++);
			  VecInt vertexEdges = graph.getVertexEdges(v);
			  for (int j = 0; j < vertexEdges.size() && unionFind.getNumberOfParts() > 1; j++) {
				  int edge = vertexEdges.get(j);
				  int neighbor = graph.getNeighbor(v, edge);
				  if (neighbor >= 0 && unionFind.hasToJoin(v, neighbor)) { // particiones distintas => agregar el eje al arbol generador
						  if (addVertex(neighbor))  // vertice aun no visitado, agregar a la lista
							  vertexes.pushBack(neighbor);
						  _spannigTree.add(edgeIdx, minLabel(v,neighbor), -1);
						  _spannigTree.add(edgeIdx, maxLabel(v,neighbor), 1);
						  edgeIdx++;
						  _treeEdges.pushBack(edge);
				  }
			  }
		  }
	  }
	  
	  public SparseMatrix getTree () {
		  return _spannigTree;
	  }
	  
	  public int label2Vertex(int label) {
		  return _label2Vertex.get(label);
	  }
	  
	  public int vertex2Label(int iV) {
		  return _vertex2Label.get(iV);
	  }

	  public VecInt getTreeEdges() {
		  return _treeEdges;
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
  3 1 0 -1 # F1
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
		System.out.println("faces: " + f.getNumberOfFaces());
		System.out.println("getFaceFirstCorner(1): " + f.getFaceFirstCorner(1));
		int iC = f.getFaceFirstCorner(2);
		System.out.println("getFaceFirstCorner(2): " + iC);
		iC = f.getNextCorner(iC);
		System.out.println("getNextCorner: " + iC);
		iC = f.getNextCorner(iC);
		System.out.println("getNextCorner: " + iC);
		iC = f.getNextCorner(iC);
		System.out.println("getNextCorner: " + iC);
		iC = f.getNextCorner(iC);
		System.out.println("getNextCorner: " + iC);
		iC = f.getNextCorner(iC);
		System.out.println("getNextCorner: " + iC);

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
		// System.out.println("getNumberOfCorners iF: " + iF);
		int beginIdx = iF == 1 ? 0 : (_facesIndex.get(iF - 2) + 1);
		// System.out.println("getNumberOfCorners iF: " + iF + " beginIdx: " + beginIdx);
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
//	  System.out.println("insertEdge(0, 1): " + g.insertEdge(0, 1));
	  System.out.println("insertEdge(1, 2): " + g.insertEdge(1, 2));
//	  System.out.println("insertEdge(2, 3): " + g.insertEdge(2, 3));
	  System.out.println("insertEdge(3, 4): " + g.insertEdge(3, 4));
	  System.out.println("insertEdge(0, 4): " + g.insertEdge(0, 4));
	  	  
	  System.out.println("isConnected?: " + g.isConnected());
  }
*/
  
  public static class Graph implements Graph_h
  { /*
  	La implementacion la hago con un VecInt (_edges) que contenga a los pares de vertices de cada arista y 
  	un VecInt[N] (_v0Edges) que para cada vertice indique los indices de los ejes en los que dicho vertice es el v0 (esto se basa
  	en el hecho de que los pares de vertices (v0-v1) que definen un eje estan ordenados (ie v0 < v1)) 
   */
	  private VecInt _edges;
	  private VecInt[] _vertexEdges;
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
		_vertexEdges = new VecInt[_nV];
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
			if (_vertexEdges[iV0] != null) {
				int i = 0;
				boolean found = false;
				while (i < _vertexEdges[iV0].size() && !found) {
					int idx = _vertexEdges[iV0].get(i);
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

		if (_vertexEdges[iV0] == null)
			_vertexEdges[iV0] = new VecInt(2);

		if (_vertexEdges[iV1] == null)
			_vertexEdges[iV1] = new VecInt(2);

		int index = _nE++;
		_vertexEdges[iV0].pushBack(index);
		_vertexEdges[iV1].pushBack(index);
		
		return index;
	}

	public int getVertex0(int iE) {
		return (0 <= iE && iE < _nE) ? _edges.get(iE * 2) : -1;
	}

	public int getVertex1(int iE) {
		return (0 <= iE && iE < _nE) ? _edges.get(iE * 2 + 1) : -1;
	}

	public int getNeighbor(int iV, int iE) {
		int iV0 = getVertex0(iE);
		int iV1 = getVertex1(iE);
		return iV == iV0 ? 
				iV1 : 
					iV == iV1 ? 
							iV0 : 
								-1;
	}

	public VecInt getVertexEdges(int iV) {
		return _vertexEdges[iV];
	}
	
	// devuelve el nodo con menor cantidad de ejes incidentes
	public int _vertexLeastEdges() {
		int iV = -1;
		int i = 0;
		boolean finished = false;
		while (i < _nV && !finished) {
			if (_vertexEdges[i] == null || _vertexEdges[i].size() == 0) {
				iV = -1;
				finished = true;
			} else if (iV == -1 || _vertexEdges[i].size() < _vertexEdges[iV].size()) {
				iV = i;
			}
			i++;
		}		
		return iV;
	}
	
	public boolean isConnected() {
		boolean isConnected = false;
		VecInt visited = new VecInt(_nV, 0);
		VecInt haveToVisit = new VecInt(_nV);		
		int firstVertex = _vertexLeastEdges(); // inicializo con el nodo con menor cantidad de ejes
		if (firstVertex > -1) {
			haveToVisit.pushBack(firstVertex);
			visited.set(firstVertex, 1);
			while (haveToVisit.size() > 0) {
				int currentVertex = haveToVisit.getPopBack();
				for (int i = 0; i < _vertexEdges[currentVertex].size(); i++) {
					int iE = _vertexEdges[currentVertex].get(i);
					int iV0 = getVertex0(iE);
					int iV1 = getVertex1(iE);
					if (visited.get(iV0) == 0) {
						haveToVisit.pushBack(iV0);
						visited.set(iV0, 1);
					}
					if (visited.get(iV1) == 0) {
						haveToVisit.pushBack(iV1);
						visited.set(iV1, 1);
					}
				}
			}
			int i = 0;
			boolean finished = false;
			while (i < _nV && !finished) {
				if (visited.get(i) == 0)
					finished = true;
				else
					i++;
			}
			if (i == _nV)
				isConnected = true;
		}
		return isConnected;
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
          0.000  1.886 -0.667 # V4
  ]
}
coordIndex [
  0 1 2 -1 # F0
  3 4 2 -1 # F1
]
*/
/*
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
	  coord.pushBack(1.886f);
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
		PolygonMesh pm = new PolygonMesh(coord, coordIndex);
		for (int i = 0; i < 2; i++) {
			System.out.println("edgeFaces edge: " + i + " #edgeFaces: " + pm.getNumberOfEdgeFaces(i));
		}
		System.out.println("isRegular?: " + pm.isRegular());
		System.out.println("hasBoundary?: " + pm.hasBoundary());
		
	 } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
*/
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
/*
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
		for (int i = 0; i < pm.getNumberOfFaces(); i++) {
			//pm.getFaceEdges(i+1).dump();
			pm.getFaceNeighbors(i+1).dump();
		}
		
//		for (int i = 0; i < 6; i++) {
//			System.out.println("edgeFaces edge: " + i + " #edgeFaces: " + pm.getNumberOfEdgeFaces(i));
//		}
//		System.out.println("isRegular?: " + pm.isRegular());
//		System.out.println("hasBoundary?: " + pm.hasBoundary());
		
	 } catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }
*/  
  public static class PolygonMesh
    extends Faces implements PolygonMesh_h
  {
	  protected int _nV;
	  protected Graph _graph;
	  protected VecFloat _coord;
	  private Vector<VecInt> _edgeFaces; // le asocia a cada eje sus Faces incidentes
	  private static final int BOUNDARY_TYPE = 1;
	  private static final int SINGULAR_TYPE = 2;
	  private Boolean _isRegular;
	  private Boolean _hasBoundary;
	  
	  public PolygonMesh(VecFloat coord, VecInt  coordIndex) throws Exception {
		super(coord.size()/3, coordIndex);
		_nV = coord.size()/3;
		_coord = coord;
		_edgeFaces = new Vector<VecInt>(); ;
		_buildGraph();
	  }
	  
	private void _buildGraph() throws Exception {
		// System.out.println("_buildGraph() begin... _nV: " + _nV);
		_graph = new Graph(_nV);
		// para cada Face agregar los ejes en el grafo
		// System.out.println("_buildGraph() num faces: " + getNumberOfFaces());
		for (int i = 0; i < getNumberOfFaces(); i++) {
			int faceNum = i+1;
			int firstCorner = getFaceFirstCorner(faceNum);
			int currentCorner = firstCorner;
			int nextCorner = getNextCorner(currentCorner);
			while (nextCorner != firstCorner) {
				// System.out.println("_buildGraph() insertEdge face: " + i + " currentCorner: " + currentCorner + " nextCorner: " + nextCorner);
				_insertEdge(faceNum, currentCorner, nextCorner);
				currentCorner = nextCorner;
				nextCorner = getNextCorner(currentCorner);
			}
			// System.out.println("_buildGraph() insertEdge face: " + i + " firstCorner: " + firstCorner + " currentCorner: " + currentCorner);
			_insertEdge(faceNum, firstCorner, currentCorner); //agregar el eje del primero al ultimo nodo de la Face
		}
		// _graph.dump();
	}
	
	private void _insertEdge(int iF, int iC0, int iC1) {
		// _edgeFaces se base en que _graph.insertEdge(iV0, iV1) inserta de forma correlativa (ie.: primero el 1, despues el 2, etc.) 
		int iV0 = _coordIndex.get(iC0);
		int iV1 = _coordIndex.get(iC1);
		int edge = _graph.insertEdge(iV0, iV1);
		// System.out.println("_insertEdge() iF: " + iF + " iV0: " + iV0 + " iV1:" + iV1 + " edge: " + edge);
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
			return _graph.getEdge(iV0,iV1);
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
		return _edgeFaces.get(iE).size();
	}

	public int getEdgeFace(int iE, int j) {
		return _edgeFaces.get(iE-1).get(j);
	}

	public boolean isRegular() {
		if (_isRegular == null) {
			_isRegular = true;
			// primero analizo los ejes
			int i = 0;
			boolean finished = false;
			while (i < _edgeFaces.size() && !finished) {
				try {
					if (!isRegularEdge(i)) {
						_isRegular = false;
						finished = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}

			if (_isRegular) { // ahora hay que analizar los vertices
				i = 0;
				finished = false;
				while (i < _nV && !finished) {
					try {
						if (!isRegularVertex(i)) {
							_isRegular = false;
							finished = true;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					i++;
				}				
			}
		}
		return _isRegular.booleanValue();
	}

	public boolean hasBoundary() {
		if (_hasBoundary == null) {
			_hasBoundary = false;
			int i = 0;
			boolean finished = false;
			while (i < _edgeFaces.size() && !finished) {
				try {
					if (isBoundaryEdge(i)) {
						_hasBoundary = true;
						finished = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				i++;
			}
		}
		return _hasBoundary.booleanValue();
	}

	public boolean isBoundaryVertex(int iV) throws Exception {
		return (getVertexType(iV) & BOUNDARY_TYPE) == BOUNDARY_TYPE;
	}

	public boolean isRegularVertex(int iV) throws Exception {
		return !isSingularVertex(iV);
	}

	public boolean isSingularVertex(int iV) throws Exception {
		return (getVertexType(iV) & SINGULAR_TYPE) == SINGULAR_TYPE;
	}

	public int getVertexType(int iV) throws Exception {
		int vertexType = 0;
		VecInt vertexEdges = _graph.getVertexEdges(iV);
		for (int i = 0; i < vertexEdges.size(); i++) {
			if ((vertexType & BOUNDARY_TYPE) == 0 && isBoundaryEdge(i))
				vertexType |= BOUNDARY_TYPE; // marco el nodo como borde
			
			if ((vertexType & SINGULAR_TYPE) == 0 && isSingularEdge(i))
				vertexType |= SINGULAR_TYPE; // marco el nodo como singular				
		}
		
		if ((vertexType & SINGULAR_TYPE) == 0) // hay que analizar si el subgrafo dual es conexo		
			vertexType |= !_vertexDualSubgraph(iV).isConnected() ? SINGULAR_TYPE : 0;

		return vertexType;
	}
	
	public Graph _vertexDualSubgraph(int iV) throws Exception {		
		//conseguir los ejes incidentes al nodo
			//hacer un map que permita indexar las caras asociadas a los ejes:
				//la key es el nro. de cara y el value es un nro. secuencial que identifique a esa cara como nodo del subgrafo dual

		VecInt vertexEdges = _graph.getVertexEdges(iV);
		Map<Integer, Integer> faceToVertex = new HashMap<Integer, Integer>();
		int vertexIndex = 0;
		for (int i = 0; i < vertexEdges.size(); i++) {
			VecInt edgeFaces = _edgeFaces.get(vertexEdges.get(i));
			for (int j = 0; j < edgeFaces.size(); j++) {
				int iF = edgeFaces.get(j);
				if (!faceToVertex.containsKey(iF)) {
					faceToVertex.put(iF, vertexIndex++);
				}
			}
		}

		//con el map se puede construir el grafo pq ya se conocen las caras-nodos
		//para cada eje incidente al nodo, si es regular, agregar un eje entre las caras-nodos que corresponden en el grafo
		Graph dualGraph = new Graph(faceToVertex.size());
		for (int i = 0; i < vertexEdges.size(); i++) {
			int iE = vertexEdges.get(i);
			if (isRegularEdge(iE)) {
			//agrego un eje en el grafo dual
				VecInt edgeFaces = _edgeFaces.get(iE);
				dualGraph.insertEdge(faceToVertex.get(edgeFaces.get(0)),
										faceToVertex.get(edgeFaces.get(1)));
			}
		}
		
		
		return dualGraph;
	}
	
	public boolean isBoundaryEdge(int iE) throws Exception {
		return _edgeFaces.get(iE).size() == 1;
	}

	public boolean isRegularEdge(int iE) throws Exception {
		return _edgeFaces.get(iE).size() == 2;
	}

	public boolean isSingularEdge(int iE) throws Exception {
		return _edgeFaces.get(iE).size() > 2;
	}
	
	public VecInt getFaceEdges(int iF) throws Exception {
		VecInt edges = new VecInt(3);
		int totalEdges = getFaceSize(iF);
		int iC = getFaceFirstCorner(iF);
		for (int i = 0; i < totalEdges; i++) {
			edges.pushBack(getEdge(iC));
			iC = getNextCorner(iC);
		}
		return edges;
	}
	public VecInt getFaceNeighbors(int iF) throws Exception {
		VecInt neighbors = new VecInt(3);
		VecInt faceEdges = getFaceEdges(iF); 
		for (int i = 0; i < faceEdges.size(); i++) {
			int edge = faceEdges.get(i);
			for (int j = 0; j < _edgeFaces.get(edge).size(); j++) {
				int face = _edgeFaces.get(edge).get(j);
				if (face != iF) {
					neighbors.pushBack(face);
				}
			}
		}
		return neighbors;
	}
  }

/*
    1 0 0
    0 1 0
    0 0 1
*/
  
 /* 
  public static void main(String[] args) {
	  int dim = 3;
	 SparseMatrix m = new SparseMatrix(dim);
	 m.set(0, 0, 3);
	 m.set(0, 1, 1);
	 m.set(1, 1, 3);
	 m.set(2, 2, 3);
	 VecFloat v = new VecFloat(3);
	 v.pushBack(1);
	 v.pushBack(2);
	 v.pushBack(3);
	 VecFloat v2 = m.multiplyByVector(v);
	 v2.dump();
	 
	 m.add(0, 0, 1);
	 m.add(1, 1, 1);
	 m.add(2, 2, 1);
	 m.add(0, 2, 1);
	 
	 for (int i = 0; i < dim; i++) {
		 for (int j = 0; j < dim; j++) {
			 System.out.println("m[" + i + "," +  j + "] = " + m.get(i, j));
		 }		
	}
  }
*/  
  public static class SparseMatrix
  {
	  int _rows;
	  int _cols;
	  private VecInt[] _colIndices;
	  private VecFloat[] _values;
	  
	  public SparseMatrix(int rows) {
		  _rows = rows;
		  _init();
	  }
	  
	  private void _init() {
		  _colIndices = new VecInt[_rows];
		  _values = new VecFloat[_rows];
	  }
	  
	  public void set(int row, int col, float value) {
		  if (_colIndices[row] == null) {
			  _colIndices[row] = new VecInt(1);
			  _values[row] = new VecFloat(1);
		  }
		  // System.out.println("set(...) row: " + row + " col: " + col + " value: " + value);
		  _colIndices[row].pushBack(col);
		  _values[row].pushBack(value);
		  if (_cols < col)
			  _cols = col;
	  }
	  
	  public float get(int row, int col) {
		  float value = 0;
		  // System.out.println("get(...) row: " + row + " col: " + col);
		  if (_colIndices[row] != null) {
			  // System.out.println("get(...) not null!!! _colIndices[row]: " + _colIndices[row].size());
			  int colIndex = -1;
			  // _colIndices[row].dump();
			  for (int i = 0; i < _colIndices[row].size() && colIndex < 0; i++) {
				  // System.out.println("get(...) _colIndices[row].get(i): " + _colIndices[row].get(i));
				  if (_colIndices[row].get(i) == col)
					  colIndex = i;
			  }
			  if (colIndex >= 0 )
				  value = _values[row].get(colIndex);
		  }
		  return value;
	  }
	  
	  public void add(int row, int col, float value) {
		  if (value > 0.0000001f || value < -0.0000001f) {
			  if (_colIndices[row] != null) {			  
				  float previousValue = 0;
				  int colIndex = -1;
				  for (int i = 0; i < _colIndices[row].size() && colIndex < 0; i++) {
					  if (_colIndices[row].get(i) == col)
						  colIndex = i;
				  }
				  if (colIndex >= 0 ) {
					  previousValue = _values[row].get(colIndex);
					  _values[row].set(colIndex, previousValue + value);
				  } else {
					  set(row, col, value);
				  }
			  } else {
				  set(row, col, value);
			  }
		  }
	  }
	  
	  public VecFloat multiplyByVector(VecFloat v) {
		  VecFloat resultVec = new VecFloat(v.size(), 0);		  
		  for (int i = 0; i < _colIndices.length; i++) {
			  float value = 0;
			  for (int j = 0; j < _colIndices[i].size(); j++) {
				  value += _values[i].get(j) * v.get(_colIndices[i].get(j));
			  }
			  resultVec.set(i, value);
		  }
		  return resultVec;
	  }

	  public VecFloat multiplyByVectorAndScalar(VecFloat v, float scalar) {
		  VecFloat resultVec = new VecFloat(v.size(), 0);		  
		  for (int i = 0; i < _colIndices.length; i++) {
			  float value = 0;
			  if (_colIndices[i] != null) {
				  for (int j = 0; j < _colIndices[i].size(); j++) {
					  value += _values[i].get(j) * v.get(_colIndices[i].get(j));
				  }
			  }
			  resultVec.set(i, scalar * value);
		  }
		  return resultVec;
	  }

	  public void resize(int rows) {
		  VecInt[] newColIndices = new VecInt[rows];
		  VecFloat[] newValues = new VecFloat[rows];
		  for (int i = 0; i < _colIndices.length; i++) {
			  int rowSize = _colIndices[i].size();
			  newColIndices[i] = new VecInt(rowSize, -1);
			  newValues[i] = new VecFloat(rowSize, -1);
			  for (int j = 0; j < rowSize; j++) {
				  newColIndices[i].set(j, _colIndices[i].get(j));
				  newValues[i].set(j, _values[i].get(j));
			  }
		  }
		  _colIndices = newColIndices;
		  _values = newValues;
		  _rows = rows;
	  }
	  
	  public void fullDump() {
		  for (int i = 0; i < _rows; i++) {
			  int currentCol = 0;
			  StringBuffer row = new StringBuffer();
			  if (_colIndices[i] != null) {
				  for (int j = 0; j < _colIndices[i].size(); j++) {
					  while (currentCol++ < _colIndices[i].get(j)) {
						  row.append("\t0");						  
					  }
					  row.append("\t" + _values[i].get(j));
				  } 
			  }
			  for (int j = currentCol -1; j < _cols; j++) {
				  row.append("\t0");
			  }
			  System.out.println(row);
		  }
		  
	  }
	  
	  public void dump() {
		  for (int i = 0; i < _rows; i++) {
			  if (_colIndices[i] != null) {
				  for (int j = 0; j < _colIndices[i].size(); j++) {
					  System.out.println("[" + i + "," + _colIndices[i].get(j) + "]: " + _values[i].get(j));
				  }
			  }
		  }
	  }
	  
	  public void dump(String tag) {
		  for (int i = 0; i < _rows; i++) {
			  if (_colIndices[i] != null) {
				  for (int j = 0; j < _colIndices[i].size(); j++) {
					  System.out.println(tag + " [" + i + "," + _colIndices[i].get(j) + "]: " + _values[i].get(j));
				  }
			  }
		  }
	  }

  }
}