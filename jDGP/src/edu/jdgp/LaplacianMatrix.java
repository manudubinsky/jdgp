package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

/*
 * Implementacion matriz laplaciana.
 * 1) Es simétrica, entonces se puede descartar la mitad
 * 2) Los elementos que no están en la diagonal son booleanos: nos importa saber cuáles 
 *    están en -1. Dependiendo del grado de cada nodo, podemos conservar los que están
 *    en -1 o el complemento (los que no están), esto permite reducir a la mitad.
 * 3) Para saber el valor de cada elemento de la diagonal, hay que contar la cantidad 
 *    de elementos que no están en la diagonal
 */ 

public class LaplacianMatrix {
	private int _N;
	private VecInt _diagValues; // los valores de la diagonal
	private VecInt[] _offDiagIndices; //elementos que están presentes o su complemento (dependiendo del valor de inverted)
	private boolean[] _invertedSet; //para cada fila de la matriz se asocia un flag que indica si su conjunto de índices es directo o complementario
	
	public LaplacianMatrix(int n) {
		_N = n;
		_init();
	}

	public LaplacianMatrix(Graph graph) {
		_N = graph.getNumberOfVertices();
		_init();
		int nE = graph.getNumberOfEdges();
		VecInt edges = graph.getEdges();
		for (int i = 0; i < nE; i++) {
			int iV0 = edges.get(2*i);
			int iV1 = edges.get(2*i+1);
			set(iV0, iV1);
		}
	}

	public int getRows() {
		return _N;
	}

	public int getCols() {
		return _N;
	}

	private void _init() {
		_diagValues = new VecInt(_N, 0);
		_offDiagIndices = new VecInt[_N];
		_invertedSet = new boolean[_N];
	}

	//se supone que set debe ejecutarse antes que compact
	public void set(int row, int col) {
		if (row != col) { //queremos los elementos fuera de la diagonal
			if (row > col) { // como es simétrica nos quedamos con la parte superior
				int aux = row;
				row = col;
				col = aux;
			}
			if (get(row, col) == 0) {
				if (_offDiagIndices[row] == null) {
					_offDiagIndices[row] = new VecInt(_N - row - 1); //para que no haya resizing
				}
				_offDiagIndices[row].pushBack(col);
				_diagValues.inc(row);
				_diagValues.inc(col);
			}
		}
	}

	public void compact() {
		for (int i = 0; i < _N; i++) {
			int offDiagElems = _N-i-1; //fuera de la diagonal en la parte superior de la matriz
			if (_offDiagIndices[i] != null && _offDiagIndices[i].size() > offDiagElems/2) {
				boolean[] complement = new boolean[_N]; //se inicializan en false
				for (int j = 0; j < _offDiagIndices[i].size(); j++) {
					complement[_offDiagIndices[i].get(j)] = true;
				}
				int complementElems = offDiagElems - _offDiagIndices[i].size();
				_offDiagIndices[i] = new VecInt(complementElems);
				for (int j = i+1; j < _N; j++) {
					if (!complement[j]) {
						_offDiagIndices[i].pushBack(j);
					}
				}
				_invertedSet[i] = true;
			} else if (_offDiagIndices[i] != null) {
				int[] values = new int[_offDiagIndices[i].size()];
				for (int j = 0; j < _offDiagIndices[i].size(); j++) {
					values[j] = _offDiagIndices[i].get(j);
				}
				_offDiagIndices[i] = new VecInt(values);
			}
		}
	}

	public int get(int row, int col) {
		int value = 0;
		if (row > col) { // como es simétrica nos quedamos con la parte superior
			int aux = row;
			row = col;
			col = aux;
		}		
		if (row == col) { //elemento de la diagonal hay que sumar según corresponda
			value = _diagValues.get(row);
		} else if (_offDiagIndices[row] != null) {
			int index = -1;
			for (int i = 0; i < _offDiagIndices[row].size() && index < 0; i++) {
				if (_offDiagIndices[row].get(i) == col)
					index = i;
			}
			if ((!_invertedSet[row] && index >= 0) || (_invertedSet[row] && index == -1))
				value = -1;
		}
		return value;
	}

