package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:27:37 taubin>
//------------------------------------------------------------------------

import gui.J3DDesktop;

import java.awt.Button;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import panels.J3DPanel;
import wrl.WrlCoordinate;
import wrl.WrlIndexedFaceSet;
import wrl.WrlNode;
import wrl.WrlSceneGraph;

public class J3DPanelFlattening
  extends    J3DPanel
  implements ActionListener,
             ComponentListener
{
  private static void _log(String s) {
    System.err.println("J3DPanelFlattening | "+s);
  }

  private int        _algorithmIter             = 100;
  private int        _step1Iter                 = 10;
  private int        _step2Iter                 = 10;
  private float      _step1Lambda               = 0.01f;
  private float      _step2Lambda               = 0.01f;
  
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

  private Button           _button_EXECUTE       = null;

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

    updateText();
  }

  public void updateText() {
    _textField_ALGORITHM_ITER.setText("  "+_algorithmIter);
    _textField_STEP1_ITER.setText("  "+_step1Iter);
    _textField_STEP1_LAMBDA.setText("  "+_step1Lambda);
    _textField_STEP2_ITER.setText("  "+_step2Iter);
    _textField_STEP2_LAMBDA.setText("  "+_step2Lambda);
  }
  
  private void _execute() {
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
							WrlIndexedFaceSet faceSet = (WrlIndexedFaceSet)localWrlNode;
							WrlSurfaceFlattener flattener = new WrlSurfaceFlattener(_step1Iter,
																					_step2Iter,
																					_algorithmIter,
																					_step1Lambda,
																					_step2Lambda);
							try {
								//flattener.flatten(faceSet);
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
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
    	  _log("_step1Iter: " + _step1Iter + 
    			  " _step2Iter: " + _step2Iter + 
    			  " _algorithmIter: " + _algorithmIter + 
    			  " _step1Lambda: " + _step1Lambda +
    			  " _step2Lambda: " + _step2Lambda);
          _desktop.updateState();
          _desktop.render();
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

  }
}
