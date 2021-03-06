package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:27:37 taubin>
//------------------------------------------------------------------------

import edu.jdgp.DGP.PolygonMesh;
import gui.J3DDesktop;

import java.awt.Button;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import mesh.MeshFaces;
import mesh.VecFloat;
import mesh.VecInt;
import panels.J3DPanel;
import wrl.WrlIndexedFaceSet;
import wrl.WrlNode;
import wrl.WrlSceneGraph;
import wrl.WrlSelection;

public class J3DPanelFlattening
  extends    J3DPanel
  implements ActionListener,
             ComponentListener
{
  private static void _log(String s) {
    System.err.println("J3DPanelFlattening | "+s);
  }

  private int        _algorithmIter             = 10;
  private int        _step1Iter                 = 1;
  private int        _step2Iter                 = 1;
  private float      _step1Lambda               = 0.1f;
  private float      _step2Lambda               = 0.1f;
  
  private Label      _label_FLATTENING          = null;
  private Label      _label_ALGORITHM_ITER      = null;
  private Label      _label_STEP1_ITER          = null;
  private Label      _label_STEP1_LAMBDA        = null;
  private Label      _label_STEP2_ITER          = null;
  private Label      _label_STEP2_LAMBDA        = null;
  
  private TextField  _textField_ALGORITHM_ITER  = null;
  private TextField  _textField_STEP1_ITER      = null;
  private TextField  _textField_STEP1_LAMBDA    = null;
  private TextField  _textField_STEP2_ITER      = null;
  private TextField  _textField_STEP2_LAMBDA    = null;

  private Button     _button_EXECUTE            = null;
  private Button     _button_SAVE_SELECTION     = null;
  private Button     _button_MAX_MIN_NORM       = null;
  private Button     _button_SCALE              = null;
  private Button     _button_START_EXECUTE      = null;
  private Button     _button_STOP_EXECUTE            = null;
  private Float max;
  private Float min;
  private boolean controlFlag;

  public J3DPanelFlattening(J3DDesktop desktop) {
    super(desktop);

    _label_FLATTENING     = newLabel("SURFACE FLATTENING",g080,g240,Label.CENTER,this);
    _label_ALGORITHM_ITER = newLabel("Iterations",g120,g240,Label.CENTER,this);
    _label_STEP1_ITER     = newLabel("Step 1 Iterations",g120,g240,Label.CENTER,this);
    _label_STEP1_LAMBDA   = newLabel("Step 1 Lambda",g120,g240,Label.CENTER,this);
    _label_STEP2_ITER     = newLabel("Step 2 Iterations",g120,g240,Label.CENTER,this);
    _label_STEP2_LAMBDA   = newLabel("Step 2 Lambda",g120,g240,Label.CENTER,this);

    _textField_ALGORITHM_ITER  = newTextField("",this,this);
    _textField_STEP1_ITER      = newTextField("",this,this);
    _textField_STEP1_LAMBDA    = newTextField("",this,this);
    _textField_STEP2_ITER      = newTextField("",this,this);
    _textField_STEP2_LAMBDA    = newTextField("",this,this);

    _button_EXECUTE = newButton("EXECUTE",this,this);
    _button_START_EXECUTE = newButton("START EXECUTE",this,this);
    _button_STOP_EXECUTE = newButton("STOP EXECUTE",this,this);
    _button_SAVE_SELECTION = newButton("SAVE SELECTION",this,this);
    _button_MAX_MIN_NORM = newButton("MAX MIN NORM",this,this);
    _button_SCALE = newButton("SCALE",this,this);
    
    updateText();
  }

  public void updateText() {
    _textField_ALGORITHM_ITER.setText("  "+_algorithmIter);
    _textField_STEP1_ITER.setText("  "+_step1Iter);
    _textField_STEP1_LAMBDA.setText("  "+_step1Lambda);
    _textField_STEP2_ITER.setText("  "+_step2Iter);
    _textField_STEP2_LAMBDA.setText("  "+_step2Lambda);
  }
  
  private WrlIndexedFaceSet _getIndexedFaceSet() {
	  WrlIndexedFaceSet faceSet = null;
		java.util.Vector<WrlNode> localVector2 = new java.util.Vector<WrlNode>();
		localVector2.addElement(_desktop.getWrl());
		
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
  
  private void _execute() {
		WrlIndexedFaceSet faceSet = _getIndexedFaceSet();
		if (faceSet != null) {
			WrlSurfaceFlattener flattener = new WrlSurfaceFlattener(_step1Iter,
					_step2Iter,
					_algorithmIter,
					_step1Lambda,
					_step2Lambda);
			try {			
				flattener.flatten(faceSet);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
  }

  /*
VecInt       getVertexIndex();
VecInt       getEdgeIndex();
VecInt       getFaceIndex();
VecInt       getPolylineIndex();
int          getNumberOfVertices();
int          getNumberOfEdges();
int          getNumberOfFaces();
int          getNumberOfPolylines();
int          getNumberOfShapes(); 
   */
  private void _saveSelection() {
	  WrlSceneGraph wrl = _desktop.getWrl();
	  if (wrl.hasSelection()) {
		  WrlSelection sel = wrl.getSelection();
		  WrlIndexedFaceSet faceSet = _getIndexedFaceSet();
		 /*
		  System.out.println("#vertices: " + sel.getNumberOfVertices() +
			  					" #edges: " + sel.getNumberOfEdges() +
								" #faces: " + sel.getNumberOfFaces() +
								" #polylines: " + sel.getNumberOfPolylines() +
								" #shapes: " + sel.getNumberOfShapes());
 		 */
		  int[] selectedFaces = sel.getSelectedFaces();
		  MeshFaces faces = faceSet.getFaces();
		  DGP.VecInt selectedVertices = new DGP.VecInt(sel.getNumberOfVertices(),0);
		  // System.out.println("ACA!!!" + selectedVertices.size());
		  for (int i = 0; i < selectedFaces.length; i++) {
			  //System.out.println(i);
			  if (selectedFaces[i] == 0) {
				  int faceLen = faces.getNumberOfFaceIndices(i);
				  for (int j = 0; j < faceLen; j++) {
					  selectedVertices.set(faces.getFaceCoordIndex(i, j), 1);
				  }
				  
			  }
		  }
		  // selectedVertices.dump();
		  VecFloat coord = faceSet.getCoordValue();
		  System.out.println("coord Coordinate { point [");
		  DGP.VecInt remapIndex = new DGP.VecInt(sel.getNumberOfVertices(), 0);
		  int totalSelectedVertices = 0;
		  for (int i = 0; i < selectedVertices.size(); i++) {
			  if (selectedVertices.get(i) == 1) {
				  System.out.println(coord.get(3 * i) + " " + 
						  					coord.get(3 * i + 1) + " " + 
						  					coord.get(3 * i + 2));
				  // System.out.println("ACA: " + totalSelectedVertices + " " + i);
				  remapIndex.set(i,totalSelectedVertices);
				  totalSelectedVertices++;
			  }
		  }
		  System.out.println("] }");
		  System.out.println("coordIndex [");
		  for (int i = 0; i < selectedFaces.length; i++) {
			  if (selectedFaces[i] == 0) {
				  int faceLen = faces.getNumberOfFaceIndices(i);
				  StringBuffer s = new StringBuffer();
				  // System.out.println(i + " " + faceLen);
				  for (int j = 0; j < faceLen; j++) {
					  s.append(remapIndex.get(faces.getFaceCoordIndex(i, j)) + " ");
				  }
				  System.out.println(s + "-1");
			  }
		  }
		  System.out.println("]");
		  
	  }
  }
  
  private void _calculateMaxMinNorm() {
	  WrlSceneGraph wrl = _desktop.getWrl();
	  if (wrl.hasSelection()) {
		  WrlIndexedFaceSet faceSet = _getIndexedFaceSet();
		  try {
			PolygonMesh mesh = new PolygonMesh(DGP.VecFloat.fromWrlVecFloat(faceSet.getCoordValue()), DGP.VecInt.fromWrlVecInt(faceSet.getCoordIndex()));
			DGP.VecFloat norms = PolygonMeshHelper.edgesNorms(mesh);
			int maxIndex = -1;
			int minIndex = -1;
			max = null;
			min = null;
			for (int i = 0; i < norms.size(); i++) {
				if (max == null || max < norms.get(i)) {
					max = norms.get(i);
					maxIndex = i;
				}
				if (min == null || min > norms.get(i)) {
					min = norms.get(i);
					minIndex = i;
				}				
			}
			System.out.println("min index: " + minIndex +
								" min norm: " + min + 
								" max index: " + maxIndex + 
								" max norm: " + max);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }	  
  }

  private void _executeContinuosly() {
	  	
		WrlIndexedFaceSet faceSet = _getIndexedFaceSet();
		if (faceSet != null) {
				controlFlag = true;
				new Thread()
				{
				    public void run() {
						WrlIndexedFaceSet faceSet = _getIndexedFaceSet();
						WrlSurfaceFlattener flattener = new WrlSurfaceFlattener(_step1Iter,
								_step2Iter,
								_algorithmIter,
								_step1Lambda,
								_step2Lambda);

						try {
							while (controlFlag) {
								flattener.flatten(faceSet);
						          _desktop.updateState();
						          _desktop.render();								
							    try {
							        Thread.sleep(1);
							      } catch (InterruptedException e) {
							        // e.printStackTrace();
							      }
							}
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}						
				    }
				}.start();				
		}
  }

  private void _scale() {
	  WrlSceneGraph wrl = _desktop.getWrl();
	  if (wrl.hasSelection()) {
		  WrlIndexedFaceSet faceSet = _getIndexedFaceSet();
		  _calculateMaxMinNorm();
		  float scaleFactor = 1/(min*10);
		  VecFloat coords = faceSet.getCoordValue();
		  for (int i = 0; i < coords.size(); i++) {
			  float value = coords.get(i);
			  coords.set(i,scaleFactor * value);
		  }
	  }	  
  }
  
  // implements ActionListener
  public void actionPerformed(ActionEvent e)
  {
    Object src = e.getSource();
    WrlSceneGraph wrl = _desktop.getWrl();
    if(wrl!=null) {
      if(src==_button_EXECUTE) {
    	  _algorithmIter = _parseInt(_textField_ALGORITHM_ITER.getText().trim(),100);
    	  _step1Iter = _parseInt(_textField_STEP1_ITER.getText().trim(),10);
    	  _step2Iter = _parseInt(_textField_STEP2_ITER.getText().trim(),10);
    	  _step1Lambda = _parseFloat(_textField_STEP1_LAMBDA.getText().trim(),0.01f);
    	  _step2Lambda = _parseFloat(_textField_STEP2_LAMBDA.getText().trim(),0.01f);
    	  /*
    	  _log("_step1Iter: " + _step1Iter + 
    			  " _step2Iter: " + _step2Iter + 
    			  " _algorithmIter: " + _algorithmIter + 
    			  " _step1Lambda: " + _step1Lambda +
    			  " _step2Lambda: " + _step2Lambda);
    		*/
    	  _execute();
          _desktop.updateState();
          _desktop.render();
      } else if(src==_button_SAVE_SELECTION) {
    	  _saveSelection();
      } else if(src==_button_MAX_MIN_NORM) {
    	  _calculateMaxMinNorm();
      }  else if(src==_button_SCALE) {
    	  _scale();
          _desktop.updateState();
          _desktop.render();
      } else if(src==_button_START_EXECUTE) {
    	  _algorithmIter = _parseInt(_textField_ALGORITHM_ITER.getText().trim(),100);
    	  _step1Iter = _parseInt(_textField_STEP1_ITER.getText().trim(),10);
    	  _step2Iter = _parseInt(_textField_STEP2_ITER.getText().trim(),10);
    	  _step1Lambda = _parseFloat(_textField_STEP1_LAMBDA.getText().trim(),0.01f);
    	  _step2Lambda = _parseFloat(_textField_STEP2_LAMBDA.getText().trim(),0.01f);

    	  _executeContinuosly();
      } else if(src==_button_STOP_EXECUTE) {
    	  controlFlag = false;
      }
    }
  }  

  //////////////////////////////////////////////////////////////////////
  // implements ComponentListener
  public void componentHidden(ComponentEvent ce)  { }
  public void componentShown(ComponentEvent ce)   { updateState(); }
  public void componentMoved(ComponentEvent ce)   { }
  public void componentResized(ComponentEvent ce) {
    _resize();
  }
  private void _resize() {
    int width  = getWidth();
    // int height = getHeight();

    int rowHeight   = 20;
    int b           = _borderWidth;

    int w0          = (width-5*_borderWidth)/4;
    int w1          = w0;
    // int w2          = w1;
    // int w3          = width-5*b-w0-w1-w2;;

    int x0          = b;
    int x1          = x0+w0+b;
    // int x2          = x1+w1+b;
    // int x3          = x2+w2+b;

    int y           = b;
    
    _label_FLATTENING.setLocation(x0,y);
    _label_FLATTENING.setSize(w0+b+w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_ALGORITHM_ITER.setLocation(x0,y);
    _label_ALGORITHM_ITER.setSize(w0,rowHeight);
    _textField_ALGORITHM_ITER.setLocation(x1,y);
    _textField_ALGORITHM_ITER.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_STEP1_ITER.setLocation(x0,y);
    _label_STEP1_ITER.setSize(w0,rowHeight);
    _textField_STEP1_ITER.setLocation(x1,y);
    _textField_STEP1_ITER.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_STEP1_LAMBDA.setLocation(x0,y);
    _label_STEP1_LAMBDA.setSize(w0,rowHeight);
    _textField_STEP1_LAMBDA.setLocation(x1,y);
    _textField_STEP1_LAMBDA.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_STEP2_ITER.setLocation(x0,y);
    _label_STEP2_ITER.setSize(w0,rowHeight);
    _textField_STEP2_ITER.setLocation(x1,y);
    _textField_STEP2_ITER.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_STEP2_LAMBDA.setLocation(x0,y);
    _label_STEP2_LAMBDA.setSize(w0,rowHeight);
    _textField_STEP2_LAMBDA.setLocation(x1,y);
    _textField_STEP2_LAMBDA.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;
          
    _button_EXECUTE.setLocation(x0,y);
    _button_EXECUTE.setSize(w0,rowHeight);

    y += rowHeight+_rowSpace;

    _button_SAVE_SELECTION.setLocation(x0,y);
    _button_SAVE_SELECTION.setSize(w0,rowHeight);

    y += rowHeight+_rowSpace;

    _button_MAX_MIN_NORM.setLocation(x0,y);
    _button_MAX_MIN_NORM.setSize(w0,rowHeight);

    y += rowHeight+_rowSpace;

    _button_SCALE.setLocation(x0,y);
    _button_SCALE.setSize(w0,rowHeight);

    y += rowHeight+_rowSpace;

    _button_START_EXECUTE.setLocation(x0,y);
    _button_START_EXECUTE.setSize(w0,rowHeight);

    y += rowHeight+_rowSpace;

    _button_STOP_EXECUTE.setLocation(x0,y);
    _button_STOP_EXECUTE.setSize(w0,rowHeight);

    y += rowHeight+_rowSpace;

  }
}
