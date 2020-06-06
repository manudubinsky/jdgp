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
public class TriMeshFaceClusteringTreeAlfa {
	private Graph _graph;
	private VecInt[] _edge2Faces;
	//private VecInt[] _face2Edges;
	private PartitionUnionFind _clusters;
	private VecInt[] _clusterEdges;
	private VecInt _loopEdges;
	
	public TriMeshFaceClusteringTreeAlfa(Graph graph) {
		_graph = graph;
		init();
		build();
	}
	
	private VecInt packFaceEdges(VecInt faceVertexes) {
		//faceVertexes.dump();
		VecInt faceEdges = new VecInt(3);
		faceEdges.pushBack(_graph.getEdge(faceVertexes.get(0), faceVertexes.get(1)));
		faceEdges.pushBack(_graph.getEdge(faceVertexes.get(0), faceVertexes.get(2)));
		faceEdges.pushBack(_graph.getEdge(faceVertexes.get(1), faceVertexes.get(2)));
		return faceEdges;
	}
	
	private void init() {
		////System.out.println(_graph.getNumberOfEdges());
		int iE = 0;
		try {			
			int faceIndex = 0;
			TriMeshInducedCycles inducedCycles = new TriMeshInducedCycles(_graph);
			int nF = inducedCycles._allFaces.size();
			_edge2Faces = new VecInt[_graph.getNumberOfEdges()];
			_clusterEdges = new VecInt[nF];
			for (String face : inducedCycles._allFaces.keySet()) {
				VecInt faceEdges = packFaceEdges(inducedCycles._allFaces.get(face));
/*				//System.out.println("faceIndex: " + faceIndex + " face: " + face);
				faceEdges.dump();
				//System.out.println("**********************");
*/				_clusterEdges[faceIndex] = faceEdges;
				for (int i = 0; i < 3; i++) {
					iE = faceEdges.get(i);
					if (_edge2Faces[iE] == null) {
						_edge2Faces[iE] = new VecInt(2);
					}
					_edge2Faces[iE].pushBack(faceIndex);
				}
				faceIndex++;
			}
			////System.out.println(faceIndex);
			_clusters = new PartitionUnionFind(nF);
			_loopEdges = new VecInt(_graph.getNumberOfEdges());
		} catch (Exception e) {
			//System.out.println(iE);
			e.printStackTrace();
		}
	}

	// el value es para registrar si el cluster ya fue apareado
	private Map<Integer, Boolean> initCluster2Joined(int numParts) {
		Map<Integer, Boolean> clusterIDs = new HashMap<Integer, Boolean>();
		for (int i = 0; i < numParts; i++) {
			clusterIDs.put(i, false);
		}
		return clusterIDs;
	}	
	
	private void showCluster(int joinEdge, int cluster) {
		StringBuffer s = new StringBuffer();
		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();
		System.out.println(_clusterEdges[cluster].size());
		for (int i = 0; i < _clusterEdges[cluster].size(); i++) {
			int iE = _clusterEdges[cluster].get(i);			
			s.append(" " + iE);
			s.append("(");
			for (int j = 0; j < _edge2Faces[iE].size(); j++) {
				s.append(_clusters.find(_edge2Faces[iE].get(j)) + ",");				
			}
			s.append(") ");
/*			
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			if (!unique.containsKey(iV0)) {
				s.append(" " + iV0);
				unique.put(iV0, false);
			}
			if (!unique.containsKey(iV1)) {
				s.append(" " + iV1);
				unique.put(iV1, false);
			}
*/		}
		int iV0 = joinEdge != -1 ? _graph.getVertex0(joinEdge) : -1;
		int iV1 = joinEdge != -1 ? _graph.getVertex1(joinEdge) : -1;
		System.out.println("cluster: " + cluster + " edges: " + s + 
				" joinEdge ("+joinEdge+"): " + iV0 + " -> " + iV1);// + " #joinEdgeFaces: " + _edge2Faces[joinEdge].size());
	}
	
