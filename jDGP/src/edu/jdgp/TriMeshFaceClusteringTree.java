package edu.jdgp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import edu.jdgp.DGP.EdgeRelabelGraph;
import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PartitionUnionFind;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.VecInt;

/*
 * ToDo:
 * 	Fase 0
 * 		Hacer otra prueba semi-automatica con otra malla
 * 	Fase 1 
 * 		Terminar de armar un árbol generador (sacando un eje del ciclo del borde) 
 * 		Construir la matriz labi asociada al árbol
 * 		Construir árboles generadores al azar asociados al grafo
 * 		Comparar las matrices LABI de los árboles que produce el algoritmo y los árboles al azar
 * Fase 2
 * 		Generar triangulaciones de la esfera "pegando" 2 copias de estas mallas triangulares
 * 		Ejecutar Fase 1 para esas mallas 
 */
public class TriMeshFaceClusteringTree {
	private Graph _graph;
	private VecInt[] _edge2Faces;
	private PartitionUnionFind _clusters;
	private VecInt[] _clusterEdges;
	private VecInt _loopEdges;
	private int debugInt = 0;
	private int debugInt2 = 0;
	
	public TriMeshFaceClusteringTree(Graph graph) throws Exception {
		_graph = graph;
		init();
		build();
	}
	
	private VecInt packFaceEdges(VecInt faceVertexes) {
		VecInt faceEdges = new VecInt(3);
		faceEdges.pushBack(_graph.getEdge(faceVertexes.get(0), faceVertexes.get(1)));
		faceEdges.pushBack(_graph.getEdge(faceVertexes.get(0), faceVertexes.get(2)));
		faceEdges.pushBack(_graph.getEdge(faceVertexes.get(1), faceVertexes.get(2)));
		return faceEdges;
	}
	
	private void init() {
		int iE = 0;
		try {			
			int faceIndex = 0;
			TriMeshInducedCycles inducedCycles = new TriMeshInducedCycles(_graph);
			int nF = inducedCycles._allFaces.size();
			_edge2Faces = new VecInt[_graph.getNumberOfEdges()];
			_clusterEdges = new VecInt[nF];
			for (String face : inducedCycles._allFaces.keySet()) {
				VecInt faceEdges = packFaceEdges(inducedCycles._allFaces.get(face));
				_clusterEdges[faceIndex] = faceEdges;
				for (int i = 0; i < 3; i++) {
					iE = faceEdges.get(i);
					if (_edge2Faces[iE] == null) {
						_edge2Faces[iE] = new VecInt(2);
					}
					_edge2Faces[iE].pushBack(faceIndex);
				}
				faceIndex++;
			}
			_clusters = new PartitionUnionFind(nF);
			_loopEdges = new VecInt(_graph.getNumberOfEdges());
		} catch (Exception e) {
			//System.out.println(iE);
			e.printStackTrace();
		}
		//_edge2Faces[397].dump();
	}
	
	private void showCluster(int joinEdge, int cluster) {
		StringBuffer s = new StringBuffer();
		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();
		//System.out.println(_clusterEdges[cluster].size());
		for (int i = 0; i < _clusterEdges[cluster].size(); i++) {
			int iE = _clusterEdges[cluster].get(i);			
			s.append(" " + iE);
			s.append("(");
			for (int j = 0; j < _edge2Faces[iE].size(); j++) {
				s.append(_clusters.find(_edge2Faces[iE].get(j)) + ",");				
			}
			s.append(") ");
			
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);

			if (!unique.containsKey(iV0)) {			
//				s.append(" " + iV0);
				unique.put(iV0, false);
			}
			if (!unique.containsKey(iV1)) {
//				s.append(" " + iV1);
				unique.put(iV1, false);
			}

			
		}
		int iV0 = joinEdge != -1 ? _graph.getVertex0(joinEdge) : -1;
		int iV1 = joinEdge != -1 ? _graph.getVertex1(joinEdge) : -1;

