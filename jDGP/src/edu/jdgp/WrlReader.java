package edu.jdgp;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
//import mesh.VecFloat;
import wrl.WrlReaderWrl;
import wrl.WrlSceneGraph;
import wrl.WrlIndexedFaceSet;
import wrl.WrlNode;
import edu.jdgp.DGP.PolygonMesh;
import edu.jdgp.DGP.VecFloat;
import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.Graph;

public class WrlReader {
	private PolygonMesh _mesh;
	
	public WrlReader(String file) throws Exception {
		BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
		WrlSceneGraph wrl = new WrlSceneGraph();
		wrl.setDone(false);
		WrlReaderWrl localWrlReaderWrl = new WrlReaderWrl(wrl, (InputStream)is);
		if (localWrlReaderWrl != null) {
			localWrlReaderWrl.start();
			wrl.waitUntilDone();
			is.close();
			WrlIndexedFaceSet faceSet =_getIndexedFaceSet(wrl);
			_mesh = new PolygonMesh(VecFloat.fromWrlVecFloat(faceSet.getCoordValue()), 
									VecInt.fromWrlVecInt(faceSet.getCoordIndex()));
		}
	}
	
	private WrlIndexedFaceSet _getIndexedFaceSet(WrlSceneGraph wrl) {
		WrlIndexedFaceSet faceSet = null;
		java.util.Vector<WrlNode> localVector2 = new java.util.Vector<WrlNode>();
		localVector2.addElement(wrl);
		
		int i;
		while ((i = localVector2.size()) > 0) {
			wrl.WrlParent localWrlParent = (wrl.WrlParent)localVector2.remove(i - 1);
			wrl.WrlNode[] arrayOfWrlNode;
			if ((arrayOfWrlNode = localWrlParent.getChildren()) != null) {
				for (int j = 0; j < arrayOfWrlNode.length; j++) {
					wrl.WrlNode localWrlNode = arrayOfWrlNode[j];
					if ((localWrlNode instanceof wrl.WrlParent)) {
						localVector2.addElement((wrl.WrlParent)localWrlNode);
					} else if ((localWrlNode instanceof wrl.WrlShape))	{
						wrl.WrlShape localWrlShape = (wrl.WrlShape)localWrlNode;
						localWrlNode = localWrlShape.getGeometry();
						if ((localWrlNode instanceof WrlIndexedFaceSet)) {
							faceSet = (WrlIndexedFaceSet)localWrlNode;
						}
					}
				}
			}
		}  
		return faceSet;
	}

	public PolygonMesh getMesh() {
		return _mesh;
	}
	
	public static void main(String[] args) throws Exception {
		WrlReader reader = new WrlReader("/home/manuel/doctorado/jdgp/jDGP/img/bunny1r.wrl");
		Graph g = reader.getMesh().getGraph();
		System.out.println(" nV: " + g.getNumberOfVertices() + 
		                   " nE: " + g.getNumberOfEdges());
	}
}
