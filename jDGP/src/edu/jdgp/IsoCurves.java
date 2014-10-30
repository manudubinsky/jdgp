package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:27:26 taubin>
//------------------------------------------------------------------------

import  java.util.*;
import mesh.*;
import wrl.*;
import img.*;
import panels.*;
import gui.*;

public class IsoCurves
{
  private WrlSceneGraph _wrl = null;

  public IsoCurves(WrlSceneGraph wrl) {
    _wrl = wrl;
  }

  private WrlIndexedFaceSet _getInputIndexedFaceSet() {
    WrlIndexedFaceSet ifs = null;
    if(_wrl!=null) {
      DgpNodes editor = new DgpNodes(_wrl);
      WrlShape shape = editor.getShape("SURFACE");      
      if(shape!=null) {
        WrlNode node = shape.getGeometry();
        if(node!=null && node instanceof WrlIndexedFaceSet)
          ifs = (WrlIndexedFaceSet)node;
      }
    }
    return ifs;
  }

  private WrlIndexedFaceSet _getOutputIndexedFaceSet() {
    WrlIndexedFaceSet ifs = null;
    if(_wrl!=null) {
      DgpNodes editor = new DgpNodes(_wrl);
      WrlShape shape = editor.getShape("OUTPUT-SURFACE");
      if(shape==null)
        shape = editor.addShape("OUTPUT-SURFACE",new WrlIndexedFaceSet());
      WrlNode node = shape.getGeometry();
      if(node!=null && node instanceof WrlIndexedFaceSet)
        ifs = (WrlIndexedFaceSet)node;
    }
    return ifs;
  }

  private static void _resetIndexedFaceSet(WrlIndexedFaceSet ifs) {

      // ASSIGNMENT 2

  }

  private static void _resetIndexedLineSet(WrlIndexedLineSet ils) {

      // ASSIGNMENT 2

  }

  private WrlIndexedLineSet _getOutputIndexedLineSet() {
    WrlIndexedLineSet ifs = null;
    if(_wrl!=null) {
      DgpNodes editor = new DgpNodes(_wrl);
      WrlShape shape = editor.getShape("OUTPUT-CURVE");
      if(shape==null)
        shape = editor.addShape("OUTPUT-CURVE",new WrlIndexedLineSet());
      WrlNode node = shape.getGeometry();
      if(node!=null && node instanceof WrlIndexedLineSet)
        ifs = (WrlIndexedLineSet)node;
    }
    return ifs;
  }

  public int getNumberOfVertices() {
    int nVertices = 0;
    WrlIndexedFaceSet ifs = _getInputIndexedFaceSet();
    if(ifs!=null) nVertices = ifs.getNumberOfCoord();
    return nVertices;
  }

  public float[] functionSetRandom() {
    float[] _vFunction = null;
    int nVertices = getNumberOfVertices();
    if(nVertices>0) {
      _vFunction = new float[nVertices];
      Random r = new Random();
      for(int iV=0;iV<nVertices;iV++)
        _vFunction[iV] = r.nextFloat();
    }
    return _vFunction;
  }

  public void functionSmooth
    (int smoothSteps, float smoothLambda, float smoothMu, float[] vFunction) {
    WrlIndexedFaceSet ifs = _getInputIndexedFaceSet();
    if(ifs==null) return;
    int nVertices = getNumberOfVertices();
    if(nVertices<=1) return;
    VecInt coordIndex = ifs.getCoordIndex();
    Graph g = new Graph(coordIndex,nVertices);
    int nE = g.getNumberOfEdges();

    // TODO Tue Sep 24 00:23:10 2013
    // we will add this function after we talk about smoothing in class

  }

  public void removeTexture() {
    boolean isTri = false;
    if(_wrl!=null) {

      // ASSIGNMENT 2

    }
  }

   private static void _appendIsoCurveVertices
     (WrlIndexedFaceSet ifs, float isoLevel,
      float[] vFunction, VecInt edgeToIsoCoord, VecFloat isoCoord) {

     if(ifs==null || vFunction==null ||
        vFunction.length!=ifs.getNumberOfCoord() ||
        edgeToIsoCoord==null || isoCoord==null) return;

     // ASSIGNMENT 2

   }

  public void isoCurveTexture(float isoLevel, float[] _vFunction) {

    if(_wrl!=null) {
      _wrl.setDone(false);
      WrlIndexedFaceSet ifsIn = _getInputIndexedFaceSet();
      if(ifsIn!=null) {
        WrlIndexedFaceSet ifsOut = _getOutputIndexedFaceSet();
        _resetIndexedFaceSet(ifsOut); 

        // ASSIGNMENT 2

      }
      _wrl.setHasChanged(true);
      _wrl.setDone(true);
    }
  }

  public void isoCurveCutFaces
    (float isoLevel, float[] vFunction, boolean selectCutEdges) {

    if(_wrl!=null) {
      _wrl.setDone(false);
      WrlIndexedFaceSet ifs = _getInputIndexedFaceSet();
      if(ifs!=null) {
        WrlIndexedFaceSet ifsOut = _getOutputIndexedFaceSet();
        _resetIndexedFaceSet(ifsOut);

        // ASSIGNMENT 2

      }
      _wrl.setHasChanged(true);
      _wrl.setDone(true);
    }
  }

  public void isoCurveLines(float isoLevel, float[] vFunction) {

    if(_wrl!=null) {
      _wrl.setDone(false);
      WrlIndexedFaceSet ifs = _getInputIndexedFaceSet();
      if(ifs!=null) {
        WrlIndexedLineSet ilsOut = _getOutputIndexedLineSet();
        _resetIndexedLineSet(ilsOut);
        
        // ASSIGNMENT 2
        
      }
      _wrl.setHasChanged(true);
      _wrl.setDone(true);
    }

  }

}

