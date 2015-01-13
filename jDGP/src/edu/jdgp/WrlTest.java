package edu.jdgp;

import gui.J3DApp;
import mesh.VecFloat;
import wrl.WrlIndexedFaceSet;
import wrl.WrlNode;

public class WrlTest extends J3DApp {
	
	public WrlTest(String file) {
		super();
	    setSize(900,610);

	    menuBar = new J3DAppMenu(this);
	    setMenuBar(menuBar);
		load(file);
	}
	
	public void change() {
		java.util.Vector<WrlNode> localVector2 = new java.util.Vector<WrlNode>();
		localVector2.addElement(this._wrl);
		
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
							WrlIndexedFaceSet faceSet = (WrlIndexedFaceSet)localWrlNode;
							VecFloat coord = faceSet.getCoordValue();
							coord.set(0, 0.2f);
							coord.set(1, -0.2f);
							coord.set(2, 0f);
							// refresh();
							/*
							Float max = null;
							Float min = null;
							for (int k = 0; k < coord.size(); k++) {
								float value = coord.get(k);
								if (max == null || max.floatValue() < value)
									max = value;
								if (min == null || min.floatValue() > value)
									min = value;

								System.out.println(k + " " + coord.get(k));
							}
							System.out.println("min: " + min + " max: " + max);
							 */
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		WrlTest app = new WrlTest("img\\piramide flatten.wrl");
		app.setVisible(true);
	    try {
	        Thread.sleep(2000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }
	    // app.change();
	}
}
