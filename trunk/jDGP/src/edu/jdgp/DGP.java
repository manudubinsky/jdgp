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
  public static void main(String[] args) {
	  try {
		Partition p = new Partition(10);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

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
	  
	  public Partition(int n) throws Exception {
		  reset(n);
	  }
	  
	public void reset(int n) throws Exception {
		_numElems = n;
		_numParts = n;
		_elems = new int[n];
		_parts = new VecInt[n];
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
		// TODO Auto-generated method stub
		return 0;
	}

	public int getSize(int i) {
		if (i >= 0 && i < _numElems)
			return _parts[i].size();
		else
			return 0;
	}

  }

/*
  //////////////////////////////////////////////////////////////////////
  public class SplittablePartition
    extends Partition implements SplittablePartition_h
  {

    // ASSIGNMENT 1

  }

  //////////////////////////////////////////////////////////////////////
  public static class Faces implements Faces_h
  {

    // ASSIGNMENT 1

  }

  //////////////////////////////////////////////////////////////////////
  public static class Graph implements Graph_h
  {

    // ASSIGNMENT 1

  }
    
  //////////////////////////////////////////////////////////////////////
  public static class PolygonMesh
    extends Faces implements PolygonMesh_h
  {

    // ASSIGNMENT 1

  }
*/
}
