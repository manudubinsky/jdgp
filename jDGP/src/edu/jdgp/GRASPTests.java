package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.FormIntegratorExact;
import edu.jdgp.FormIntegratorGRASP;
import edu.jdgp.FormIntegratorGRASP.GRASPParams;
import edu.jdgp.FormIntegratorGRASP.GRASPSolution;
import edu.jdgp.WrlReader;
import edu.jdgp.WeightedGraphBuilder;
import edu.jdgp.WeightedGraphBuilder.ExactForm;
import edu.jdgp.MethodStats;
import java.util.concurrent.ThreadLocalRandom;


/*
 * Tests de estrategia GRASP para integrar 1-formas
 */ 

public class GRASPTests {
	public static final String sep = ",";
	public static class GRASPTestCase {
		private int _nodes;
		private int _edges;
		private int _testCaseNum;
		private int _alpha;
		private int _beta;
		private int _maxIter; // cantidad de iteraciones de GRASP
		private long _nanoSecs;
		private float _error;
		
		public GRASPTestCase(int nodes,int edges,int testCaseNum,int alpha,int beta,int maxIter,long nanoSecs,float error) {
			_nodes = nodes;
			_edges = edges;
			_testCaseNum = testCaseNum;
			_alpha = alpha;
			_beta = beta;
			_maxIter = maxIter;
			_nanoSecs = nanoSecs;
			_error = error;
		}
		
		public void dump() {
			System.out.println(_nodes + sep + _edges + sep + _testCaseNum + sep + 
								_maxIter + sep + _alpha + sep + _beta + sep + 
								_error + sep + _nanoSecs/1000000);
		}		
	}

	public void test1(Graph g) throws Exception { 
		//System.out.println("begin test1");
		//MethodStats stats = new MethodStats(20);
		ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
		//System.out.println("built weighted graph");
		GRASPParams params = new GRASPParams(2, 10, 20000);
		//FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, stats);
		FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, null);
		//stats.start("completeTest1");
		GRASPSolution s = integ.integrate();
		//stats.stop("completeTest1");
		VecInt weights = form.getWeightedGraph().getEdgeWeights();
		float p = s.verify();
		float q = weights.toFloat().squareNorm();
		//stats.dump();
		//System.out.println("p: " + p + " q: " + q + " quot: " + p/q);
	}

	public void completeGraphsTest1(int n) throws Exception {
		test1(Graph.buildCompleteGraph(n));
	}

	public void completeGraphsTest2(int n, int testCases) throws Exception {
		Graph g = Graph.buildCompleteGraph(n);
		for (int i = 0; i < testCases; i++) {
			test1(g);
		}
	}

	public void completeGraphsParams(int nodesCnt, int testCases) throws Exception {
		MethodStats stats = new MethodStats(20);
		GRASPTestCase[] tests = new GRASPTestCase[4*testCases];
		System.out.println("Total casos: " + (4*testCases));
		int testNum = 0;
		Graph g = Graph.buildCompleteGraph(nodesCnt);
		int edgesCnt = g.getNumberOfEdges();
		for (int test = 0; test < testCases; test++) {			
			ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
			VecInt weights = form.getWeightedGraph().getEdgeWeights();
			//weights.dump();
			for (int alpha = 1; alpha <= 4; alpha++) {
				for (int beta = 10; beta <= 10; beta+=2) {
					System.out.println(testNum + " " + test + " " + alpha + " " + beta);
					for (int iter = 20000; iter <= 20000; iter+=10000) {
						GRASPParams params = new GRASPParams(alpha, beta, iter);
						FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, stats);
						long start = System.nanoTime();
						GRASPSolution s = integ.integrate();
						long delta = System.nanoTime() - start;
						float p = s.verify();
						float q = weights.toFloat().squareNorm();
						//System.out.println("***********" + p/q);
						tests[testNum++] = new GRASPTestCase(g.getNumberOfVertices(),
														g.getNumberOfEdges(),
														test,
														alpha,
														beta,
														iter,
														delta,
														p/q);
					}
				}
			}
		}
		for (int i = 0; i < tests.length; i++) {
			if (tests[i] != null)
				tests[i].dump();
		}
		stats.dump();
	}

	public void completeGraphs() throws Exception {
		completeGraphsTest2(400, 20);
		//completeGraphsTest1(100);
		//completeGraphsParams(100, 50);
	}

	public void cycleGraphsTest1(int n) throws Exception {		
		test1(Graph.buildCycleGraph(n));
	}

	public void cycleGraphsTest2(int n, int testCases) throws Exception {
		Graph g = Graph.buildCycleGraph(n);
		for (int i = 0; i < testCases; i++) {
			test1(g);
		}
	}

	public void wrlGraphTest1(Graph g) throws Exception {		
		test1(g);
	}

	public void wrlGraphTest2(Graph g, int testCases) throws Exception {
		for (int i = 0; i < testCases; i++) {
			test1(g);
		}
	}

	public void cycleGraphs() throws Exception {
		//cycleGraphsTest1(2000);
		cycleGraphsTest2(4000,20);
	}

	public void bipartiteGraphsTest1(int n, int m) throws Exception {		
		test1(Graph.buildCompleteBipartite(n, m));
	}

	public void bipartiteGraphs() throws Exception {
		bipartiteGraphsTest1(10, 10);
	}

//venusv.wrl  -> nV: 819 nE: 2452
//elephav.wrl -> nV: 623 nE: 1759
	public void wrlGraph() throws Exception {
		System.out.println("begin wrlGraph");
		WrlReader reader = new WrlReader("/home/manuel/doctorado/jdgp/jDGP/img/elephav.wrl");
		Graph g = reader.getMesh().getGraph();
		//System.out.println("nV: " + g.getNumberOfVertices() + " nE: " + g.getNumberOfEdges());		
		wrlGraphTest2(g, 20);
	}

	public static void main(String[] args) throws Exception {
		GRASPTests tests = new GRASPTests();
		tests.wrlGraph();
		//tests.completeGraphs();
		//tests.cycleGraphs();
		//tests.bipartiteGraphs();
	}

}
