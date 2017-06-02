package edu.jdgp;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class MethodStats {
	public class ItemStats {
		private long begin; //inicio para despues calcular el delta
		private long totalNanoSecs;
		private long totalCalls; //cantidad de veces que se controlo el item
		
		public ItemStats() {
		}
		
		public void begin() {
			begin = System.nanoTime();
		}
		
		public void end() {
			totalCalls++;
			totalNanoSecs += System.nanoTime() - begin;
		}
		
		public void dump(String tag) {
		//imprime en milisegundos
			System.out.println(tag + "\t" + totalNanoSecs / 1000000 + "\t" + totalCalls);
		}
	}

	private HashMap<String, ItemStats> stats;
	private int periodicDumpSecs;
	private long lastDump;
	
	public MethodStats() {
		this(-1);
	}

	public MethodStats(int dumpPeriod) {
		stats = new HashMap<String, ItemStats>();
		periodicDumpSecs = dumpPeriod;
	}
	
	public void start(String tag) {
		periodicDump();
		if (!stats.containsKey(tag))
			stats.put(tag, new ItemStats());
		stats.get(tag).begin();
	}

	public void stop(String tag) {
		if (stats.containsKey(tag))
			stats.get(tag).end();
		periodicDump();
	}
	
	public void periodicDump() {
		if (periodicDumpSecs != -1 && (System.nanoTime()/1000000000 - lastDump) > periodicDumpSecs) {
			dump();
			lastDump = System.nanoTime()/1000000000;
		}
	}
	
	public void dump() {
		Set set = stats.keySet();
		Iterator iterator = set.iterator();
		while(iterator.hasNext()) {
			String key = (String)iterator.next();
			stats.get(key).dump(key);
		}
	}
	
	public static void main(String args[]) {
		MethodStats stats = new MethodStats();
		
		stats.start("m1");
		for (int i = 0; i < 100000; i++) {
			stats.start("m2");	
			stats.stop("m2");
		}
		stats.stop("m1");
		stats.dump();
	}
}
