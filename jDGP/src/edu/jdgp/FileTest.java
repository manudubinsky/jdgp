package edu.jdgp;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

import edu.jdgp.DGP.Graph;
import edu.jdgp.DGP.VecInt;

public class FileTest {

	public static void main(String[] args) {
		int nodeCnt = 7;
		//String prefix = "6";
		String fName = "A18";
		Graph _graph = GraphTarjanRead.buildFromAdjMatrix(nodeCnt, "/home/manuel/20170817/doctorado/octave/tesis-octave/ejemplos/connected-graphs/deg-"+nodeCnt+"/" + fName + ".mat");
		BufferedWriter writer = null;
	    try {
	        //File logFile = new File("/home/manuel/20170817/doctorado/octave/tesis-octave/ejemplos/connected-graphs/spanning_trees/" + fName + ".graph");
	        File logFile = new File("/home/manuel/20170817/doctorado/octave/tesis-octave/ejemplos/connected-graphs/tri-meshes/" + fName + ".graph");
	        writer = new BufferedWriter(new FileWriter(logFile));
	        writer.write("graph {\n");
	        for (int i = 0; i < _graph.getNumberOfEdges(); i++) {
	        	int iV0 = _graph.getVertex0(i);
	        	int iV1 = _graph.getVertex1(i);
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
	
/*
	public static void main(String[] args) {
		BufferedWriter writer = null;
        try {
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File logFile = new File("/home/manuel/20170817/doctorado/octave/tesis-octave/ejemplos/connected-graphs/spanning_trees/prueba");

            // This will output the full path where the file will be written to...
            System.out.println(logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile, true));
            writer.write("graph {\n");
            writer.write("  1 -- 2 [color=red]\n");
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
*/
}
