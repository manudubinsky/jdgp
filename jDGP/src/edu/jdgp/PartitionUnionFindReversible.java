package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.PartitionUnionFind;

/*
 * La idea es hacer un Union Find reversible. Es un problema difícil.
 * Para encararlo se puede leer el paper "UnionFindDinamico.pdf" ahí hay
 * referencias.
 */ 
public class PartitionUnionFindReversible {

	public static class IntSparseMatrix	{
		int _rows;
		int _cols;
		private VecInt[] _colIndices;
		private VecInt[] _values;

		public IntSparseMatrix(int rows) {
			_rows = rows;
			_init();
		}

		private void _init() {
			_colIndices = new VecInt[_rows];
			_values = new VecInt[_rows];
		}

		private int getColIndex(int row, int col) {
			int colIndex = -1;
			if (_colIndices[row] != null) {
				for (int i = 0; i < _colIndices[row].size() && colIndex < 0; i++) {
					if (_colIndices[row].get(i) == col)
						colIndex = i;
				}
			}
			return colIndex;
		}
		
		public void set(int row, int col, int value) {
			if (_colIndices[row] == null) {
				_colIndices[row] = new VecInt(1);
				_values[row] = new VecInt(1);
			}
			int colIndex = getColIndex(row, col);
			if (colIndex == -1) {
				_colIndices[row].pushBack(col);
				_values[row].pushBack(value);
			} else {
				_values[row].set(colIndex, value);
			}
			if (_cols <= col)
				_cols = col+1;
		}

		public int get(int row, int col) {
			int value = -1;
			if (_colIndices[row] != null) {
				int colIndex = getColIndex(row, col);
				if (colIndex >= 0 )
					value = _values[row].get(colIndex);
			}
			return value;
		}

		/*
		 * Devuelve el valor de la fila "row" asociado a la mayor 
		 * columna menor o igual a "col". 
		 * */
		public int getLastValue(int row, int col)  {
			int value = 0;
			// System.out.println("get(...) row: " + row + " col: " + col);
			if (_colIndices[row] != null) {
				// System.out.println("get(...) not null!!! _colIndices[row]: " + _colIndices[row].size());
				int colIndex = -1;
				boolean seguir = true;
				// _colIndices[row].dump();
				for (int i = 0; i < _colIndices[row].size() && seguir; i++) {
				  // System.out.println("get(...) _colIndices[row].get(i): " + _colIndices[row].get(i));
					if (_colIndices[row].get(i) > col)
						seguir = false;
					else 
						colIndex = i;
				}
				if (colIndex >= 0 )
					value = _values[row].get(colIndex);
			}
			return value;
		}

		//una truchada para poder hacer undo
		public void resetColumn(int iV0, int iV1, int col) {
			if (get(iV0, col) != -1) {
				_colIndices[iV0].popBack();
				_values[iV0].popBack();
			}
			if (get(iV1, col) != -1) {
				_colIndices[iV1].popBack();
				_values[iV1].popBack();
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

		public void fullDump() {
			System.out.println("_rows: " + _rows + " _cols: " + _cols);
			for (int i = 0; i < _rows; i++) {
				int currentCol = 0;
				StringBuffer row = new StringBuffer();
				for (int j = 0; j < _cols; j++) {
					int colIndex = getColIndex(i, j);
					if (colIndex != -1) { 
						row.append("\t" + _values[i].get(colIndex));
					} else {
						row.append("\tNaN");
					}			  
				}
				System.out.println(row);
			}
		}

	}
/*
	public static void main(String[] args) throws Exception {
		IntSparseMatrix m = new IntSparseMatrix(4);
		for (int i = 0; i < 4; i++) {
			m.set(i,i,1);
		}
		m.fullDump();
	}
*/
	/*
	* Un Union Find reversible, o sea que vaya guardando distintas versiones
	* para poder revertir los cambios.  En el algoritmo de Tarjan-Read 
	* para cada eje del grafo hay que probar a agregarlo y no 
	* agregarlo al PST (partial spanning tree)
	*/
	public static class PartitionUnionFindReversibleInner {
		private IntSparseMatrix _elems; //matriz de elementos en funcion de la iteracion
		private int _currentCol; // columna (iteracion) actual de _elems
		private int _numElems; // cantidad de elementos
		
		public PartitionUnionFindReversibleInner(int n) {
			reset(n);
		}
	  
		public void reset(int n) {
			_numElems = n;
			_currentCol = 0;
			_elems = new IntSparseMatrix(n);
			for (int i = 0; i < n; i++) {
				_elems.set(i, 0, i);
			}
		}

		public int getCurrentCol() {
			return _currentCol;
		}
		
		public void setCurrentCol(int col) {
			_currentCol = col;
		}

		public void undoLastJoin(int iV0, int iV1) {
			_elems.resetColumn(iV0, iV1, _currentCol);
			_currentCol--;
		}
		
		public int find(int i) {
			if (i >= 0 && i < _numElems) {
				int value = _elems.getLastValue(i, _currentCol);
				if (value != i)
					value = find(value);
				return value;
			} else
				return -1;
		}

		public boolean checkHasToJoin(int i, int j) {
			// obtengo los representantes de las particiones de i y j
			int iPart = find(i);
			int jPart = find(j);
			return (iPart != jPart);
		}

		public boolean checkJoin(int i, int j) {			
			// obtengo los representantes de las particiones de i y j
			// obtengo los representates de las particiones de i y j
			int iPart = find(i);
			int jPart = find(j);
			//System.out.println(i + " (" + iPart + ") -> " + j + " (" + jPart + ")");
			int part = iPart;
			if (iPart != jPart) { // si no estan en la misma particion...				
			// a la mayor particion, le asignamos la menor
				int idx = jPart;
				if (jPart < iPart) {
					idx = iPart;
					part = jPart;
				}
				_elems.set(idx, ++_currentCol, part);
				//_elems.fullDump();
			}
			return (iPart != jPart);
		}
	
		public int join(int i, int j) {			
			// obtengo los representates de las particiones de i y j
			int iPart = find(i);
			int jPart = find(j);
			int part = iPart;
			if (iPart != jPart) { // si no estan en la misma particion...
			// a la mayor particion, le asignamos la menor
				int idx = jPart;
				if (jPart < iPart) {
					idx = iPart;
					part = jPart;
				}
				
				_elems.set(idx, ++_currentCol, part);
			}
			return part;
		}

		public void dump(String s){
			System.out.println(s + " _numElems: " + _numElems + " _currentCol: " + _currentCol);
			_elems.fullDump();
		}
	}  
/*
	public static void main(String[] args) throws Exception {
		PartitionUnionFindReversible p = new PartitionUnionFindReversible(4);
		p.dump("Init");
		p.checkJoin(0,1);
		p.checkJoin(0,2);
		p.checkJoin(2,3);
		p.dump("Caso 1");
		p.undoLastJoin();
		p.dump("Caso 2");
		p.undoLastJoin();
		p.dump("Caso 3.a");
		p.checkJoin(1,2);
		p.dump("Caso 3.b");

		p = new PartitionUnionFindReversible(4);
		p.dump("Init");
  		p.checkJoin(0,1);
		p.checkJoin(2,3);
		p.checkJoin(1,2);
		p.checkJoin(0,2);
		p.dump("Caso 1");
	}
*/

}

