package edu.jdgp;

import edu.jdgp.DGP.Graph;
import edu.jdgp.CustomSpanningTree.WeightedGraph;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.FormIntegratorExact;
import java.util.concurrent.ThreadLocalRandom;

/*
 * Implementacion GRASP del algoritmo para integrar 1-formas
 * Ver: Integration1FormsGRASP.pdf
 */ 

public class WeightedGraphBuilder {
	public static class ExactForm {
		private WeightedGraph _weightedGraph;
		private VecInt _nodeValues;
		
		public ExactForm(WeightedGraph weightedGraph, VecInt nodeValues) {
			_weightedGraph = weightedGraph;
			_nodeValues = nodeValues;
		}
		
		public WeightedGraph getWeightedGraph() {
			return _weightedGraph;
		}
		
		public VecInt getNodeValues() {
			return _nodeValues;
		}
		
		public void dump() {
			_weightedGraph.dump();
			_nodeValues.dump();
		}

	}

	public static ExactForm buildExactForm(Graph g, int maxNodeValue) {
		//1) generar una funcion f: V->Z_+ al azar
		int nV = g.getNumberOfVertices();
		VecInt f = new VecInt(nV);
		for (int i = 0; i < nV; i++) {
			f.pushBack(ThreadLocalRandom.current().nextInt(1, maxNodeValue + 1));
		}
		//2) para cada eje e=(v,w) definir el peso del eje como: w(e) = max(f(v), f(w)) - min(f(v), f(w))
		int nE = g.getNumberOfEdges();
		VecInt w = new VecInt(nE);
		for (int i = 0; i < nE; i++) {
			int fV0 = f.get(g.getVertex0(i));
			int fV1 = f.get(g.getVertex1(i));
			w.pushBack(Math.max(fV0, fV1) - Math.min(fV0,fV1));
		}
		return new ExactForm((new WeightedGraph(g, w)), f);
	}

	public static ExactForm buildExactForm(Graph g, VecInt f) {
		//1) para cada eje e=(v,w) definir el peso del eje como: w(e) = max(f(v), f(w)) - min(f(v), f(w))
		int nE = g.getNumberOfEdges();
		VecInt w = new VecInt(nE);
		for (int i = 0; i < nE; i++) {
			int fV0 = f.get(g.getVertex0(i));
			int fV1 = f.get(g.getVertex1(i));
			w.pushBack(Math.max(fV0, fV1) - Math.min(fV0,fV1));
		}
		return new ExactForm((new WeightedGraph(g, w)), f);
	}
/*
	public static void main(String[] args) throws Exception {
		//3.0 2.0 10.0 9.0 4.0
		VecInt f = new VecInt(4);
		f.pushBack(3); f.pushBack(2); f.pushBack(10); f.pushBack(9); f.pushBack(4);
		//ExactForm form = WeightedGraphBuilder.buildExactForm(Graph.buildCompleteGraph(5), 10) ;
		ExactForm form = WeightedGraphBuilder.buildExactForm(Graph.buildCompleteGraph(5), f) ;
		form.dump();
		FormIntegratorExact s = new FormIntegratorExact(form.getWeightedGraph());
		s.integrate().dump(); 
	}
*/	

	public static void main(String[] args) throws Exception {		
		for (int i = 4; i < 10; i++) {
			System.out.println(i);
			VecFloat ones = new VecFloat(i, 1);
			for (int j = 0; j < 20; j++) {
				System.out.println("\t" + j);
				ExactForm form = WeightedGraphBuilder.buildExactForm(Graph.buildCompleteGraph(i), 10) ;
				//form.dump();
				FormIntegratorExact s = new FormIntegratorExact(form.getWeightedGraph());
				VecFloat x = s.integrate();
				VecFloat origValues = form.getNodeValues().toFloat();
				if (!x.subtract(origValues).allEqual() && !x.addMultiple(origValues,2).allEqual()) {
					origValues.dump();
					throw new Exception();
				}
			}
		}
	}
}
