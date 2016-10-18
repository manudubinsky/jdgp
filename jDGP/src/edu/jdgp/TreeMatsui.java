package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.PartitionUnionFind;

/*
 * Implementacion de Tree específico para Matsui
 * (generar todos los árbles generadores de un grafo conexo
 * Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class TreeMatsui {
	private VecInt _treeEdges;
	private int top;
	private int bottom;
	
	public TreeMatsui(int n) {
		_treeEdges = new VecInt(n-1,-1);
		top = bottom = -1;
	}
	
	public void set(int iE, int V) {
		int currentValue = _treeEdges.get(iE);
		_treeEdges.set(iE, V);
		if (top == -1) { // es el primer elemento
			top = bottom = V;
		} else {
			if (currentValue == top || currentValue == bottom) { // si el valor que hay que pisar el bottom o top
				top = bottom = -1;
				for (int i = 0; i < _treeEdges.size(); i++) {
					int value = _treeEdges.get(i);
					if (value >= 0) {
						if (value < top || top == -1)
							top = value;
						if (value > bottom || bottom == -1)
							bottom = value;
					}
				}
			}
			if (V < top) //si el nuevo valor es top
				top = V;
			if (V > bottom) //si el nuevo valor es bottom
				bottom = V;
		}
	}
	
	public VecInt getTreeEdges() {
		return _treeEdges;
	}

	public int getTop() {
		return top;
	}

	public int getBottom() {
		return bottom;
	}

/*
	public VecBool getSetH() {
		int n = _treeEdges.size();
		VecBool setH = new VecBool(n,false);
		for (int i = 0; i < n; i++) {
			int iE = _treeEdges.get(i);
			if (iE < n)
				setH.set(iE, true);
		}
		return setH;
	}
*/
	//simplificacion de la funcion "label" que sugiere el paper
	public PartitionUnionFind label(Graph graph) throws Exception {
		PartitionUnionFind label = new PartitionUnionFind(graph.getNumberOfVertices());
		for (int i = 0; i < _treeEdges.size(); i++) {
			int iE = _treeEdges.get(i);
			if (iE >= _treeEdges.size()) {
			// si es un indice > n-2 hay que agregar el eje
				label.join(graph.getVertex0(iE),graph.getVertex1(iE));
			}
		}
		return label;
	}
	
	public void dump() {
		System.out.println("top: " + top + " bottom: " + bottom);
		_treeEdges.dump();
	}

	public static void main(String[] args) throws Exception {
		Graph g = new Graph(5);
		g.insertEdge(0, 1);
		g.insertEdge(0, 2);
		g.insertEdge(0, 3);
		g.insertEdge(0, 4);
		g.insertEdge(1, 2);
		g.insertEdge(2, 3);
		g.insertEdge(3, 4);
		TreeMatsui t = new TreeMatsui(5);
		t.set(0, 0);
		t.set(1, 4);
		t.set(2, 3);
		t.set(3, 6);
		t.dump();
		t.label(g).dump();
	}

}
