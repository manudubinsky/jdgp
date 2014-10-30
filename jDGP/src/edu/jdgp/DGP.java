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
  
  public static void main(String[] args) {
	VecInt v = new VecInt();
	for (int i = 0; i < 5000; i++) {
		v.pushBack(i);
	}
	System.out.println("v[0]: " + v.get(0) + " v[1024]: " + v.get(1024) + " v[2048]: " + v.get(2048) + " v[4096]: " + v.get(4096));
  }

  //////////////////////////////////////////////////////////////////////
  public static class VecFloat  implements VecFloat_h
  {
	public VecFloat() {
		
	}
	
	public VecFloat(int N) {
				
	}

	public void erase() {
	}

	public int size() {
		return 0;
	}

	public void pushBack(float v) {
	}

	public float get(int j) throws ArrayIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getFront() throws ArrayIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return 0;
	}

	public float getBack() throws ArrayIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		return 0;
	}

	public void popBack() throws ArrayIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		
	}

	public void set(int j, float vj) throws ArrayIndexOutOfBoundsException {
		// TODO Auto-generated method stub
		
	}

	public void swap(VecFloat_h other) throws Exception {
		// TODO Auto-generated method stub
		
	}

  }

/*
  //////////////////////////////////////////////////////////////////////
  public static class Partition implements Partition_h
  {

    // ASSIGNMENT 1

  }

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
