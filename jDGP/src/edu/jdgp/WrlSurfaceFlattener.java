package edu.jdgp;

import wrl.WrlIndexedFaceSet;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class WrlSurfaceFlattener {
	private int _iterStep1;
	private int _iterStep2;
	private int _algorithmIter;
	private float _lambdaStep1;
	private float _lambdaStep2;
	
	public WrlSurfaceFlattener(int iterStep1, int iterStep2, int algorithmIter, float lambdaStep1, float lambdaStep2) {
		_iterStep1 = iterStep1;
		_iterStep2 = iterStep2;
		_algorithmIter = algorithmIter;
		_lambdaStep1 = lambdaStep1;
		_lambdaStep2 = lambdaStep2;		
	}
	
	public mesh.VecFloat flatten(WrlIndexedFaceSet wrl) throws Exception {
		PolygonMesh _mesh = new PolygonMesh(VecFloat.fromWrlVecFloat(wrl.getCoordValue()), VecInt.fromWrlVecInt(wrl.getCoordIndex()));

		SurfaceFlattenerStep1 step1 = new SurfaceFlattenerStep1(); // modificar las normales a las caras
		SurfaceFlattenerStep2 step2 = new SurfaceFlattenerStep2(); // modificar los nodos de acuerdo a las normales del step1
		SurfaceFlattenerParams paramsMain = new SurfaceFlattenerParams(-1, _algorithmIter);
		SurfaceFlattenerParams paramsStep1 = new SurfaceFlattenerParams(_lambdaStep1, _iterStep1);
		SurfaceFlattenerParams paramsStep2 = new SurfaceFlattenerParams(_lambdaStep2, _iterStep2);
		while (paramsMain.hasToContinue()) {
			VecFloat facesNormals = step1.execute(_mesh, paramsStep1.reset());
			_mesh._coord = step2.execute(_mesh, facesNormals, paramsStep2.reset());
		}
		
		return VecFloat.toWrlVecFloat(_mesh._coord);
	}

}
