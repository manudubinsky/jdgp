package edu.jdgp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.VecBool;
import edu.jdgp.DGP.VecInt;

/*
Obtiene muestras de una malla triangular.
	Hace un BFS de una determinada profundidad a partir de un nodo 

*/
public class TriMeshSampler {
	private int _root;
	private int _depth;
	private Graph _graph;
	private VecInt _sampleEdges;
	private VecInt _vertexLabels;
	private int _vertexCnt;
	
	public TriMeshSampler (int root, int depth, Graph graph) throws Exception {
		_root = root;
		_depth = depth;
		_graph = graph;		
		_sampleEdges = new VecInt(10);
		build();
	}

	public void build() {
		int nV= _graph.getNumberOfVertices();
		VecBool visited = new VecBool(nV, false);
		VecInt queue = new VecInt(nV);
		VecInt vertexesDepth = new VecInt(nV, -1);
		_vertexLabels = new VecInt(nV, -1);
		queue.pushBack(_root);
		visited.set(_root, true);
		vertexesDepth.set(_root, 0);
		int queueIdx = 0;
		_vertexCnt = 0;
		_vertexLabels.set(_root, _vertexCnt++);
		VecBool visitedEdge = new VecBool(_graph.getNumberOfEdges(),false);
		while (queueIdx < queue.size()) {
			int v = queue.get(queueIdx++);
			VecInt vertexEdges = _graph.getVertexEdges(v);
			int vertexDepth = vertexesDepth.get(v);
			for (int j = 0; j < vertexEdges.size(); j++) {
				int edge = vertexEdges.get(j);
				int neighbor = _graph.getNeighbor(v, edge);
				String cond = "";
				if (!visited.get(neighbor)) {
					cond = "!visited.get(neighbor)"; 
					if (vertexDepth < _depth) {
						cond = cond + " && vertexDepth < _depth"; 
						queue.pushBack(neighbor);
						vertexesDepth.set(neighbor,vertexDepth+1);
						visited.set(neighbor, true);
						_sampleEdges.pushBack(edge);
						_vertexLabels.set(neighbor, _vertexCnt++);
					}
				} else {
					cond = "visited.get(neighbor)";
					if (!visitedEdge.get(edge)) {
						cond = cond + " && !visitedEdge.get(edge)";
						_sampleEdges.pushBack(edge);
					}
				}
/*				if (v == 0) {
					System.out.println("parent: " + v + " edge: " + edge + 
										" neighbor: " + neighbor + " cond: " + cond);
				}
*/				visitedEdge.set(edge, true);
			}
		}
	}
	
	public VecInt getSampleEdges() {
		return _sampleEdges;
	}
	
	public void dumpGraphViz(String fileName) {
		BufferedWriter writer = null;
        try {
            //create a temporary file
            //String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmssSS").format(Calendar.getInstance().getTime());
            File logFile = new File(fileName);

            // This will output the full path where the file will be written to...
            //System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write("graph {\n");
            for (int i = 0; i < _sampleEdges.size(); i++) {
            	int edge = _sampleEdges.get(i);
            	int iV0 = _vertexLabels.get(_graph.getVertex0(edge));
            	int iV1 = _vertexLabels.get(_graph.getVertex1(edge));
        		writer.write("  " + iV0 + " -- " + iV1 + " [color=blue]\n");
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

	public SparseMatrixInt adjMatrix() {
		SparseMatrixInt adjMatrix = new SparseMatrixInt(_vertexCnt);
		for (int i = 0; i < _sampleEdges.size(); i++) {
			int edge = _sampleEdges.get(i);
        	int iV0 = _vertexLabels.get(_graph.getVertex0(edge));
        	int iV1 = _vertexLabels.get(_graph.getVertex1(edge));
        	adjMatrix.set(iV0, iV1, 1);
        	adjMatrix.set(iV1, iV0, 1);
		}
		return adjMatrix;
	}

	public static void main(String[] args) throws Exception {
		String file = "ateneav";
		WrlReader reader = new WrlReader("/home/manuel/workspace/jdgp/img/"+file+".wrl");
		PolygonMesh mesh = reader.getMesh();
		Graph g = mesh.getGraph();
//		System.out.println(" nV: " + g.getNumberOfVertices() + 
//		                   " nE: " + g.getNumberOfEdges() + 
//		                   " nF: " + mesh.getNumberOfFaces());

		int vertex = 514;
		int depth = 1;
		TriMeshSampler s = new TriMeshSampler(vertex,depth,g);
//		VecInt v = s.getSampleEdges();
//		v.dump();
//		String vertexType = "";
//		for (int i = 0; i < v.size(); i++) {
//			if (mesh.isBoundaryEdge(i)) {
//				vertexType = "boundary";
//			} else if (mesh.isSingularEdge(i)) {
//				vertexType = "singular";
//			} else if (mesh.isRegularEdge(i)) {
//				vertexType = "regular";
//			}
//			System.out.println(i + " " + vertexType);
//		}
//		VecInt vertexEdges = g.getVertexEdges(0);
//		for (int i = 0; i < vertexEdges.size(); i++) {
//			System.out.println("edge: " + i + " iV0: " + g.getVertex0(vertexEdges.get(i)) 
//								+ " iV1: " + g.getVertex1(vertexEdges.get(i)));			
//		}
		
		s.dumpGraphViz("/home/manuel/20170817/doctorado/octave/tesis-octave"+
						"/ejemplos/connected-graphs/tri-meshes/samples/"+file+"." + 
						vertex + "." + depth + ".sample");
	
		//s.adjMatrix().fullDump();
	}

/*
	public static void main(String[] args) throws Exception {
		int nodeCnt = 17;
		String path = "/home/manuel/20170817/doctorado/octave/tesis-octave/"+
						"ejemplos/connected-graphs/tri-meshes/samples";
		String file = "ateneav.0.2.adj_mat";
		Graph g = GraphTarjanRead.buildFromAdjMatrix(nodeCnt, path + "/" + file);
//		g.dump();
		TriMeshInducedCycles t = new TriMeshInducedCycles(g);
		Map<String, VecInt> allFaces = t._allFaces;
		System.out.println(allFaces.size());
		for (String face : allFaces.keySet()) {
			allFaces.get(face).dump();
		}	
	}
*/	
}
