package edu.jdgp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PartitionUnionFind;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.VecInt;

/*
 Encuentra los ciclos simples de una malla triangular, la idea es la siguiente
 
1 	Mientras haya ejes disponibles
2 		Encontrar las componentes conexas generadas por los ejes disponibles
3 		Para cada componente conexa C
4 			Construir un árbol generador (BFS)
5 			Inferir los ciclos simples que involucran a los ejes del árbol generador
				5.1 Consideremos un eje del árbol e=(v->w) y que "v" pertenece a la vecindad "H" y "w" a la 
				vecindad "H+1", entonces el eje "e" puede pertenecer a dos tipos de ciclos simples 
					
					1) el loop-edge l=(w->u) conecta a "w" con un nodo "u" que pertenece a la vecindad 
					concéntrica H (la misma que "v"), en este caso hay que ver que exista el eje e'=(v->u)
					
					2) el loop-edge l=(w->u) conecta a "w" con un nodo "u" que pertenece a su misma vecindad 
					concéntrica "H+1", en este caso hay que verificar que "u" y "w" sean hermanos (es decir: 
					que "v" sea su padre)

					(Nota los puntos 1 y 2 se basan en la propiedad detallada abajo)
					
6 			Eliminiar del conjunto de ejes disponibles los ejes del árbol generador

Prop.: los loop-edges asociados a un árbol generador BFS conectan nodos en la misma vecindad concéntrica o en
		vecindadades consecutivas
*/
public class TriMeshInducedCycles {
	private Graph _graph;
	private VecBool _availableEdges;
	private int _availableEdgesCount;
	public int _nF;
	public Map<String, VecInt> _allFaces;
	
	public TriMeshInducedCycles (Graph graph) throws Exception {
		_graph = graph;		
		_availableEdges = new VecBool(_graph.getNumberOfEdges(), true);
		_availableEdgesCount = _graph.getNumberOfEdges();
		_allFaces = new HashMap<String, VecInt>();
		build();
	}

	private VecInt packFaceVertexes(int iV0, int iV1, int iV2) {
		VecInt faceVertexes = new VecInt(3);
		faceVertexes.pushBack(iV0);
		faceVertexes.pushBack(iV1);
		faceVertexes.pushBack(iV2);
		return faceVertexes;
	}
	
	public void build() {
		_nF = 0;
		while (_availableEdgesCount > 0) {
			VecInt[] components = connectedComponents();
			for (int i = 0; i < components.length; i++) {
				// 1) arbol generador (bfs)
				BFSComponentSpanningTree tree = new BFSComponentSpanningTree(_graph, components[i], _availableEdges);
				// 2) detectar ciclos simples
				VecInt loopEdges = tree.getLoopEdges();
				VecInt parents = tree.getParents();
				for (int j = 0; j < loopEdges.size(); j++) {
					int iE = loopEdges.get(j);
					int iV0 = _graph.getVertex0(iE);
					int iV1 = _graph.getVertex1(iE);
					if (_graph.getEdge(iV0, parents.get(iV1)) != -1) {
						_nF++;
						String key = orderThree(parents.get(iV1),iV0, iV1);						
						_allFaces.put(key, packFaceVertexes(parents.get(iV1),iV0, iV1));
					}
					if (_graph.getEdge(parents.get(iV0), iV1) != -1) {
						_nF++;
						String key = orderThree(parents.get(iV0),iV0, iV1);
						_allFaces.put(key, packFaceVertexes(parents.get(iV0),iV0, iV1));						
					}
				}
				// 3) eliminar los ejes de árbol generador (sacarlos de available edges)
				VecInt treeEdges = tree.getTreeEdges();
				for (int j = 0; j < treeEdges.size(); j++) {
					int iE = treeEdges.get(j);
					_availableEdges.set(iE, false);
					_availableEdgesCount--;
				}
			}			
		}
	}
	