		System.out.println("cluster: " + cluster + " edges: " + s + 
				" joinEdge ("+joinEdge+"): " + iV0 + " -> " + iV1);// + " #joinEdgeFaces: " + _edge2Faces[joinEdge].size());
//		System.out.println(unique.size());
//		for (int i = 0; i < 818; i++) {
//			if (!unique.containsKey(i))
//				System.out.println("i: " + i);
//		}
	}

	// el value es para registrar si el cluster ya fue apareado
	private Map<Integer, Boolean> initCluster2Joined(int numParts) {
		Map<Integer, Boolean> clusterIDs = new HashMap<Integer, Boolean>();
		for (int i = 0; i < numParts; i++) {
			clusterIDs.put(i, false);
		}
		return clusterIDs;
	}	
	
	private void build() throws Exception {
		Map<Integer, Boolean> cluster2Joined = initCluster2Joined(_clusters.getNumberOfParts());
		boolean changed = true;
		int lastIterNumParts = _clusters.getNumberOfParts();		
		while(_clusters.getNumberOfParts() > 1 && changed) {			
			Map<Integer, Boolean> newCluster2Joined = new HashMap<Integer, Boolean>();
			for (Integer cluster : cluster2Joined.keySet()) {				
				if (!cluster2Joined.get(cluster)) { // si el cluster todavia no fue apareado, buscar vecino

					int clusteringEdge = findClusteringEdge(cluster, cluster2Joined);
//					if (clusteringEdge == 397) {
//						System.out.println("antes cluster: " +cluster+ " cluster2Joined: " + cluster2Joined.get(cluster));
//					}
//					if (cluster == 495) {
//						System.out.println("ACCAA iE: " + clusteringEdge);
//					}
					int newCluster = joinClustersByEdge(clusteringEdge, cluster2Joined, newCluster2Joined);					
//					if (cluster == 495) {
//						System.out.println("ACCU iE: " + clusteringEdge);
//						System.out.println(cluster2Joined.get(495));
//						System.out.println(cluster2Joined.get(1624));
//					}

//					if (clusteringEdge == 397) {
//						System.out.println("ACA cluster: " + cluster + " iter: " + debugInt);
//						System.out.println("despues: cluster2Joined: " + cluster2Joined.get(cluster));
//						//showCluster(-1, cluster);
//					}
					_loopEdges.pushBack(clusteringEdge);
					if (_clusters.getNumberOfParts() == 1) {
						showCluster(clusteringEdge, newCluster);
					}
					debugInt2++;
				}
			}			
			if (lastIterNumParts != _clusters.getNumberOfParts()) {
				lastIterNumParts = _clusters.getNumberOfParts();
			} else {
				System.out.println(_clusters.getNumberOfParts());
				changed = false;
			}
			cluster2Joined = newCluster2Joined;
			debugInt++;
		}
	}

	private VecInt appendEdges(int iE, int cluster1, int cluster2) {
		boolean flag = false;
//		if (cluster1 == cluster2) {
//			System.out.println("cluster1 == cluster2: " + cluster1);
//		}
		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();
		for (int i = 0; i < _clusterEdges[cluster1].size(); i++) {
			int e = _clusterEdges[cluster1].get(i);
			if (e != iE)
				unique.put(e, true);
//			if (e == 397) {
//				flag = true;
//				System.out.println("ACA1 cluster1: " + cluster1 + " cluster2: " +cluster2+ " iE: " + iE);
//				showCluster(-1, cluster1);
//				showCluster(-1, cluster2);
//			}
		}
		for (int i = 0; i < _clusterEdges[cluster2].size(); i++) {
			int e = _clusterEdges[cluster2].get(i);
			if (e != iE)
				unique.put(e, true);
//			if (e == 397) {
//				flag = true;
//				System.out.println("ACA2 cluster1: " + cluster1 + " cluster2: " +cluster2+ " iE: " + iE);
//				showCluster(-1, cluster1);
//				showCluster(-1, cluster2);
//			}
		}
		VecInt edges = new VecInt(unique.size());
		StringBuffer s = new StringBuffer();
		for (Integer edge : unique.keySet()) {			
			edges.pushBack(edge);
			s.append(edge + " ");
		}
//		if (flag) {
//			System.out.println("ACA3 ejes: " + s);
//		}
		return edges;
	}
	
	private int joinClustersByEdge(int iE, Map<Integer, Boolean> cluster2Joined, Map<Integer, Boolean> newCluster2Joined) {
		// OBS: la cantidad de ejes del cluster resultante es:
		// la suma de los clusters excluyendo el eje del join
		VecInt edgeFaces = _edge2Faces[iE];
		boolean flag = iE == 397;
		//edgeFaces.dump();
//		if (flag) {
//			System.out.println("ENTREE!!!! iter: " + debugInt + " iter2: " + debugInt2);
//			//edgeFaces.dump();
//		}
		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();
		for (int i = 0; i < edgeFaces.size(); i++) {
			int face = edgeFaces.get(i);
			int cluster = _clusters.find(face);
			unique.put(cluster, false);
		}
//		if (flag) {
//			System.out.println(unique.keySet().toString());
//		}
		int joinedCluster = -1;
		for (Integer cluster : unique.keySet()) {
			if (flag) {
				//System.out.println("ACAAAAA " + cluster);
			}
			if (joinedCluster == -1) { // la primera pasada
				joinedCluster = cluster;

			} else {
				newCluster2Joined.remove(cluster);
				newCluster2Joined.remove(joinedCluster);
				VecInt appendedEdges = appendEdges(iE, joinedCluster, cluster);
				int cl1 = joinedCluster;
				int cl2 = cluster;
				joinedCluster = _clusters.join(joinedCluster, cluster);
				_clusterEdges[joinedCluster] = appendedEdges;
				newCluster2Joined.put(joinedCluster, false);
				cluster2Joined.put(cl1, true);
				cluster2Joined.put(cl2, true);
			}
		}
		return joinedCluster;
	}

	private VecInt getEdgeRank(int iE, Map<Integer, Boolean> cluster2Joined) throws Exception {
		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();
		VecInt edgeFaces = _edge2Faces[iE];
		int unclusteredNeighbors = 0;
		int totalEdgeCount = 0;
		for (int i = 0; i < edgeFaces.size(); i++) {
			int cluster = _clusters.find(edgeFaces.get(i));
			unique.put(cluster, false);
			if (cluster2Joined.get(cluster)) 
				unclusteredNeighbors++;
		}
		for (Integer cl : unique.keySet()) {
			totalEdgeCount += _clusterEdges[cl].size();
		}
		int incidentClustersCount = unique.size();
		VecInt rank = new VecInt(3, 0);
		rank.set(0,incidentClustersCount);
		rank.set(1,unclusteredNeighbors);
		rank.set(2,totalEdgeCount);
		return rank; // prioriza que varios esten sin agrupar
	}
	
	private boolean isBetterRank(VecInt current, VecInt next) {
		boolean isBetter = false;
		if (current.get(0) < 2 || (next.get(0) >= 2 && next.get(2) < current.get(2))) {//al menos 2 clusters y menor cantidad de ejes
			isBetter = true;
		}
		return isBetter;
	}
	
	// devuelve el eje que hay que sacar para juntar los 2 clusters 
	private int findClusteringEdge(int cluster, Map<Integer, Boolean> cluster2Joined) throws Exception {
		int clusteringEdge = -1;
		VecInt edgeRank = null;
		VecInt clusterEdges = _clusterEdges[cluster];
		for (int i = 0; i < clusterEdges.size(); i++) {
			int iE = clusterEdges.get(i);
			VecInt rank = getEdgeRank(iE, cluster2Joined);
			if (edgeRank == null || isBetterRank(edgeRank,rank)) {
				edgeRank = rank;
				clusteringEdge = iE;
			}
		}
		return clusteringEdge;
	}

	public void dumpGraphViz(String fileName) {
		BufferedWriter writer = null;
        try {
        	VecBool alreadyProcessedCluster = new VecBool(_clusterEdges.length, false);
            //create a temporary file
            //String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(Calendar.getInstance().getTime());
            File logFile = new File(fileName);

            // This will output the full path where the file will be written to...
            ////System.out.println(logFile.getCanonicalPath());
    		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write("graph {\n");
            for (int i = 0; i < _clusterEdges.length; i++) {
				int cluster = _clusters.find(i);
				if (!alreadyProcessedCluster.get(cluster)) {
					VecInt clusterEdges = _clusterEdges[cluster];
					for (int j = 0; j < clusterEdges.size(); j++) {
						int iE = clusterEdges.get(j);
						if (!unique.containsKey(iE)) {
			            	int iV0 = _graph.getVertex0(iE);
			            	int iV1 = _graph.getVertex1(iE);
			            	writer.write("  " + iV0 + " -- " + iV1 + " [color=blue]\n");
			            	unique.put(iE,true);
						}
					}
					alreadyProcessedCluster.set(cluster,true);
				}
			}
            writer.write("}\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
	}

	/*** Matriz LABI (begin)**********************/
	/* La idea es hacer un puente con la clase TreeMatsui que es la que implementa la construción LABI
		Para eso hay que: 
			1) En base al último cluster: armar un EdgeRelabelGraph que renombra los ejes de modo de que los 
			primero n-1 correspondan a los de un árbol generador
			2) Agregar los demás ejes del grafo original (aquellos que se fueron removiendo en esta cosntrucción)
			3) Armar un TreeMatsui para obtener la matriz LABI
	*/
	
	private Graph buildClusterGraph() {
		Graph clusterGraph = new Graph(_graph.getNumberOfVertices());		
		int cluster = _clusters.find(0);
		VecInt clusterEdges = _clusterEdges[cluster];
		for (int i = 0; i < clusterEdges.size(); i++) {
			int iE = clusterEdges.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			//if (iV1 == 109 || iV1 == 110)
			//	System.out.println(iE + " " + iV0 + " " + iV1);
			clusterGraph.insertEdge(iV0, iV1);
		}
		return clusterGraph;
	}
	
	public TreeMatsui buildLABI() {
		Graph clusterGraph = buildClusterGraph();
		EdgeRelabelGraph relabelGraph = new EdgeRelabelGraph(clusterGraph, _graph.getNumberOfEdges());
		System.out.println("clusterGraph: " + clusterGraph.getNumberOfEdges() + " _graph: " + _graph.getNumberOfEdges());
		for (int i = 0; i < _loopEdges.size(); i++) {
			int iE = _loopEdges.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
//			System.out.println(clusterGraph.getEdge(iV0, iV1));
			int index = clusterGraph.insertEdge(iV0, iV1);
			//System.out.println("index: " + index + " i: " + i +  " iV0: " + iV0+ 
			//		" iV1: " + iV1 + " iE: " + iE);
			//try {
				relabelGraph.addExtraEdge(index);
			//} catch (Exception e) {
				
			//}
		}
		TreeMatsui tree = new TreeMatsui(relabelGraph);
		return tree;
	}
	
	/*** Matriz LABI (end)
	 * @throws Exception **********************/
/*
	public static void main(String[] args) {
		int nodeCnt = 17;
		String path = "/home/manuel/20170817/doctorado/octave/tesis-octave/"+
						"ejemplos/connected-graphs/tri-meshes/samples";
		String file = "ateneav.0.2";
		Graph g = GraphTarjanRead.buildFromAdjMatrix(nodeCnt, path + "/" + file + ".adj_mat");
		TriMeshFaceClusteringTree faceCluster = new TriMeshFaceClusteringTree(g);
		TreeMatsui tree = faceCluster.buildLABI();
		SparseMatrixInt labi = tree.labiMatrix();
		labi.transpose().multiply(labi).fullDump();
		
		
		//faceCluster.dumpGraphViz(path + "/" + file + ".iter4");
//		for (int i = 0; i < faceCluster._edge2Faces.length; i++) {
//			faceCluster._edge2Faces[i].dump();
//		}
	}
*/
	public static void main(String[] args) throws Exception {
		String file = "ateneav";
		//String file = "venusv";
		//String file = "elephav";
		WrlReader reader = new WrlReader("/home/manuel/workspace/jdgp/img/"+file+".wrl");
		PolygonMesh mesh = reader.getMesh();
		Graph g = mesh.getGraph();
		//System.out.println(mesh.isRegularEdge(397));

//		int iV0 = 73;
//		int iV1 = 75;
				
//		System.out.println(g.getEdge(iV0, iV1)); //1655: 514 519
		TriMeshFaceClusteringTree faceCluster = new TriMeshFaceClusteringTree(g);
		System.out.println("nV:" + g.getNumberOfVertices() + " nE: " + g.getNumberOfEdges());
		TreeMatsui tree = faceCluster.buildLABI();
		SparseMatrixInt labi = tree.labiMatrix();
		System.out.println(tree.labiCantZeros());
		//labi.transpose().multiply(labi).fullDump();
		
		
		//faceCluster.dumpGraphViz(path + "/" + file + ".iter4");
//		for (int i = 0; i < faceCluster._edge2Faces.length; i++) {
//			faceCluster._edge2Faces[i].dump();
//		}
	}


}