	private void build() {
		Map<Integer, Boolean> cluster2Joined = initCluster2Joined(_clusters.getNumberOfParts());
		int i = 0;
		//while(_clusters.getNumberOfParts() > 1 && i < 4) {
		boolean changed = true;
		int lastIterNumParts = _clusters.getNumberOfParts();
		while(_clusters.getNumberOfParts() > 1 && changed) {
			Map<Integer, Boolean> newCluster2Joined = new HashMap<Integer, Boolean>();
			for (Integer cluster : cluster2Joined.keySet()) {
				if (!cluster2Joined.get(cluster)) { // si el cluster todavia no fue apareado, buscar vecino 
					VecInt edgeAndCluster = findUnclusteredNeighbor(cluster, cluster2Joined);
					//System.out.println("ACA: " + cluster + "/" + edgeAndCluster.get(1));
					int newCluster = joinClusters(edgeAndCluster.get(0), edgeAndCluster.get(1), cluster);
					cluster2Joined.put(cluster, true);
					cluster2Joined.put(edgeAndCluster.get(1), true);
					newCluster2Joined.put(newCluster, false);
					_loopEdges.pushBack(edgeAndCluster.get(0));
					//showCluster(edgeAndCluster.get(0), newCluster);
//					if (cluster2Joined.size() == 9) {
//						System.out.println("joinClusters: " + edgeAndCluster.get(1) + " - "  + cluster);
//					}
				}
			}
			//System.out.println("********");
			//System.out.println(cluster2Joined.size());
			System.out.println(_clusters.getNumberOfParts());
			if (lastIterNumParts != _clusters.getNumberOfParts()) {
				lastIterNumParts = _clusters.getNumberOfParts();
			} else {
//				System.out.println("NOOOOO " + _clusters.getNumberOfParts());
//				for (Integer clust : cluster2Joined.keySet()) {
//					if (!cluster2Joined.get(clust)) {
//						System.out.println(clust + ": " + newCluster2Joined.get(clust));
//					} else {
//						System.out.println(clust + ": " + newCluster2Joined.get(clust));
//					}
//				}
				changed = false;
			}
			cluster2Joined = newCluster2Joined;
			i++;
		}
	}

	private VecInt appendEdges(int iE, VecInt cluster1, VecInt cluster2) {
		Map<Integer, Boolean> unique = new HashMap<Integer, Boolean>();
		if (iE == 191) {
			System.out.println("ACAAAA");
		}
		for (int i = 0; i < cluster1.size(); i++) {
			int e = cluster1.get(i);
			if (e != iE)
				unique.put(e, true);			
		}
		for (int i = 0; i < cluster2.size(); i++) {
			int e = cluster2.get(i);
			if (e != iE)
				unique.put(e, true);			
		}
		VecInt edges = new VecInt(unique.size());
		for (Integer edge : unique.keySet()) {
			if (iE == 191) {
				System.out.println("eje: " + edge);
			}
			edges.pushBack(edge);
		}
		return edges;
	}
	
	private int joinClusters(int iE, int cluster1, int cluster2) {
		// OBS: la cantidad de ejes del cluster resultante es:
		// la suma de los clusters excluyendo el eje del join
		int cl = -1;
		if (cluster1 == 33) {
			cl = cluster1;			
		} else if (cluster2 == 33) {
			cl = cluster2;
		}
		if (cl != -1) {
			//System.out.println("entre");
//			showCluster(-1, cl);
//			System.out.println("ACAAA: " + cluster1 + " - " + cluster2);
		}
		//System.out.println("cluster1: " + cluster1 + " cluster2: " + cluster2);
		//System.out.println("ACAAA: " + cluster1 + " - " + cluster2);
		int joinedCluster = _clusters.join(cluster1, cluster2);
		_clusterEdges[joinedCluster] = appendEdges(iE, _clusterEdges[cluster1], _clusterEdges[cluster2]);
		//_clusterEdges[joinedCluster].dump();
		return joinedCluster;
	}

