package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-23 23:46:09 taubin>
//------------------------------------------------------------------------

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.*;
import mesh.*;
import wrl.*;
import img.*;
import panels.*;
import gui.*;

public class J3DPanelIsoCurves
  extends    J3DPanel
  implements ItemListener,
             ActionListener,
             ComponentListener
{
  private boolean    _debug                        = false;

  private enum Operation {
    ISOCURVE_NONE,
    ISOCURVE_TEXTURE,
    ISOCURVE_LINES,
    ISOCURVE_CUTFACES
  };

  private Operation  _lastOperation                = Operation.ISOCURVE_NONE;

  private float      _isoLevel                     = 0.5f;
  private float      _isoLevelIncrement            = 0.01f;
  private float[]    _vFunction                    = null;
  private float      _vFunctionMin                 = 0.5f;
  private float      _vFunctionMax                 = 0.5f;
  private int        _smoothSteps                  = 5;
  private float      _smoothLambda                 = 0.5f;

  private Label      _label_ISOLEVEL               = null;
  private Label      _label_ISOLEVEL_VALUE         = null;
  private TextField  _textField_ISOLEVEL_VALUE     = null;
  private Label      _label_ISOLEVEL_INCREMENT     = null;
  private TextField  _textField_ISOLEVEL_INCREMENT = null;
  private Button     _button_ISOLEVEL_UP           = null;
  private Button     _button_ISOLEVEL_DOWN         = null;

  private Label      _label_ISOCURVE               = null;
  private Button     _button_ISOCURVE_TEXTURE      = null;
  private Button     _button_ISOCURVE_LINES        = null;
  private Button     _button_ISOCURVE_CUTFACES     = null;
  private Checkbox   _checkbox_SELECT_CUT_EDGES    = null;

  private Label      _label_TEXTURE                = null;
  private Button     _button_REMOVE_TEXTURE        = null;

  private Label      _label_VERTEX_FUNCTION        = null;
  private Label      _label_FUNCTION_MIN           = null;
  private TextField  _textField_FUNCTION_MIN       = null;
  private Label      _label_FUNCTION_MAX           = null;
  private TextField  _textField_FUNCTION_MAX       = null;
  private Button     _button_FUNCTION_RANDOM       = null;

  private Label      _label_SMOOTH_STEPS           = null;
  private TextField  _textField_SMOOTH_STEPS       = null;
  private Label      _label_SMOOTH_LAMBDA          = null;
  private TextField  _textField_SMOOTH_LAMBDA      = null;
  private Button     _button_FUNCTION_SMOOTH       = null;

  // USER INTERFACE -------------------------------------------------------

  public J3DPanelIsoCurves(J3DDesktop desktop) {
    super(desktop);

    _label_ISOLEVEL =
      newLabel("ISO-LEVEL",g080,g240,Label.CENTER,this);

    _label_ISOLEVEL_VALUE =
      newLabel("VALUE",g120,g240,Label.CENTER,this);
    _textField_ISOLEVEL_VALUE =
      newTextField("",this,this);

    _label_ISOLEVEL_INCREMENT =
      newLabel("INCREMENT",g120,g240,Label.CENTER,this);
    _textField_ISOLEVEL_INCREMENT =
      newTextField("",this,this);

    _button_ISOLEVEL_UP =
      newButton("UP",this,this);
    _button_ISOLEVEL_DOWN =
      newButton("DOWN",this,this);

    _label_ISOCURVE =
      newLabel("ISOCURVE",g080,g240,Label.CENTER,this);
    
    _button_ISOCURVE_TEXTURE =
      newButton("TEXTURE",this,this);
    _button_ISOCURVE_TEXTURE.setEnabled(false);
      
    _button_ISOCURVE_LINES =
      newButton("LINES",this,this);
    _button_ISOCURVE_LINES.setEnabled(false);
      
    _button_ISOCURVE_CUTFACES =
      newButton("CUT FACES",this,this);
    _button_ISOCURVE_CUTFACES.setEnabled(false);

    _checkbox_SELECT_CUT_EDGES =
        newCheckbox("SELECT CUT EDGES",true,this,this);

    _label_TEXTURE =
      newLabel("TEXTURE",g080,g240,Label.CENTER,this);

    _button_REMOVE_TEXTURE =
      newButton("REMOVE",this,this);
    _button_REMOVE_TEXTURE.setEnabled(false);

    _label_VERTEX_FUNCTION =
      newLabel("VERTEX FUNCTION",g080,g240,Label.CENTER,this);

    _label_FUNCTION_MIN =
      newLabel("MIN",g120,g240,Label.CENTER,this);
    _textField_FUNCTION_MIN =
      newTextField("",this,this);
    _textField_FUNCTION_MIN.setEditable(false);

    _label_FUNCTION_MAX =
      newLabel("MAX",g120,g240,Label.CENTER,this);
    _textField_FUNCTION_MAX =
      newTextField("",this,this);
    _textField_FUNCTION_MAX.setEditable(false);

    _button_FUNCTION_RANDOM =
      newButton("RANDOM",this,this);
    _button_FUNCTION_RANDOM.setEnabled(false);

    _label_SMOOTH_STEPS =
      newLabel("STEPS",g120,g240,Label.CENTER,this);
    _textField_SMOOTH_STEPS =
      newTextField("",this,this);

    _label_SMOOTH_LAMBDA =
      newLabel("LAMBDA",g120,g240,Label.CENTER,this);
    _textField_SMOOTH_LAMBDA =
          newTextField("",this,this);

    _button_FUNCTION_SMOOTH =
      newButton("SMOOTH",this,this);
    _button_FUNCTION_SMOOTH.setEnabled(false);

    updateText();
  }

  //////////////////////////////////////////////////////////////////////
  public void updateText() {

    _textField_ISOLEVEL_VALUE.setText
      (String.format("%8.4f",_isoLevel));
    _textField_ISOLEVEL_INCREMENT.setText
      (String.format("%8.4f",_isoLevelIncrement));

    if(_vFunction==null || _vFunction.length<=0) {
      _vFunctionMin = _isoLevel;
      _vFunctionMax = _isoLevel;
    } else {
      float min = _vFunction[0];
      float max = _vFunction[0];
      for(int i=1;i<_vFunction.length;i++) {
        if(_vFunction[i]<min) min = _vFunction[i];
        if(_vFunction[i]>max) max = _vFunction[i];
      }
      _vFunctionMin = min;
      _vFunctionMax = max;
    }

    _textField_FUNCTION_MIN.setText
      (String.format("%8.4f",_vFunctionMin));
    _textField_FUNCTION_MAX.setText
      (String.format("%8.4f",_vFunctionMax));

    _textField_SMOOTH_STEPS.setText
      (String.format("%4d",_smoothSteps));
    _textField_SMOOTH_LAMBDA.setText
      (String.format("%8.4f",_smoothLambda));
  }

  //////////////////////////////////////////////////////////////////////
  public void updateState() {
    updateText();
    WrlSceneGraph wrl = _desktop.getWrl();
    if(wrl==null) {
      _button_FUNCTION_RANDOM.setEnabled(false);
      _button_FUNCTION_SMOOTH.setEnabled(false);
      _button_REMOVE_TEXTURE.setEnabled(false);
      _button_ISOCURVE_LINES.setEnabled(false);
      _button_ISOCURVE_TEXTURE.setEnabled(false);
      _button_ISOCURVE_CUTFACES.setEnabled(false);
    } else {
      WrlSelection s = wrl.getSelection();
      if(s!=null) {
        int          nV                = s.getNumberOfVertices();
        boolean      hasVertexFunction = (_vFunction!=null && _vFunction.length==nV);
        boolean      isTriMesh         = wrl.isTriangleMesh(); 
        boolean      isTextured        = wrl.isTextured();
        
        _button_FUNCTION_RANDOM.setEnabled(true);

        _button_FUNCTION_SMOOTH.setEnabled(hasVertexFunction);
        
        _button_REMOVE_TEXTURE.setEnabled(isTextured);
        
        boolean isocurveTextureEnabled =
          hasVertexFunction&&isTriMesh&&(!isTextured);
        _button_ISOCURVE_TEXTURE.setEnabled(isocurveTextureEnabled);
        
        boolean isocurveLinesEnabled =
          hasVertexFunction&&isTriMesh;
        _button_ISOCURVE_LINES.setEnabled(isocurveLinesEnabled);
        
        boolean isocurveCutFacesEnabled =
          hasVertexFunction&&isTriMesh;
        _button_ISOCURVE_CUTFACES.setEnabled(isocurveCutFacesEnabled);
        
        _desktop.render();
      }
    }
  } 

  //////////////////////////////////////////////////////////////////////
  // implements ItemListener
  public void itemStateChanged(ItemEvent e)
  {
    String item = (String)e.getItem();
    Object src  = e.getSource();
    
    // if(src==_checkbox_STEPS_SHOW) {
    // ???
    // }

    _desktop.render();
  }  

  //////////////////////////////////////////////////////////////////////
  // implements ActionListener
  public void actionPerformed(ActionEvent e)
  {
    Object        src            = e.getSource();
    String        cmd            = e.getActionCommand();
    WrlSceneGraph wrl            = _desktop.getWrl();
    boolean       tryToRecompute = false;

    if(src==_textField_ISOLEVEL_VALUE) {

       String text = _textField_ISOLEVEL_VALUE.getText().trim();
       // float isoLevel = _isoLevel; 
       // try {
       //   isoLevel = Float.valueOf(text).floatValue();
       // } catch(Exception ee) {
       // }
       _isoLevel = _parseFloat(text,0.0f); 
       tryToRecompute = true;

    } else if(src==_textField_ISOLEVEL_INCREMENT) {

       String text = _textField_ISOLEVEL_INCREMENT.getText().trim();
       // float isoLevelIncrement = _isoLevelIncrement; 
       // try {
       //   isoLevelIncrement = Float.valueOf(text).floatValue();
       //   if(isoLevelIncrement<0) isoLevelIncrement = -isoLevelIncrement;
       // } catch(Exception ee) {
       // }
       _isoLevelIncrement = _parseFloat(text,0.0f); 
       if(_isoLevelIncrement<0) _isoLevelIncrement = -_isoLevelIncrement;

    } else if(src==_button_ISOLEVEL_UP) {

      _isoLevel += _isoLevelIncrement;
      tryToRecompute = true;
      
    } else if(src==_button_ISOLEVEL_DOWN) {

      _isoLevel -= _isoLevelIncrement;      
       tryToRecompute = true;

    } else if(src==_button_FUNCTION_RANDOM) {

      IsoCurves ic = new IsoCurves(wrl);
      _vFunction = ic.functionSetRandom();
      tryToRecompute = true;

    } else if(src==_textField_SMOOTH_STEPS) {

       String text = _textField_SMOOTH_STEPS.getText().trim();
       // int smoothSteps = _smoothSteps; 
       // try {
       //   smoothSteps = Integer.valueOf(text).intValue();
       //   if(smoothSteps<1) smoothSteps = 1;
       // } catch(Exception ee) {
       // }
       // _smoothSteps = smoothSteps;
       _smoothSteps = _parseInt(text,1);
       if(_smoothSteps<1) _smoothSteps = 1;


    } else if(src==_textField_SMOOTH_LAMBDA) {

       String text = _textField_SMOOTH_LAMBDA.getText().trim();
       // float smoothLambda = _smoothLambda; 
       // try {
       //   smoothLambda = Float.valueOf(text).floatValue();
       // } catch(Exception ee) {
       // }
       // _smoothLambda = smoothLambda;
       _smoothLambda = _parseFloat(text,0.0f);

    } else if(src==_button_FUNCTION_SMOOTH) {

      IsoCurves ic = new IsoCurves(wrl);
      ic.functionSmooth(_smoothSteps,_smoothLambda,_smoothLambda,_vFunction);
      tryToRecompute = true;

    } else if(src==_button_REMOVE_TEXTURE) {

      // called only if wrl is textured 
      IsoCurves ic = new IsoCurves(wrl);
      ic.removeTexture();

      _desktop.replaceWrl(wrl);
      _desktop.setWarningStatus(wrl.getHasChanged());

      _lastOperation = Operation.ISOCURVE_NONE;

    } else if(src==_button_ISOCURVE_TEXTURE) {

      // called only if wrl is triangle mesh and not textured 
      IsoCurves ic = new IsoCurves(wrl);
      ic.isoCurveTexture(_isoLevel,_vFunction);
      _desktop.replaceWrl(wrl);
      _desktop.setWarningStatus(wrl.getHasChanged());

      _lastOperation = Operation.ISOCURVE_TEXTURE;

    } else if(src==_button_ISOCURVE_CUTFACES) {

      // called only if wrl is triangle mesh
      IsoCurves ic = new IsoCurves(wrl);
      ic.isoCurveCutFaces
        (_isoLevel,_vFunction,_checkbox_SELECT_CUT_EDGES.getState());
      _desktop.replaceWrl(wrl);
      _desktop.setWarningStatus(wrl.getHasChanged());

      _lastOperation = Operation.ISOCURVE_CUTFACES;

    } else if(src==_button_ISOCURVE_LINES) {

      // called only if wrl is triangle mesh
      IsoCurves ic = new IsoCurves(wrl);
      ic.isoCurveLines(_isoLevel,_vFunction);
      _desktop.replaceWrl(wrl);
      _desktop.setWarningStatus(wrl.getHasChanged());

      _lastOperation = Operation.ISOCURVE_LINES;

    }

    if(tryToRecompute) {

      switch(_lastOperation) {

      case ISOCURVE_TEXTURE:
        {
          IsoCurves ic = new IsoCurves(wrl);
          ic.removeTexture();
          ic.isoCurveTexture(_isoLevel,_vFunction);
        }
        break;

      case ISOCURVE_CUTFACES:
        break;

      case ISOCURVE_LINES:
        break;

      case ISOCURVE_NONE:
      default:
        break;
      }

    }

    updateState();
    _desktop.render();
  }  

  //////////////////////////////////////////////////////////////////////
  // implements ComponentListener

  public void componentHidden(ComponentEvent ce)  { }
  public void componentMoved(ComponentEvent ce)   { }
  public void componentShown(ComponentEvent ce)   { updateText(); }
  public void componentResized(ComponentEvent ce) {
    int width  = getWidth();
    int height = getHeight();

    int b   = _borderWidth;
    int h   = 18; // rowHeight;

    int w1 = (width-5*b)/4;
    int w2 = w1;
    int w3 = w1;
    int w0 = width-w1-w2-w3-5*b;

    int x0 = b;
    int x1 = x0+w0+b;
    int x2 = x1+w1+b;
    int x3 = x2+w2+b;

    // second column

    int y   = b;

    // y += 5*(h+_rowSpace)/4;

    _label_ISOLEVEL.setLocation(x0,y);
    _label_ISOLEVEL.setSize(w0+b+w1,h);

    y += h+_rowSpace;

    _label_ISOLEVEL_VALUE.setLocation(x0,y);
    _label_ISOLEVEL_VALUE.setSize(w0,h);
    _textField_ISOLEVEL_VALUE.setLocation(x1,y);
    _textField_ISOLEVEL_VALUE.setSize(w1,h);

    y += h+_rowSpace;

    _label_ISOLEVEL_INCREMENT.setLocation(x0,y);
    _label_ISOLEVEL_INCREMENT.setSize(w0,h);
    _textField_ISOLEVEL_INCREMENT.setLocation(x1,y);
    _textField_ISOLEVEL_INCREMENT.setSize(w1,h);

    y += h+_rowSpace;

    _button_ISOLEVEL_UP.setLocation(x0,y);
    _button_ISOLEVEL_UP.setSize(w0,h);
    _button_ISOLEVEL_DOWN.setLocation(x1,y);
    _button_ISOLEVEL_DOWN.setSize(w1,h);

    y += h+_rowSpace;

    _label_ISOCURVE.setLocation(x0,y);
    _label_ISOCURVE.setSize(w0+b+w1,h);

    y += h+_rowSpace;

    _button_ISOCURVE_TEXTURE.setLocation(x0,y);
    _button_ISOCURVE_TEXTURE.setSize(w0,h);
    _button_ISOCURVE_LINES.setLocation(x1,y);
    _button_ISOCURVE_LINES.setSize(w1,h);

    y += h+_rowSpace;

    _button_ISOCURVE_CUTFACES.setLocation(x0,y);
    _button_ISOCURVE_CUTFACES.setSize(w0,h);
    _checkbox_SELECT_CUT_EDGES.setLocation(x1,y);
    _checkbox_SELECT_CUT_EDGES.setSize(w1,h);

    y += h+_rowSpace;

    _label_TEXTURE.setLocation(x0,y);
    _label_TEXTURE.setSize(w0+b+w1,h);

    y += h+_rowSpace;

    _button_REMOVE_TEXTURE.setLocation(x0,y);
    _button_REMOVE_TEXTURE.setSize(w0,h);


    // second column

    y   = b;

    _label_VERTEX_FUNCTION.setLocation(x2,y);
    _label_VERTEX_FUNCTION.setSize(w2+b+w3,h);

    y += h+_rowSpace;

    _label_FUNCTION_MIN.setLocation(x2,y);
    _label_FUNCTION_MIN.setSize(w2,h);
    _textField_FUNCTION_MIN.setLocation(x3,y);
    _textField_FUNCTION_MIN.setSize(w3,h);

    y += h+_rowSpace;

    _label_FUNCTION_MAX.setLocation(x2,y);
    _label_FUNCTION_MAX.setSize(w2,h);
    _textField_FUNCTION_MAX.setLocation(x3,y);
    _textField_FUNCTION_MAX.setSize(w3,h);

    y += h+_rowSpace;

    _button_FUNCTION_RANDOM.setLocation(x2,y);
    _button_FUNCTION_RANDOM.setSize(w2,h);

    y += h+_rowSpace;

    _label_SMOOTH_STEPS.setLocation(x2,y);
    _label_SMOOTH_STEPS.setSize(w2,h);
    _textField_SMOOTH_STEPS.setLocation(x3,y);
    _textField_SMOOTH_STEPS.setSize(w3,h);

    y += h+_rowSpace;

    _label_SMOOTH_LAMBDA.setLocation(x2,y);
    _label_SMOOTH_LAMBDA.setSize(w2,h);
    _textField_SMOOTH_LAMBDA.setLocation(x3,y);
    _textField_SMOOTH_LAMBDA.setSize(w3,h);

    y += h+_rowSpace;

    _button_FUNCTION_SMOOTH.setLocation(x2,y);
    _button_FUNCTION_SMOOTH.setSize(w3,h);


  }
}
