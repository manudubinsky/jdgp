package edu.jdgp;

import edu.jdgp.DGP.EdgeRelabelGraph;
import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.SparseMatrixInt;
import edu.jdgp.DGP.VecInt;

public class StarSamples {
	public static GraphTarjanRead build11Star() {
		GraphTarjanRead g = new GraphTarjanRead(11);
		g.insertEdge(0, 1);
		g.insertEdge(0, 2);
		g.insertEdge(0, 3);
		g.insertEdge(0, 4);
		g.insertEdge(0, 5);
		g.insertEdge(0, 6);
		g.insertEdge(0, 7);
		g.insertEdge(0, 8);
		g.insertEdge(0, 9);
		g.insertEdge(0, 10);
		
		g.insertEdge(1, 3);
		g.insertEdge(1, 4);
		g.insertEdge(1, 7);
		g.insertEdge(1, 8);

		g.insertEdge(2, 5);
		g.insertEdge(2, 6);
		g.insertEdge(2, 9);
		g.insertEdge(2, 10);

		g.insertEdge(3, 7);
		
		g.insertEdge(4, 8);
		
		g.insertEdge(5, 9);
		
		g.insertEdge(6, 10);
		return g;
	}

	public static VecInt build11Tree2(Graph graph) {
		VecInt v = new VecInt(graph.getNumberOfVertices()-1);
		v.pushBack(graph.getEdge(0, 1));
		v.pushBack(graph.getEdge(0, 2));
		
		v.pushBack(graph.getEdge(1, 3));
		v.pushBack(graph.getEdge(1, 4));
		
		v.pushBack(graph.getEdge(2, 5));
		v.pushBack(graph.getEdge(2, 6));
		
		v.pushBack(graph.getEdge(3, 7));
		
		v.pushBack(graph.getEdge(4, 8));
		
		v.pushBack(graph.getEdge(5, 9));
		
		v.pushBack(graph.getEdge(6, 10));
		return v;
	}

/*	
	public static void main(String[] args) throws Exception {
		int nodeCnt = 11;
		SpanningTreesMatsui s;
		String tag = "star-"+nodeCnt;
		MaxZerosTrees collector;
		String path = "/home/manuel/20170817/doctorado/octave/tesis-octave/ejemplos/connected-graphs/stats/stars";
		LABILogger log = new LABILogger(path + "/star-" + nodeCnt + "-maxZerosTrees.csv");		
		s = new SpanningTreesMatsui(StarSamples.build11Star());
		collector = new MaxZerosTrees(s, tag, log);
		s.allSpanningTrees();
		collector.postProcess();
		int treeCnt = collector.getTreeCnt();
		System.out.println(tag + " - treeCnt: " + treeCnt);
		log.flush();
	}
*/
/*
	public static void main(String[] args) throws Exception {
		int nodeCnt =11;
		SpanningTreesMatsui s;
		String path = "/home/manuel/20170817/doctorado/octave/tesis-octave/ejemplos/connected-graphs/stats/stars";
		StarTreeCheckerCollector collecttor;
		LABILogger log = new LABILogger(path + "/deg-" + nodeCnt + "-nonstar.txt");
		FileReaderSimple fs = new FileReaderSimple(path + "/star-" + nodeCnt + "-maxZerosTrees.csv");
		while (fs.hasItem()) {
			Item item = fs.nextItem();
			if (item.getData().size() > 0) {
				String fName = item.fileName();
				s = new SpanningTreesMatsui(StarSamples.build11Star());
				collector = new StarTreeCheckerCollector(s, fName, log, item.getData());
				s.allSpanningTrees();
				collector.postProcess();
				int treeCnt = collector.getTreeCnt();
				System.out.println(fName + " - treeCnt: " + treeCnt);							
			}
			//System.out.println(item.toString());
		}
		log.flush();
	}
*/

	public static void main(String[] args) throws Exception {
		Graph g = StarSamples.build11Star();
		TreeMatsui tree = new TreeMatsui(new EdgeRelabelGraph(g));
		VecInt treeEdges = new VecInt(g.getNumberOfVertices()-1, -1);
		for (int i = 0; i < treeEdges.size(); i++) {
			treeEdges.set(i,i);
		}
		tree.setTreeEdges(treeEdges);
		System.out.println(tree.labiCantZeros());
		tree.labiMatrix().fullDump();
		//SparseMatrixInt labi = tree.labiMatrix();
		//labi.transpose().multiply(labi).toOctave();

		System.out.println("****************");
		
		tree.setTreeEdges(build11Tree2(g));
		System.out.println(tree.labiCantZeros());
		tree.labiMatrix().fullDump();
		//labi = tree.labiMatrix();
		//labi.transpose().multiply(labi).toOctave();
	}

}