	public VecInt[] connectedComponents() {
		try {
			VecInt[] components;
			PartitionUnionFind parts;
			parts = new PartitionUnionFind(_graph.getNumberOfVertices());
			for (int i = 0; i < _graph.getNumberOfEdges(); i++) {
				if (_availableEdges.get(i)) {
					parts.join(_graph.getVertex0(i), _graph.getVertex1(i));
				}
			}
			//parts.dump();
			int index = 0;
			components = new VecInt[parts.getNumberOfParts()];
			Map<Integer, Integer> partNum2Index = new HashMap<Integer, Integer>();
			for (int i = 0; i < parts.getNumberOfElements(); i++) {
				int part = parts.find(i);
				if (!partNum2Index.containsKey(part)) {
					//System.out.println("ACA!!!! " + part);
					partNum2Index.put(part, index++);
				}
				int idx = partNum2Index.get(part);
				if (components[idx] == null) {
					components[idx] = new VecInt(10);
				}
				components[idx].pushBack(i);
			}
			return components;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public class BFSComponentSpanningTree {
		int _root;
		VecInt[] _tree;
		VecInt _treeEdges;
		VecInt _loopEdges;
		VecInt _parents;
		VecInt _neigborRings;
		Graph _graph;		
		
		public BFSComponentSpanningTree(Graph graph, VecInt component, VecBool availEdges) {
			_graph = graph;			
			build(component, availEdges);
		}
		
		public VecInt[] getTree() {
			return _tree;
		}

		public VecInt getParents() {
			return _parents;
		}

		public VecInt getLoopEdges() {
			return _loopEdges;
		}

		public VecInt getTreeEdges() {
			return _treeEdges;
		}

		public VecInt getNeighborRings() {
			return _neigborRings;
		}

		public void build(VecInt component, VecBool availEdges) {
			int nV= component.size();
			_root = component.get(0);
			_tree = new VecInt[_graph.getNumberOfVertices()];
			_treeEdges = new VecInt(nV-1);
			_loopEdges = new VecInt(10);
			_parents = new VecInt(_graph.getNumberOfVertices(),0); //el tamaño es asi porque se usan los iV globales
			_neigborRings = new VecInt(_graph.getNumberOfVertices(),0); // "						
			VecBool visited = new VecBool(_graph.getNumberOfVertices(), false);
			VecInt queue = new VecInt(nV);
			queue.pushBack(_root);
			visited.set(_root, true);
			_parents.set(_root, -1);
			_neigborRings.set(_root, 0);
			int queueIdx = 0;
			VecBool visitedEdge = new VecBool(_graph.getNumberOfEdges(),false);
			while (queueIdx < nV) {
				int v = queue.get(queueIdx++);
				VecInt vertexEdges = _graph.getVertexEdges(v);
				for (int j = 0; j < vertexEdges.size(); j++) {
					int edge = vertexEdges.get(j);
					if (availEdges.get(edge)) {
						int neighbor = _graph.getNeighbor(v, edge);
						if (!visited.get(neighbor)) {
							if (_tree[v] == null) {
								_tree[v] = new VecInt(2);
							}
							_tree[v].pushBack(neighbor);
							_parents.set(neighbor, v);
							_treeEdges.pushBack(edge);
							_neigborRings.set(neighbor,_neigborRings.get(v) + 1);
							queue.pushBack(neighbor);
							visited.set(neighbor, true);						
						} else if (!visitedEdge.get(edge)) {
							_loopEdges.pushBack(edge);
						}
						visitedEdge.set(edge, true);
					}
				}
			}
		}
	}		

	public static String orderThree(int e0, int e1, int e2) {
		if (e1 < e0) { 
			int aux = e0; e0 = e1; e1 = aux; 
		}
		if (e2 < e1) { 
			int aux = e1; e1 = e2; e2 = aux; 
		}
		if (e1 < e0) { 
			int aux = e0; e0 = e1; e1 = aux; 
		}		
		return e0 + "_" + e1 + "_" + e2;
	}

	public static String orderThree(VecInt v) {
		return orderThree(v.get(0),v.get(1),v.get(2));
	}
	
	public static ArrayList<String> allFaces(PolygonMesh mesh) throws Exception {
		ArrayList<String> faces = new ArrayList<String>();
		for(int i = 1; i < mesh.getNumberOfFaces()+1; i++) {
			int iC = mesh.getFaceFirstCorner(i);
			int iV0 = mesh.getVertex(iC);
			iC = mesh.getNextCorner(iC);
			int iV1 = mesh.getVertex(iC);
			iC = mesh.getNextCorner(iC);
			int iV2 = mesh.getVertex(iC);
			if (iV0 == iV1 || iV0 == iV2 || iV1 == iV2) {
				System.out.println(iV0 + " " + iV1 + " " + iV2);
			}
			faces.add(orderThree(iV0, iV1, iV2));
		}
		return faces;
	}
	
	public static void main(String[] args) throws Exception {
		WrlReader reader = new WrlReader("/home/manuel/workspace/jdgp/img/ateneav.wrl");
		PolygonMesh mesh = reader.getMesh();
		Graph g = mesh.getGraph();
		System.out.println(" nV: " + g.getNumberOfVertices() + 
		                   " nE: " + g.getNumberOfEdges() + 
		                   " nF: " + mesh.getNumberOfFaces());
		
		ArrayList<String> allFaces = allFaces(mesh);
		Collections.sort(allFaces);
		
		TriMeshInducedCycles t = new TriMeshInducedCycles(g);
		Map<String, VecInt> allFaces2 = t._allFaces;
		
		System.out.println(allFaces2.size());
		for (String face : allFaces) {
			if (!allFaces2.containsKey(face)) {
				System.out.println("ACAAAAAAAAA" + face);
			}			
		}
	}

//	public static void main(String[] args) throws Exception  {
//	//Graph g = Graph.buildCompleteBipartite(10,12);
//	Graph g = new Graph(9);	  
//
//	g.insertEdge(0, 1);
//	g.insertEdge(0, 2);
//	g.insertEdge(1, 2);
//
//	g.insertEdge(2, 3);
//	g.insertEdge(2, 4);
//	g.insertEdge(3, 4);
//
//	g.insertEdge(4, 5);
//	g.insertEdge(4, 6);
//	g.insertEdge(5, 6);
//
//	g.insertEdge(6, 7);
//	g.insertEdge(6, 8);
//	g.insertEdge(7, 8);
//			
//	ChordalGraphSimpleCycles t = new ChordalGraphSimpleCycles(g);
//}
//

}
