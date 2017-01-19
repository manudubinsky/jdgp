package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.FormIntegratorExact;
import edu.jdgp.FormIntegratorGRASP;
import edu.jdgp.FormIntegratorGRASP.GRASPParams;
import edu.jdgp.FormIntegratorGRASP.GRASPSolution;
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

	public void completeGraphsLimit() throws Exception {
		MethodStats stats = new MethodStats(20);
		int i = 100;
		int edgesCnt =  i * (i - 1) / 2;
		ExactForm form = WeightedGraphBuilder.buildExactForm(Graph.buildCompleteGraph(i), 10);
		GRASPParams params = new GRASPParams(2, 10, 20000);
		FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, stats);
		stats.start("completeGraphsLimit");
		GRASPSolution s = integ.integrate();
		stats.stop("completeGraphsLimit");
		VecInt weights = form.getWeightedGraph().getEdgeWeights();
		float p = s.verify(weights);
		float q = weights.toFloat().squareNorm();
		stats.dump();
		System.out.println("quot: " + p/q);
	}


	public void completeGraphsParams(int nodesCnt, int testCases) throws Exception {
		MethodStats stats = new MethodStats(20);
		GRASPTestCase[] tests = new GRASPTestCase[10*10*testCases];
		int testNum = 0;
		Graph g = Graph.buildCompleteGraph(nodesCnt);
		int edgesCnt = g.getNumberOfEdges();
		for (int test = 0; test < testCases; test++) {
			System.out.println(test);
			ExactForm form = WeightedGraphBuilder.buildExactForm(g, 10);
			VecInt weights = form.getWeightedGraph().getEdgeWeights();
			weights.dump();
			for (int alpha = 1; alpha <= 10; alpha++) {
				for (int beta = 1; beta <= 10; beta++) {
					GRASPParams params = new GRASPParams((int)Math.max(edgesCnt / 100 * alpha, 1),
															(int)Math.max(edgesCnt / 100 * beta, 1), 1000);
					FormIntegratorGRASP integ = new FormIntegratorGRASP(form.getWeightedGraph(), params, stats);
					long start = System.nanoTime();
					GRASPSolution s = integ.integrate();
					long delta = System.nanoTime() - start;
					float p = s.verify(weights);
					float q = weights.toFloat().squareNorm();
					System.out.println("***********" + p/q);
					tests[testNum++] = new GRASPTestCase(g.getNumberOfVertices(),
													g.getNumberOfEdges(),
													test,
													alpha,
													beta,
													1000,
													delta,
													p/q);
				}
			}
		}
		for (int i = 0; i < tests.length; i++) {
			tests[i].dump();
		}
		stats.dump();
	}
	
	public void completeGraphs() throws Exception {
		completeGraphsLimit();
		//completeGraphsParams(50, 50);
	}

	public static void main(String[] args) throws Exception {
		GRASPTests tests = new GRASPTests();
		tests.completeGraphs();
	}

}