	private int getNeighborCluster(int cluster, int iE) {
		boolean found = false;
		VecInt edgeFaces = _edge2Faces[iE];
		int neighborCluster = -1;
		for (int i = 0; i < edgeFaces.size() && !found; i++) {
			neighborCluster = _clusters.find(edgeFaces.get(i));
//			System.out.println("ACAAAA getNeighborCluster: " + cluster + " - " + neighborCluster + " iE: " + iE);
			if (cluster != neighborCluster) {
//				System.out.println("entre!");
				found = true;
			}
		}
		//System.out.println("sali!!");
		if (neighborCluster == cluster) 
			return -1;
		return neighborCluster;
	}
	
	// esto es para inicializar la eleccion de un eje en un valor valido
	private int findFirstClusterEdge(int cluster) {
		int iE = -1;
		boolean found = false;
		VecInt clusterEdges = _clusterEdges[cluster];
		for (int i = 0; i < clusterEdges.size() && !found; i++) {
			iE = clusterEdges.get(i);
			if (getNeighborCluster(cluster, iE) != -1) {				
				found = true;
			}
		}
//		if (!found) {
//			//System.out.println("!found");
//		}
		return iE;
	}
	
	// devuelve el eje que hay que sacar para juntar los 2 clusters 
	private VecInt findUnclusteredNeighbor(int cluster, Map<Integer, Boolean> cluster2Joined) {
		VecInt clusterEdges = _clusterEdges[cluster];
		VecInt edgeAndCluster = new VecInt(2);
		//inicializo en un vecino arbitrario para devolver alguno
//		//System.out.println("ACA: " + cluster);
//		if (cluster == 20) {
//			clusterEdges.dump();
//		}
		int iE = findFirstClusterEdge(cluster);
		edgeAndCluster.pushBack(iE);
		edgeAndCluster.pushBack(getNeighborCluster(cluster, iE));
//		if (cluster == 33) {
//			showCluster(-1, cluster);
//		}
		boolean found = false;
		for (int i = 0; i < clusterEdges.size() && !found; i++) {
			iE = clusterEdges.get(i);
			if (_edge2Faces[iE].size() > 1) {
				////System.out.println("ACA2: " + _edge2Faces[iE].size());
				int neighbor = getNeighborCluster(cluster, iE);
				//System.out.println("ACCCCAAA");
				if (neighbor != -1) { // si el eje separa 2 clusters
//					System.out.println("Entre ACA!");
					if (!cluster2Joined.get(neighbor)) { // si el vecino no fue apareado aun						
						found = true;
						edgeAndCluster.set(0,iE);
						edgeAndCluster.set(1,neighbor);
						//System.out.println("******");
						//System.out.println("ACAAAA: " + neighbor);
						//edgeAndCluster.dump();
						//System.out.println("******");
					} else {
//						System.out.println("Y ACA!");						
					}
				}
			}
		}
		if (!found) { // si los vecinos estan todos apareados, apareo con el 1ro. que encuentro 
			//System.out.println("!found");
			//edgeAndCluster.dump();
		}
		return edgeAndCluster;
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
			//System.out.println(iE + " " + iV0 + " " + iV1);
			clusterGraph.insertEdge(iV0, iV1);
		}
		return clusterGraph;
	}
	
	public TreeMatsui buildLABI() {
		Graph clusterGraph = buildClusterGraph();
		EdgeRelabelGraph relabelGraph = new EdgeRelabelGraph(clusterGraph, _graph.getNumberOfEdges());
		for (int i = 0; i < _loopEdges.size(); i++) {
			int iE = _loopEdges.get(i);
			int iV0 = _graph.getVertex0(iE);
			int iV1 = _graph.getVertex1(iE);
			//System.out.println(iV0 + " " + iV1);
			int index = clusterGraph.insertEdge(iV0, iV1);
			//System.out.println(index);
			relabelGraph.addExtraEdge(index);
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
		WrlReader reader = new WrlReader("/home/manuel/workspace/jdgp/img/"+file+".wrl");
		PolygonMesh mesh = reader.getMesh();
		Graph g = mesh.getGraph();

//		int iV0 = 73;
//		int iV1 = 75;
				
//		System.out.println(g.getEdge(iV0, iV1)); //1655: 514 519
		TriMeshFaceClusteringTreeAlfa faceCluster = new TriMeshFaceClusteringTreeAlfa(g);
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
