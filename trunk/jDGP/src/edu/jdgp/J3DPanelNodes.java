package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:27:37 taubin>
//------------------------------------------------------------------------

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import gui.*;
import mesh.*;
import panels.*;
import wrl.*;

public class J3DPanelNodes
  extends    J3DPanel
  implements ItemListener,
             ActionListener,
             ComponentListener
{
  private static void _log(String s) {
    System.err.println("J3DPanelNodes | "+s);
  }

  private float            _epsBBox               = 0.05f;
  private Label            _label_BBOX            = null;
  private Label            _label_BBOX_EPS        = null;
  private TextField        _textField_BBOX_EPS    = null;
  private Checkbox         _checkbox_BBOX_CUBE    = null;
  private Button           _button_BBOX_ADD       = null;
  private Label            _label_SHOW            = null;
  private Vector<String>   _checkbox_NAME         = null;
  private Vector<Checkbox> _checkbox_SHOW         = null;

  public J3DPanelNodes(J3DDesktop desktop) {
    super(desktop);

    _label_BBOX =
      newLabel("BOUNDING BOX",g080,g240,Label.CENTER,this);
    _label_BBOX_EPS =
      newLabel("EPS",g120,g240,Label.CENTER,this);
    _textField_BBOX_EPS =
      newTextField("",this,this);
    _button_BBOX_ADD = newButton("ADD/UPDATE",this,this);
    _checkbox_BBOX_CUBE =
      newCheckbox(" CUBE",false,this,this);

    _label_SHOW =
      newLabel("SHOW",g080,g240,Label.CENTER,this);
    _checkbox_SHOW = new Vector<Checkbox>();

    updateText();
  }

  private void _updateBBox() {
    WrlSceneGraph wrl = _desktop.getWrl();
    if(wrl!=null) {
      DgpNodes editor = new DgpNodes(wrl);
      if(editor.hasShape("BOUNDING-BOX")==false)
        editor.addShape("BOUNDING-BOX",new WrlIndexedLineSet());
      editor.updateBBox(_epsBBox,_checkbox_BBOX_CUBE.getState());
    }
  }

  // implements ItemListener
  public void itemStateChanged(ItemEvent e) {
    Object src  = e.getSource();
    if(src instanceof Checkbox) {

      Checkbox         cb     = (Checkbox)src;
      boolean          state  = cb.getState();
      WrlSceneGraph    wrl    = _desktop.getWrl();
      DgpNodes editor = new DgpNodes(wrl);

      if(src==_checkbox_BBOX_CUBE) {
        _updateBBox();
      } else {
        WrlNode node = wrl.use(cb.getLabel());
        node.setShow(state);
      }

    }
    _desktop.updateState();
    _desktop.render();
  }  

  public void updateText() {
    _textField_BBOX_EPS.setText("  "+_epsBBox);
  }

  private void _clearCheckboxShow() {
    for(int i=0;i<_checkbox_SHOW.size();i++)
      remove(_checkbox_SHOW.get(i));
    _checkbox_SHOW.clear();
  }
  private void _updateCheckboxeShow() {
    WrlSceneGraph    wrl    = _desktop.getWrl();
    DgpNodes editor = new DgpNodes(wrl);
    _checkbox_NAME = editor.getNames();
    boolean match = (_checkbox_NAME.size()==_checkbox_SHOW.size());
    int i;
    String label,name;
    for(i=0;match && i<_checkbox_SHOW.size();i++) {
      label = _checkbox_SHOW.get(i).getLabel();
      name  = _checkbox_NAME.get(i);
      match = (label.equals(name)==false);
    }
    if(match) return;
    _clearCheckboxShow();
    WrlNode node;
    for(i=0;i<_checkbox_NAME.size();i++) {
      name = _checkbox_NAME.get(i);
      node = wrl.use(name);
      if(node!=null)
        _checkbox_SHOW.add(newCheckbox(name,node.getShow(),this,this));
    }
    _resize();
  }

  public void updateState() {
    updateText();

    WrlSceneGraph wrl = _desktop.getWrl();
    if(wrl==null) {

      _checkbox_BBOX_CUBE.setEnabled(false);
      _button_BBOX_ADD.setEnabled(false);

    } else {
      
      DgpNodes editor = new DgpNodes(wrl);

      _checkbox_BBOX_CUBE.setEnabled(true);
      _button_BBOX_ADD.setEnabled(true);

      boolean state;
      _updateCheckboxeShow();
      for(int i=0;i<_checkbox_SHOW.size();i++) {
        state = _checkbox_SHOW.get(i).getState();
        editor.showShape(_checkbox_NAME.get(i),state);        
      }
      
      _desktop.render();
    }
  } 

  // implements ActionListener
  public void actionPerformed(ActionEvent e)
  {
    Object src = e.getSource();
    // String cmd = e.getActionCommand();
    WrlSceneGraph wrl = _desktop.getWrl();
    if(wrl!=null) {
      DgpNodes editor = new DgpNodes(wrl);

      if(src==_button_BBOX_ADD) {
        _updateBBox();
      } else if(src==_textField_BBOX_EPS) {
        float eps = _parseFloat(_textField_BBOX_EPS.getText().trim(),0.0f);
        if(eps<0.0f) eps = 0.0f;
        _epsBBox = eps;
        _updateBBox();
   // } else if(src==_button_ADD_POINTS) {
   //   editor.addShape("POINTS",new WrlIndexedFaceSet());
   // } else if(src==_button_ADD_SURFACE) {
   //   editor.addShape("SURFACE",new WrlIndexedFaceSet());
      }

      _desktop.updateState();
      _desktop.render();
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
    int height = getHeight();

    int rowHeight   = 20;
    int b           = _borderWidth;

    int w0          = (width-5*_borderWidth)/4;
    int w1          = w0;
    int w2          = w1;
    int w3          = width-5*b-w0-w1-w2;;

    int x0          = b;
    int x1          = x0+w0+b;
    int x2          = x1+w1+b;
    int x3          = x2+w2+b;

    int y           = b;
    
    _label_BBOX.setLocation(x0,y);
    _label_BBOX.setSize(w0+b+w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_BBOX_EPS.setLocation(x0,y);
    _label_BBOX_EPS.setSize(w0,rowHeight);
    _textField_BBOX_EPS.setLocation(x1,y);
    _textField_BBOX_EPS.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;

    _button_BBOX_ADD.setLocation(x0,y);
    _button_BBOX_ADD.setSize(w0,rowHeight);
    _checkbox_BBOX_CUBE.setLocation(x1,y);
    _checkbox_BBOX_CUBE.setSize(w1,rowHeight);

    y += rowHeight+_rowSpace;

    _label_SHOW.setLocation(x0,y);
    _label_SHOW.setSize(w0+b+w1,rowHeight);
    for(int i=0;i<_checkbox_SHOW.size();i++) {
      y += rowHeight+_rowSpace;
      _checkbox_SHOW.get(i).setLocation(x0,y);
      _checkbox_SHOW.get(i).setSize(w0+b+w1,rowHeight);
    }
  }
}
