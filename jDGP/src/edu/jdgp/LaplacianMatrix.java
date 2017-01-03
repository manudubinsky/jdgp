package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.FormIntegratorExact;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Implementacion matriz laplaciana 
 */ 

public class LaplacianMatrix {
	private int _N;
	private VecInt _diag;
	private VecInt _offDiag;

	public LaplacianMatrix(int n) {
		_N = n;
		_init();
	}

	public LaplacianMatrix(Graph graph) {
		_N = graph.getNumberOfVertices();
		_init();

		SparseMatrixInt m = new SparseMatrixInt(_nV);
		for (int i = 0; i < _N; i++) {
			VecInt neighbors = graph.getVertexEdges(i);
			m.set(i, i, neighbors.size());
			for (int j = 0; j < neighbors.size(); j++) {
				m.set(i, getNeighbor(i, neighbors.get(j)), -1);
			}
		}
		return m;

	}

	public int getRows() {
		return _N;
	}

	public int getCols() {
		return _N;
	}

	private void _init() {
		_diag = new VecInt(_N, 0);
		_offDiag = new VecInt(_N, 0);
	}

	public void set(int row, int col) {
		if (row != col && (0 <= row && row < _N) && (0 <= col && col < _N)) {
			if (row > col) { 
				row = col;
				col = row;
			}
			int bit = 1 << col;
			int value = _offDiag.get(row);
			if ((value & bit) == 0) {
				_offDiag.set(row, value | bit);
				_diag.set(row, _diag.get(row) + 1);
			}
		}
	}

	public void unset(int row, int col) {
		if (row != col && (0 <= row && row < _N) && (0 <= col && col < _N)) {
			if (row > col) { 
				row = col;
				col = row;
			}
			int bit = 1 << col;
			int value = _offDiag.get(row);
			if ((value & bit) == bit) {
				_offDiag.set(row, value & ~bit);
				_diag.set(row, _diag.get(row) - 1);
			}
		}
	}
/*
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
	  VecFloat resultVec = new VecFloat(_rows, 0);		  
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
	  VecFloat resultVec = new VecFloat(_rows, 0);		  
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
		  newColIndices[i] = new VecInt(rowSize,-1);
		  newValues[i] = new VecFloat(rowSize,-1);
		  for (int j = 0; j < rowSize; j++) {
			  newColIndices[i].set(j, _colIndices[i].get(j));
			  newValues[i].set(j, _values[i].get(j));
		  }
	  }
	  _colIndices = newColIndices;
	  _values = newValues;
	  _rows = rows;
	}
*/
	public void dump() {
		System.out.println("dim: " + _N + " x " + _N);
		_diag.dump();
		_offDiag.dump();
	}

	public static void main(String[] args) throws Exception {
		LaplacianMatrix m = new LaplacianMatrix(5);
		m.set(1,0);
		m.dump();
		m.unset(1,0);
		m.dump();
	}
}
