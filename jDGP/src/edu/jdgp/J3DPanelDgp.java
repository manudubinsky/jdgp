package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2008 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:38:10 taubin>
//------------------------------------------------------------------------

import java.awt.*;
import java.awt.event.*;
import wrl.*;
import panels.*;
import gui.*;

public class J3DPanelDgp
  extends    J3DPanel
  implements ItemListener,
             ActionListener,
             ComponentListener,
             MouseWheelListener
{
  private static boolean _debug = false;
  private static void _log(String s) {
    if(_debug) System.err.println("Assignment1 | "+s);
  }
  public static void setDebug(boolean value) { _debug = value; }

  // ASSIGNMENT 1
  // implement the DGP class and used it here

  private Label      _label_SELECT;

  private Checkbox   _checkbox_SELECT_NOTHING   = null;
  private Checkbox   _checkbox_SELECT_VIEWPOINT = null;
  private Checkbox   _checkbox_SELECT_VERTICES  = null;
  private Checkbox   _checkbox_SELECT_EDGES     = null;
  private Checkbox   _checkbox_SELECT_FACES     = null;
  private Checkbox   _checkbox_SELECT_SHAPES    = null;

  private Label      _label_CLEAR;
  private Label      _label_INVERT;
  private Label      _label_BOUNDARY;
  private Label      _label_REGULAR;
  private Label      _label_SINGULAR;
  private Label      _label_DILATE;
  private Label      _label_ERODE;
  private Label      _label_STEPS;
  private Label      _label_CONNECTED;
  private Label      _label_SHAPE;
  private Label      _label_DELETE;
  private Label      _label_CUT;
  private Label      _label_FROM_V_TO;
  private Label      _label_FROM_E_TO;
  private Label      _label_FROM_F_TO;

  private Button     _button_SELECT_VERTICES_CLEAR;
  private Button     _button_SELECT_VERTICES_INVERT;
  private Button     _button_SELECT_VERTICES_DELETE;
  private Button     _button_SELECT_VERTICES_BOUNDARY;
  private Button     _button_SELECT_VERTICES_REGULAR;
  private Button     _button_SELECT_VERTICES_SINGULAR;
  private Button     _button_SELECT_VERTICES_DILATE;
  private Button     _button_SELECT_VERTICES_ERODE;
  private Button     _button_SELECT_VERTICES_CONNECTED;
  private Button     _button_SELECT_VERTICES_SHAPE;

  private Button     _button_SELECT_EDGES_CLEAR;
  private Button     _button_SELECT_EDGES_INVERT;
  private Button     _button_SELECT_EDGES_DELETE;
  private Button     _button_SELECT_EDGES_BOUNDARY;
  private Button     _button_SELECT_EDGES_REGULAR;
  private Button     _button_SELECT_EDGES_SINGULAR;
  private Button     _button_SELECT_EDGES_DILATE;
  private Button     _button_SELECT_EDGES_ERODE;
  private Button     _button_SELECT_EDGES_CONNECTED;
  private Button     _button_SELECT_EDGES_SHAPE;
  private Button     _button_SELECT_EDGES_CUT;

  private Checkbox   _checkbox_DILATE_OVERWRITE = null;

  private Button     _button_SELECT_FACES_CLEAR;
  private Button     _button_SELECT_FACES_INVERT;
  private Button     _button_SELECT_FACES_DELETE;
  private Button     _button_SELECT_FACES_BOUNDARY;
  private Button     _button_SELECT_FACES_REGULAR;
  private Button     _button_SELECT_FACES_SINGULAR;
  private Button     _button_SELECT_FACES_DILATE;
  private Button     _button_SELECT_FACES_ERODE;
  private Button     _button_SELECT_FACES_CONNECTED;
  private Button     _button_SELECT_FACES_SHAPE;
  private Button     _button_SELECT_FACES_CUT;

  private Button     _button_SELECT_V2E;
  private Button     _button_SELECT_V2F;
  private Button     _button_SELECT_E2V;
  private Button     _button_SELECT_E2F;
  private Button     _button_SELECT_F2V;
  private Button     _button_SELECT_F2E;

  private int        _steps = 1;
  private TextField  _textField_STEPS;
  private Button     _button_STEPS_MORE;
  private Button     _button_STEPS_LESS;

  private TextField  _textField_SELECTED_V_INDEX;
  private Label      _label_SELECTED_V_INDEX;
  private Button     _button_SELECTED_V_INDEX_UP;
  private Button     _button_SELECTED_V_INDEX_DN;
  private Label      _label_SELECTED_V_COLOR;

  private TextField  _textField_SELECTED_E_INDEX;
  private Label      _label_SELECTED_E_INDEX;
  private Button     _button_SELECTED_E_INDEX_UP;
  private Button     _button_SELECTED_E_INDEX_DN;
  private Label      _label_SELECTED_E_COLOR;

  private TextField  _textField_SELECTED_F_INDEX;
  private Label      _label_SELECTED_F_INDEX;
  private Button     _button_SELECTED_F_INDEX_UP;
  private Button     _button_SELECTED_F_INDEX_DN;
  private Label      _label_SELECTED_F_COLOR;

  // USER INTERFACE -----------------------------------------------------

  public J3DPanelDgp(J3DDesktop desktop) {
    super(desktop);

    _label_SELECT = newLabel("SELECT",g080,g240,Label.CENTER,this);

    _checkbox_SELECT_NOTHING   =
      newCheckbox(" NOTHING",false,this,this);
    _checkbox_SELECT_VIEWPOINT =
      newCheckbox(" VIEWPOINT",false,this,this);
    _checkbox_SELECT_VERTICES  =
      newCheckbox(" VERTICES",false,this,this);
    _checkbox_SELECT_EDGES     =
      newCheckbox(" EDGES",false,this,this);
    _checkbox_SELECT_FACES     =
      newCheckbox(" FACES",false,this,this);
    _checkbox_SELECT_SHAPES    =
      newCheckbox(" SHAPES",false,this,this);

    _label_CLEAR = newLabel("CLEAR",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_CLEAR = newButton("V",this,this);
    _button_SELECT_EDGES_CLEAR = newButton("E",this,this);
    _button_SELECT_FACES_CLEAR = newButton("F",this,this);

    _label_INVERT = newLabel("INVERT",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_INVERT = newButton("V",this,this);
    _button_SELECT_EDGES_INVERT = newButton("E",this,this);
    _button_SELECT_FACES_INVERT = newButton("F",this,this);

    _label_BOUNDARY = newLabel("BOUNDARY",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_BOUNDARY = newButton("V",this,this);
    _button_SELECT_EDGES_BOUNDARY = newButton("E",this,this);
    _button_SELECT_FACES_BOUNDARY = newButton("F",this,this);

    _label_REGULAR = newLabel("REGULAR",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_REGULAR = newButton("V",this,this);
    _button_SELECT_EDGES_REGULAR = newButton("E",this,this);
    _button_SELECT_FACES_REGULAR = newButton("F",this,this);

    _label_SINGULAR = newLabel("SINGULAR",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_SINGULAR = newButton("V",this,this);
    _button_SELECT_EDGES_SINGULAR = newButton("E",this,this);
    _button_SELECT_FACES_SINGULAR = newButton("F",this,this);

    _checkbox_DILATE_OVERWRITE =
      newCheckbox(" DILATE OVERWRITE",false,this,this);

    _label_DILATE = newLabel("DILATE",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_DILATE = newButton("V",this,this);
    _button_SELECT_EDGES_DILATE = newButton("E",this,this);
    _button_SELECT_FACES_DILATE = newButton("F",this,this);

    _label_ERODE = newLabel("ERODE",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_ERODE = newButton("V",this,this);
    _button_SELECT_EDGES_ERODE = newButton("E",this,this);
    _button_SELECT_FACES_ERODE = newButton("F",this,this);

    _label_STEPS = newLabel("STEPS",g120,g240,Label.CENTER,this);
    _textField_STEPS = newTextField("",this,this);
    _button_STEPS_MORE = newButton("MORE",this,this);
    _button_STEPS_LESS = newButton("LESS",this,this);

    _label_CONNECTED = newLabel("CONNECTED",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_CONNECTED = newButton("V",this,this);
    _button_SELECT_EDGES_CONNECTED = newButton("E",this,this);
    _button_SELECT_FACES_CONNECTED = newButton("F",this,this);

    _label_SHAPE = newLabel("SHAPE",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_SHAPE = newButton("V",this,this);
    _button_SELECT_EDGES_SHAPE = newButton("E",this,this);
    _button_SELECT_FACES_SHAPE = newButton("F",this,this);

    _label_DELETE = newLabel("DELETE",g080,g240,Label.CENTER,this);
    _button_SELECT_VERTICES_DELETE = newButton("V",this,this);
    _button_SELECT_EDGES_DELETE = newButton("E",this,this);
    _button_SELECT_FACES_DELETE = newButton("F",this,this);

    _label_CUT = newLabel("CUT",g080,g240,Label.CENTER,this);
    _button_SELECT_EDGES_CUT = newButton("E",this,this);
    _button_SELECT_FACES_CUT = newButton("F",this,this);

    _label_FROM_V_TO = newLabel("FROM V TO",g080,g240,Label.CENTER,this);
    _button_SELECT_V2E = newButton("E",this,this);
    _button_SELECT_V2F = newButton("F",this,this);

    _label_FROM_E_TO = newLabel("FROM E TO",g080,g240,Label.CENTER,this);
    _button_SELECT_E2V = newButton("V",this,this);
    _button_SELECT_E2F = newButton("F",this,this);

    _label_FROM_F_TO = newLabel("FROM F TO",g080,g240,Label.CENTER,this);
    _button_SELECT_F2V = newButton("V",this,this);
    _button_SELECT_F2E = newButton("E",this,this);

    _label_SELECTED_V_INDEX =
      newLabel("SELECTED V INDEX",g080,g240,Label.CENTER,this);
    _textField_SELECTED_V_INDEX = newTextField("",this,this);
    _textField_SELECTED_V_INDEX.addMouseWheelListener(this);

    _button_SELECTED_V_INDEX_DN = newButton("DN",this,this);
    _button_SELECTED_V_INDEX_UP = newButton("UP",this,this);

    _label_SELECTED_V_COLOR =
      newLabel("",g080,g240,Label.CENTER,this);

    _label_SELECTED_E_INDEX =
      newLabel("SELECTED E INDEX",g080,g240,Label.CENTER,this);
    _textField_SELECTED_E_INDEX = newTextField("",this,this);
    _textField_SELECTED_E_INDEX.addMouseWheelListener(this);

    _button_SELECTED_E_INDEX_DN = newButton("DN",this,this);
    _button_SELECTED_E_INDEX_UP = newButton("UP",this,this);

    _label_SELECTED_E_COLOR =
      newLabel("",g080,g240,Label.CENTER,this);

    _label_SELECTED_F_INDEX =
      newLabel("SELECTED F INDEX",g080,g240,Label.CENTER,this);
    _textField_SELECTED_F_INDEX = newTextField("",this,this);
    _textField_SELECTED_F_INDEX.addMouseWheelListener(this);

    _button_SELECTED_F_INDEX_DN = newButton("DN",this,this);
    _button_SELECTED_F_INDEX_UP = newButton("UP",this,this);

    _label_SELECTED_F_COLOR =
      newLabel("",g080,g240,Label.CENTER,this);
  }

  //////////////////////////////////////////////////////////////////////
  private WrlSelection _getSelection() {
    WrlSceneGraph wrl = _desktop.getWrl();
    return (wrl!=null)?wrl.getSelection():null;
  }

  //////////////////////////////////////////////////////////////////////
  public void updateText() {
    WrlSelection s = _getSelection();

    _textField_STEPS.setText("  "+_steps);

    int iVsel = (s!=null)?s.getDefaultVertexIndex():0;
    int iEsel = (s!=null)?s.getDefaultEdgeIndex():0;
    int iFsel = (s!=null)?s.getDefaultFaceIndex():0;

    _textField_SELECTED_V_INDEX.setText("  "+iVsel);
    _textField_SELECTED_E_INDEX.setText("  "+iEsel);
    _textField_SELECTED_F_INDEX.setText("  "+iFsel);

    // get colors
    J3DCanvasColors jcc = _desktop.getJ3DCanvasColors();
    if(jcc!=null) {
      _label_SELECTED_V_COLOR.setBackground
        (new Color(jcc.getSelectedVertexColor(iVsel)));
      _label_SELECTED_E_COLOR.setBackground
        (new Color(jcc.getSelectedEdgeColor(iEsel)));
      _label_SELECTED_F_COLOR.setBackground
        (new Color(jcc.getSelectedFaceColor(iFsel)));
    }
  }

  //////////////////////////////////////////////////////////////////////
  public void updateState() {
    updateText();

    WrlSceneGraph wrl = _desktop.getWrl();
    boolean value = (wrl!=null);

    if(value && wrl.hasSelection()==false) {
      wrl.makeSelection();
      _checkbox_SELECT_NOTHING.setState(false);
      _checkbox_SELECT_VIEWPOINT.setState(false);
      _checkbox_SELECT_VERTICES.setState(false);
      _checkbox_SELECT_EDGES.setState(false);
      _checkbox_SELECT_FACES.setState(false);
      _checkbox_SELECT_SHAPES.setState(false);
    } else {
      _checkbox_SELECT_NOTHING.setState(_desktop.getPickNothing1());
      _checkbox_SELECT_VIEWPOINT.setState(_desktop.getPickViewpoint1());
      _checkbox_SELECT_VERTICES.setState(_desktop.getPickVertices1());
      _checkbox_SELECT_EDGES.setState(_desktop.getPickEdges1());
      _checkbox_SELECT_FACES.setState(_desktop.getPickFaces1());
      _checkbox_SELECT_SHAPES.setState(_desktop.getPickShapes1());
    }

    _button_SELECT_VERTICES_CLEAR.setEnabled(value);
    _button_SELECT_VERTICES_INVERT.setEnabled(value);
    _button_SELECT_VERTICES_DELETE.setEnabled(value);
    _button_SELECT_VERTICES_BOUNDARY.setEnabled(value);
    _button_SELECT_VERTICES_REGULAR.setEnabled(value);
    _button_SELECT_VERTICES_SINGULAR.setEnabled(value);
    _button_SELECT_VERTICES_DILATE.setEnabled(value);
    _button_SELECT_VERTICES_CONNECTED.setEnabled(value);
    _button_SELECT_VERTICES_ERODE.setEnabled(value);
    _button_SELECT_VERTICES_SHAPE.setEnabled(value);

    _button_SELECT_EDGES_CLEAR.setEnabled(value);
    _button_SELECT_EDGES_INVERT.setEnabled(value);
    _button_SELECT_EDGES_DELETE.setEnabled(value);
    _button_SELECT_EDGES_BOUNDARY.setEnabled(value);
    _button_SELECT_EDGES_REGULAR.setEnabled(value);
    _button_SELECT_EDGES_SINGULAR.setEnabled(value);
    _button_SELECT_EDGES_DILATE.setEnabled(value);
    _button_SELECT_EDGES_CONNECTED.setEnabled(value);
    _button_SELECT_EDGES_ERODE.setEnabled(value);
    _button_SELECT_EDGES_SHAPE.setEnabled(value);
    _button_SELECT_EDGES_CUT.setEnabled(value);

    _button_SELECT_FACES_CLEAR.setEnabled(value);
    _button_SELECT_FACES_INVERT.setEnabled(value);
    _button_SELECT_FACES_DELETE.setEnabled(value);
    _button_SELECT_FACES_BOUNDARY.setEnabled(value);
    _button_SELECT_FACES_REGULAR.setEnabled(value);
    _button_SELECT_FACES_SINGULAR.setEnabled(value);
    _button_SELECT_FACES_DILATE.setEnabled(value);
    _button_SELECT_FACES_ERODE.setEnabled(value);
    _button_SELECT_FACES_CONNECTED.setEnabled(value);
    _button_SELECT_FACES_SHAPE.setEnabled(value);
    _button_SELECT_FACES_CUT.setEnabled(value);

    _button_SELECT_V2E.setEnabled(value);
    _button_SELECT_V2F.setEnabled(value);
    _button_SELECT_E2V.setEnabled(value);
    _button_SELECT_E2F.setEnabled(value);
    _button_SELECT_F2V.setEnabled(value);
    _button_SELECT_F2E.setEnabled(value);

    _desktop.render();
  }

  //////////////////////////////////////////////////////////////////////
  // implements ItemListener
  public void itemStateChanged(ItemEvent e) {
    // String item = (String)e.getItem();
    Object src  = e.getSource();

    if(src==_checkbox_SELECT_NOTHING) {
      _desktop.setPickNothing1();
    } else if(src==_checkbox_SELECT_VIEWPOINT) {
      _desktop.setPickViewpoint1();
    } else if(src==_checkbox_SELECT_VERTICES) {
      _desktop.setPickVertices1();
    } else if(src==_checkbox_SELECT_EDGES) {
      _desktop.setPickEdges1();
    } else if(src==_checkbox_SELECT_FACES) {
      _desktop.setPickFaces1();
    } else if(src==_checkbox_SELECT_SHAPES) {
      _desktop.setPickShapes1();
    } else if(src==_checkbox_DILATE_OVERWRITE) {
      // nothing to do in this case
    }

    _desktop.updateState();
    _desktop.render();
  }

  //////////////////////////////////////////////////////////////////////
  // implements ActionListener

  public void actionPerformed(ActionEvent e) {

    Object        src = e.getSource();
    WrlSceneGraph wrl = _desktop.getWrl();

    if(wrl==null) return;

    WrlSelection  s    = wrl.getSelection();
    int           indx = 0;

    if(src==_button_SELECT_VERTICES_CLEAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_INVERT) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_DELETE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_BOUNDARY) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_REGULAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_SINGULAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_DILATE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_ERODE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_CONNECTED) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_VERTICES_SHAPE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_CLEAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_INVERT) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_DELETE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_BOUNDARY) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_REGULAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_SINGULAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_DILATE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_ERODE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_CONNECTED) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_CUT) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_EDGES_SHAPE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_CLEAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_INVERT) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_DELETE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_BOUNDARY) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_REGULAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_SINGULAR) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_DILATE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_ERODE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_CONNECTED) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_SHAPE) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_FACES_CUT) {

      // ASSIGNMENT 1

    } else if(src==_textField_STEPS) {
      _steps = _parseInt(_textField_STEPS.getText().trim(),1);
      if(_steps<1) _steps = 1;;
    } else if(src==_button_STEPS_MORE) {
      _steps++;
    } else if(src==_button_STEPS_LESS) {
      if(_steps>1) _steps--;
    } else if(src==_button_SELECT_V2E) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_V2F) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_E2V) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_E2F) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_F2V) {

      // ASSIGNMENT 1

    } else if(src==_button_SELECT_F2E) {

      // ASSIGNMENT 1

    } else if((indx=
               (src==_textField_SELECTED_V_INDEX)?0:
               (src==_button_SELECTED_V_INDEX_UP)?1:
               (src==_button_SELECTED_V_INDEX_DN)?-1:2)!=2) {

      if((indx+=_parseInt(_textField_SELECTED_V_INDEX.getText().trim(),0))<0)
        indx = 0;
      s.setDefaultVertexIndex(indx);

    } else if((indx=
               (src==_textField_SELECTED_E_INDEX)?0:
               (src==_button_SELECTED_E_INDEX_UP)?1:
               (src==_button_SELECTED_E_INDEX_DN)?-1:2)!=2) {

      if((indx+=_parseInt(_textField_SELECTED_E_INDEX.getText().trim(),0))<0)
        indx = 0;
      s.setDefaultEdgeIndex(indx);

    } else if((indx=
               (src==_textField_SELECTED_F_INDEX)?0:
               (src==_button_SELECTED_F_INDEX_UP)?1:
               (src==_button_SELECTED_F_INDEX_DN)?-1:2)!=2) {

      if((indx+=_parseInt(_textField_SELECTED_F_INDEX.getText().trim(),0))<0)
        indx = 0;
      s.setDefaultFaceIndex(indx);

    }

    _desktop.updateState();
    _desktop.render();
  }

  //////////////////////////////////////////////////////////////////////
  // IMPLEMENTS MouseWheelListener

  public void mouseWheelMoved(MouseWheelEvent e) {
    // Invoked when the mouse wheel is rotated.
    Object src     = e.getSource();
    int    nClicks = e.getWheelRotation();

    if(src==_textField_SELECTED_V_INDEX) {

      String text = _textField_SELECTED_V_INDEX.getText().trim();
      try {
        int indx = Integer.valueOf(text).intValue();

        if(nClicks<0)
          indx++;
        else
          indx--;
        if(indx<0) indx = 0;

        WrlSelection s = _getSelection();
        if(s!=null) s.setDefaultVertexIndex(indx);

        updateText();

      } catch(Exception ee) {
      }

    } else if(src==_textField_SELECTED_E_INDEX) {

      String text = _textField_SELECTED_E_INDEX.getText().trim();
      try {
        int indx = Integer.valueOf(text).intValue();

        if(nClicks<0)
          indx++;
        else
          indx--;
        if(indx<0) indx = 0;

        WrlSelection s = _getSelection();
        if(s!=null) s.setDefaultEdgeIndex(indx);

        updateText();

      } catch(Exception ee) {
      }

    } else if(src==_textField_SELECTED_F_INDEX) {

      String text = _textField_SELECTED_F_INDEX.getText().trim();
      try {
        int indx = Integer.valueOf(text).intValue();

        if(nClicks<0)
          indx++;
        else
          indx--;
        if(indx<0) indx = 0;

        WrlSelection s = _getSelection();
        if(s!=null) s.setDefaultFaceIndex(indx);

        updateText();

      } catch(Exception ee) {
      }

    }
  }

  //////////////////////////////////////////////////////////////////////
  // implements ComponentListener

  public void componentHidden(ComponentEvent ce)  { }
  public void componentMoved(ComponentEvent ce)   { }
  public void componentShown(ComponentEvent ce)   { updateText(); }
  public void componentResized(ComponentEvent ce) {
    _log("componentResized() {");
    _log("  "+ce.paramString());
    int width  = getWidth();
    int height = getHeight();
    _log("  width  = "+width);
    _log("  height = "+height);

    int rowHeight   = 18;
    int checkboxWidth = width-2*_borderWidth;

    int w1 = (width-7*_borderWidth)/6;
    int w2 = w1;
    int w3 = w1;
    int w4 = w1;
    int w5 = w4;
    int w0 = width-w1-w2-w3-w4-w5-7*_borderWidth;
    int w40 = (w4-_borderWidth)/2;
    int w41 = w4-_borderWidth-w40;
    int w012 = w0+_borderWidth+w1+_borderWidth+w2;

    int w01 = w0+_borderWidth+w1;
    int w23 = w2+_borderWidth+w3;
    int w45 = w4+_borderWidth+w5;

    int x0 = _borderWidth;
    int x1 = x0+w0+_borderWidth;
    int x2 = x1+w1+_borderWidth;
    int x3 = x2+w2+_borderWidth;
    int x4 = x3+w3+_borderWidth;
    int x5 = x4+w4+_borderWidth;
    int x40 = x4;
    int x41 = x40+w40+_borderWidth;

    int h   = rowHeight;
    int y = _borderWidth;

    _label_SELECT.setLocation(x0,y);
    _label_SELECT.setSize(width-2*_borderWidth,h);

    y += 5*(h+_rowSpace)/4;

    _checkbox_SELECT_NOTHING.setLocation(x0,y);
    _checkbox_SELECT_NOTHING.setSize(w01,h);
    _checkbox_SELECT_VIEWPOINT.setLocation(x2,y);
    _checkbox_SELECT_VIEWPOINT.setSize(w23,h);
    _checkbox_SELECT_SHAPES.setLocation(x4,y);
    _checkbox_SELECT_SHAPES.setSize(w45,h);

    y += h+_rowSpace;

    _checkbox_SELECT_VERTICES.setLocation(x0,y);
    _checkbox_SELECT_VERTICES.setSize(w01,h);
    _checkbox_SELECT_EDGES.setLocation(x2,y);
    _checkbox_SELECT_EDGES.setSize(w23,h);
    _checkbox_SELECT_FACES.setLocation(x4,y);
    _checkbox_SELECT_FACES.setSize(w45,h);

    y += 5*(h+_rowSpace)/4;

    _label_CLEAR.setLocation(x0,y);
    _label_CLEAR.setSize(w012,h);
    _button_SELECT_VERTICES_CLEAR.setLocation(x3,y);
    _button_SELECT_VERTICES_CLEAR.setSize(w3,h);
    _button_SELECT_EDGES_CLEAR.setLocation(x4,y);
    _button_SELECT_EDGES_CLEAR.setSize(w4,h);
    _button_SELECT_FACES_CLEAR.setLocation(x5,y);
    _button_SELECT_FACES_CLEAR.setSize(w5,h);

    y += h+_rowSpace;
    
    _label_INVERT.setLocation(x0,y);
    _label_INVERT.setSize(w012,h);
    _button_SELECT_VERTICES_INVERT.setLocation(x3,y);
    _button_SELECT_VERTICES_INVERT.setSize(w3,h);
    _button_SELECT_EDGES_INVERT.setLocation(x4,y);
    _button_SELECT_EDGES_INVERT.setSize(w4,h);
    _button_SELECT_FACES_INVERT.setLocation(x5,y);
    _button_SELECT_FACES_INVERT.setSize(w5,h);

    y += 3*(h+_rowSpace)/2;

    _label_BOUNDARY.setLocation(x0,y);
    _label_BOUNDARY.setSize(w012,h);
    _button_SELECT_VERTICES_BOUNDARY.setLocation(x3,y);
    _button_SELECT_VERTICES_BOUNDARY.setSize(w3,h);
    _button_SELECT_EDGES_BOUNDARY.setLocation(x4,y);
    _button_SELECT_EDGES_BOUNDARY.setSize(w4,h);
    _button_SELECT_FACES_BOUNDARY.setLocation(x5,y);
    _button_SELECT_FACES_BOUNDARY.setSize(w5,h);

    y += h+_rowSpace;

    _label_REGULAR.setLocation(x0,y);
    _label_REGULAR.setSize(w012,h);
    _button_SELECT_VERTICES_REGULAR.setLocation(x3,y);
    _button_SELECT_VERTICES_REGULAR.setSize(w3,h);
    _button_SELECT_EDGES_REGULAR.setLocation(x4,y);
    _button_SELECT_EDGES_REGULAR.setSize(w4,h);
    _button_SELECT_FACES_REGULAR.setLocation(x5,y);
    _button_SELECT_FACES_REGULAR.setSize(w5,h);

    y += h+_rowSpace;

    _label_SINGULAR.setLocation(x0,y);
    _label_SINGULAR.setSize(w012,h);
    _button_SELECT_VERTICES_SINGULAR.setLocation(x1,y);
    _button_SELECT_VERTICES_SINGULAR.setSize(w3,h);
    _button_SELECT_EDGES_SINGULAR.setLocation(x4,y);
    _button_SELECT_EDGES_SINGULAR.setSize(w4,h);
    _button_SELECT_FACES_SINGULAR.setLocation(x5,y);
    _button_SELECT_FACES_SINGULAR.setSize(w5,h);

    y += 5*(h+_rowSpace)/4;

    _checkbox_DILATE_OVERWRITE.setLocation(x0,y);
    _checkbox_DILATE_OVERWRITE.setSize(w012,h);

    y += h+_rowSpace;

    _label_DILATE.setLocation(x0,y);
    _label_DILATE.setSize(w012,h);
    _button_SELECT_VERTICES_DILATE.setLocation(x3,y);
    _button_SELECT_VERTICES_DILATE.setSize(w3,h);
    _button_SELECT_EDGES_DILATE.setLocation(x4,y);
    _button_SELECT_EDGES_DILATE.setSize(w4,h);
    _button_SELECT_FACES_DILATE.setLocation(x5,y);
    _button_SELECT_FACES_DILATE.setSize(w5,h);

    y += h+_rowSpace;

    _label_ERODE.setLocation(x0,y);
    _label_ERODE.setSize(w012,h);
    _button_SELECT_VERTICES_ERODE.setLocation(x3,y);
    _button_SELECT_VERTICES_ERODE.setSize(w3,h);
    _button_SELECT_EDGES_ERODE.setLocation(x4,y);
    _button_SELECT_EDGES_ERODE.setSize(w4,h);
    _button_SELECT_FACES_ERODE.setLocation(x5,y);
    _button_SELECT_FACES_ERODE.setSize(w5,h);

    y += h+_rowSpace;

    _label_STEPS.setLocation(x0,y);
    _label_STEPS.setSize(w012,h);
    _textField_STEPS.setLocation(x3,y);
    _textField_STEPS.setSize(w3,h);
    _button_STEPS_MORE.setLocation(x4,y);
    _button_STEPS_MORE.setSize(w4,h);
    _button_STEPS_LESS.setLocation(x5,y);
    _button_STEPS_LESS.setSize(w5,h);

    y += 5*(h+_rowSpace)/4;

    _label_CONNECTED.setLocation(x0,y);
    _label_CONNECTED.setSize(w012,h);
    _button_SELECT_VERTICES_CONNECTED.setLocation(x3,y);
    _button_SELECT_VERTICES_CONNECTED.setSize(w3,h);
    _button_SELECT_EDGES_CONNECTED.setLocation(x4,y);
    _button_SELECT_EDGES_CONNECTED.setSize(w4,h);
    _button_SELECT_FACES_CONNECTED.setLocation(x5,y);
    _button_SELECT_FACES_CONNECTED.setSize(w5,h);

    y += h+_rowSpace;

    _label_SHAPE.setLocation(x0,y);
    _label_SHAPE.setSize(w012,h);
    _button_SELECT_VERTICES_SHAPE.setLocation(x3,y);
    _button_SELECT_VERTICES_SHAPE.setSize(w3,h);
    _button_SELECT_EDGES_SHAPE.setLocation(x4,y);
    _button_SELECT_EDGES_SHAPE.setSize(w4,h);
    _button_SELECT_FACES_SHAPE.setLocation(x5,y);
    _button_SELECT_FACES_SHAPE.setSize(w5,h);

    y += 5*(h+_rowSpace)/4;

    _label_DELETE.setLocation(x0,y);
    _label_DELETE.setSize(w012,h);
    _button_SELECT_VERTICES_DELETE.setLocation(x0,y);
    _button_SELECT_VERTICES_DELETE.setSize(w012,h);
    _button_SELECT_EDGES_DELETE.setLocation(x4,y);
    _button_SELECT_EDGES_DELETE.setSize(w4,h);
    _button_SELECT_FACES_DELETE.setLocation(x5,y);
    _button_SELECT_FACES_DELETE.setSize(w5,h);

    y += h+_rowSpace;

    _label_CUT.setLocation(x0,y);
    _label_CUT.setSize(w012,h);
    _button_SELECT_EDGES_CUT.setLocation(x4,y);
    _button_SELECT_EDGES_CUT.setSize(w1,h);
    _button_SELECT_FACES_CUT.setLocation(x5,y);
    _button_SELECT_FACES_CUT.setSize(w4,h);

    y += 5*(h+_rowSpace)/4;

    _label_FROM_V_TO.setLocation(x0,y);
    _label_FROM_V_TO.setSize(w012,h);
    _button_SELECT_V2E.setLocation(x4,y);
    _button_SELECT_V2E.setSize(w4,h);
    _button_SELECT_V2F.setLocation(x5,y);
    _button_SELECT_V2F.setSize(w5,h);

    y += h+_rowSpace;

    _label_FROM_E_TO.setLocation(x0,y);
    _label_FROM_E_TO.setSize(w012,h);
    _button_SELECT_E2V.setLocation(x3,y);
    _button_SELECT_E2V.setSize(w3,h);
    _button_SELECT_E2F.setLocation(x5,y);
    _button_SELECT_E2F.setSize(w5,h);

    y += h+_rowSpace;

    _label_FROM_F_TO.setLocation(x0,y);
    _label_FROM_F_TO.setSize(w012,h);
    _button_SELECT_F2V.setLocation(x3,y);
    _button_SELECT_F2V.setSize(w3,h);
    _button_SELECT_F2E.setLocation(x4,y);
    _button_SELECT_F2E.setSize(w4,h);

    y += 5*(h+_rowSpace)/4;

    _label_SELECTED_V_INDEX.setLocation(x0,y);
    _label_SELECTED_V_INDEX.setSize(w012,h);
    _textField_SELECTED_V_INDEX.setLocation(x3,y);
    _textField_SELECTED_V_INDEX.setSize(w3,h);
    _button_SELECTED_V_INDEX_DN.setLocation(x40,y);
    _button_SELECTED_V_INDEX_DN.setSize(w40,h);
    _button_SELECTED_V_INDEX_UP.setLocation(x41,y);
    _button_SELECTED_V_INDEX_UP.setSize(w41,h);
    _label_SELECTED_V_COLOR.setLocation(x5,y);
    _label_SELECTED_V_COLOR.setSize(w5,h);

    y += h+_rowSpace;

    _label_SELECTED_E_INDEX.setLocation(x0,y);
    _label_SELECTED_E_INDEX.setSize(w012,h);
    _textField_SELECTED_E_INDEX.setLocation(x3,y);
    _textField_SELECTED_E_INDEX.setSize(w3,h);
    _button_SELECTED_E_INDEX_DN.setLocation(x40,y);
    _button_SELECTED_E_INDEX_DN.setSize(w40,h);
    _button_SELECTED_E_INDEX_UP.setLocation(x41,y);
    _button_SELECTED_E_INDEX_UP.setSize(w41,h);
    _label_SELECTED_E_COLOR.setLocation(x5,y);
    _label_SELECTED_E_COLOR.setSize(w5,h);

    y += h+_rowSpace;

    _label_SELECTED_F_INDEX.setLocation(x0,y);
    _label_SELECTED_F_INDEX.setSize(w012,h);
    _textField_SELECTED_F_INDEX.setLocation(x3,y);
    _textField_SELECTED_F_INDEX.setSize(w3,h);
    _button_SELECTED_F_INDEX_DN.setLocation(x40,y);
    _button_SELECTED_F_INDEX_DN.setSize(w40,h);
    _button_SELECTED_F_INDEX_UP.setLocation(x41,y);
    _button_SELECTED_F_INDEX_UP.setSize(w41,h);
    _label_SELECTED_F_COLOR.setLocation(x5,y);
    _label_SELECTED_F_COLOR.setSize(w5,h);

    _log("}");
  }
}
