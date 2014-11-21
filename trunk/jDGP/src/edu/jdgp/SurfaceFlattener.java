package edu.jdgp;

import edu.jdgp.DGP.PolygonMesh;

public class SurfaceFlattener {
	DecoratedPolygonMesh _surface;
	
	public SurfaceFlattener(PolygonMesh surface) throws Exception {
		_surface = new DecoratedPolygonMesh(surface);
	}
	
	/*
	 *  devuelve una version aplanada de la superfice (ie. los angulos entre 
	 *  las normales con ~= 0 y las normas de los ejes son similares)  
	 */
	
	public PolygonMesh flatten(){
	
		return null;
	}
}
