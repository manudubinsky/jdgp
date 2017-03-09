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
			System.out.println(_nodes + "\t" + _edges + "\t" + _testCaseNum + "\t" + 
								_maxIter + "\t" + _alpha + "\t" + _beta + "\t" + _error + "\t" + _nanoSecs/1000000);
		}		
	}

	public void test1(Graph g) throws Exception {
		System.out.println("begin test1");
		MethodStats stats = new MethodStats(20);
		ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
		System.out.println("built weighted graph");
		GRASPParams params = new GRASPParams(2, 10, 20000);
		FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, stats);
		stats.start("completeTest1");
		GRASPSolution s = integ.integrate();
		stats.stop("completeTest1");
		VecInt weights = form.getWeightedGraph().getEdgeWeights();
		float p = s.verify();
		float q = weights.toFloat().squareNorm();
		stats.dump();
		System.out.println("p: " + p + " q: " + q + " quot: " + p/q);
		//form.getNodeValues().dump();
		//s.getX().dump();
		//s.getX().subtract(form.getNodeValues()).dump();
		//s.getX().add(form.getNodeValues().toFloat()).dump();
	}

	public void completeGraphsTest1(int n) throws Exception {		
		test1(Graph.buildCompleteGraph(n));
	}

/*
	public void completeGraphsParams(int nodesCnt, int testCases) throws Exception {
		MethodStats stats = new MethodStats(20);
		GRASPTestCase[] tests = new GRASPTestCase[10*10*testCases];
		int testNum = 0;
		Graph g = Graph.buildCompleteGraph(nodesCnt);
		int edgesCnt = g.getNumberOfEdges();
		for (int test = 0; test < testCases; test++) {			
			ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
			VecInt weights = form.getWeightedGraph().getEdgeWeights();
			//weights.dump();
			for (int alpha = 1; alpha <= 10; alpha++) {
				for (int beta = 1; beta <= 10; beta++) {
					System.out.println(test + " " + alpha + " " + beta);
					for (int iter = 10000; iter <= 10000; iter+=10000) {
						GRASPParams params = new GRASPParams(alpha, beta, iter);
						FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, stats);
						long start = System.nanoTime();
						GRASPSolution s = integ.integrate();
						long delta = System.nanoTime() - start;
						float p = s.verify(weights);
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
*/	
	public void completeGraphs() throws Exception {
		completeGraphsTest1(100);
		//completeGraphsParams(100, 1);
	}

	public void cycleGraphsTest1(int n) throws Exception {		
		test1(Graph.buildCycleGraph(n));
	}

	public void cycleGraphs() throws Exception {
		cycleGraphsTest1(100);
	}

	public void bipartiteGraphsTest1(int n, int m) throws Exception {		
		test1(Graph.buildCompleteBipartite(n, m));
	}

	public void bipartiteGraphs() throws Exception {
		bipartiteGraphsTest1(10, 10);
	}

	public void wrlGraph() throws Exception {
		System.out.println("begin wrlGraph");
		WrlReader reader = new WrlReader("/home/manuel/doctorado/jdgp/jDGP/img/venusv.wrl");
		Graph g = reader.getMesh().getGraph();
		test1(g);
	}

	public static void main(String[] args) throws Exception {
		GRASPTests tests = new GRASPTests();
		tests.wrlGraph();
		//tests.completeGraphs();
		//tests.cycleGraphs();
		//tests.bipartiteGraphs();
	}

}
