package edu.jdgp;

import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;

public class SurfaceFlattenerTestCases {

/*	  
    point [
       1.633 -0.943 -0.667 # V0
       0.000  0.000  2.000 # V1
      -1.633 -0.943 -0.667 # V2
       0.000  1.886 -0.667 # V3
    ]
  }
  coordIndex [
    0 1 2 -1 # F0
    3 1 0 -1 # F1
    2 1 3 -1 # F2
  ]
*/

	public static PolygonMesh buildPyramid() throws Exception {
		  VecFloat coord = new VecFloat(16);
		  coord.pushBack(1.633f);
		  coord.pushBack(-0.943f);
		  coord.pushBack(-0.667f);
		  coord.pushBack(0.000f);
		  coord.pushBack(0.000f);
		  coord.pushBack(2.000f);
		  coord.pushBack(-1.633f);
		  coord.pushBack(-0.943f);
		  coord.pushBack(-0.667f);
		  coord.pushBack(0.000f);
		  coord.pushBack(1.886f);
		  coord.pushBack(-0.667f);
		   
		  VecInt coordIndex = new VecInt(20);
		  coordIndex.pushBack(0);
		  coordIndex.pushBack(1);
		  coordIndex.pushBack(2);
		  coordIndex.pushBack(-1);
		  coordIndex.pushBack(3);
		  coordIndex.pushBack(1);
		  coordIndex.pushBack(0);
		  coordIndex.pushBack(-1);
		  coordIndex.pushBack(2);
		  coordIndex.pushBack(1);
		  coordIndex.pushBack(3);
		  coordIndex.pushBack(-1);

		  return new PolygonMesh(coord, coordIndex);
	}
}