	private void log(int row, int[] idx) {
		System.out.println("i: " + row);
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < idx.length; i++) {
			s.append(" " + idx[i]);
		}
		System.out.println(s);
	}
	
	public VecFloat multiplyByVector(VecFloat v) {
		float[] resultVec = new float[_N];
		float[] vecValues = v.getVec();
		int[] indexes;
		for (int i = 0; i < _N; i++) {
			if (_offDiagIndices[i] != null) {
				if (!_invertedSet[i]) {
					indexes = _offDiagIndices[i].getVec();
				} else {
					boolean[] complement = new boolean[_N]; //se inicializan en false
					for (int j = 0; j < _offDiagIndices[i].size(); j++) {
						complement[_offDiagIndices[i].get(j)] = true;
					}
					int complementElems = _N - (i + 1) - _offDiagIndices[i].size();
					indexes = new int[complementElems];
					for (int j = i+1; j < _N; j++) {
						if (!complement[j]) {
							indexes[j-(i+1)] = j;
						}
					}
				}
				//(i, indexes);
				for (int j = 0; j < indexes.length; j++) {
					resultVec[i] -= vecValues[indexes[j]];
					resultVec[indexes[j]] -= vecValues[i];
				}
			}
			resultVec[i] += (float)_diagValues.get(i) * vecValues[i];
		}
		return new VecFloat(resultVec);
	}

	public void dump() {
		System.out.println("dim: " + _N + " x " + _N);
		for (int i = 0; i < _N; i++) {
			if (_offDiagIndices[i] != null) {
				System.out.println("row: " + i + " inverted?: " + _invertedSet[i]);
				_offDiagIndices[i].dump();
			}
		}		
	}

	public void dumpMatrix() {
		for (int i = 0; i < 5; i++) {
			StringBuffer s = new StringBuffer();
			for (int j = 0; j < 5; j++) {
				s.append(" " + get(i, j));
			}
			System.out.println(s);
		}
	}

	public static void main(String[] args) throws Exception {
		LaplacianMatrix m = new LaplacianMatrix(Graph.buildCompleteGraph(1000));
		m.compact();
		VecFloat v = new VecFloat(1000, 1);
		System.out.println("start");
		long start = System.nanoTime();
		m.multiplyByVector(v);
		long end = System.nanoTime();
		System.out.println("end - delta: " + (end - start));
		/*
		for (int i = 0; i < 500; i++) {
			System.out.println(i);
			LaplacianMatrix m = new LaplacianMatrix(Graph.buildCompleteGraph(i));
			m.compact();
			VecFloat v = new VecFloat(i, 1);
			m.multiplyByVector(v);
		}
		*/
	}

/*
	public static void main(String[] args) throws Exception {
		LaplacianMatrix m = new LaplacianMatrix(5);
		m.set(0,1);
		m.set(0,2);
		m.set(1,2);
		m.set(1,3);
		m.set(1,4);
		m.dump();
		m.compact();
		m.dump();
		for (int i = 0; i < 5; i++) {
			StringBuffer s = new StringBuffer();
			for (int j = 0; j < 5; j++) {
				s.append(" " + m.get(i, j));
			}
			System.out.println(s);
		}
	}
*/
/*
	public static void main(String[] args) throws Exception {
		LaplacianMatrix m = new LaplacianMatrix(Graph.buildCompleteGraph(5));
		m.dumpMatrix();
		m.compact();
		m.dump();
		m.compact();
		m.dump();
		m.dumpMatrix();
		VecFloat v = new VecFloat(5, 1);
		v.dump();
		m.multiplyByVector(v).dump();
	}
*/
}
