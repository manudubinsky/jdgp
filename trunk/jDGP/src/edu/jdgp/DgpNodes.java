package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-09-24 13:27:51 taubin>
//------------------------------------------------------------------------

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import mesh.*;
import wrl.*;

public class DgpNodes
{
  private static void _log(String s) {
    // System.err.println("DgpNodes | "+s);
  }

  private WrlSceneGraph _wrl      = null;

  public DgpNodes(WrlSceneGraph wrl) {
    _wrl = wrl;
  }

  public Vector<String> getNames() {
    Vector<String> names = null;
    if(_wrl!=null) {
      names = new Vector<String>();
      WrlSceneGraphTraversal traversal =
        new WrlSceneGraphTraversal(_wrl);
      String  name  = null;
      WrlNode child = null;
      while((child=traversal.getCurrentNode())!=null) {
        if((name=child.getName()) != null) names.add(name);
        traversal.advance();
      }
    }
    return names;
  }

  public WrlTransform getTransform(String name) {
    WrlTransform transform = null;
    if(_wrl!=null) {
      WrlSceneGraphTraversal traversal = new WrlSceneGraphTraversal(_wrl);
      WrlNode child = null;
      while((child=traversal.getCurrentNode())!=null) {
        if(child instanceof WrlTransform &&
           child.getName() != null &&
           child.getName().equals(name)) {
          transform = (WrlTransform)child;
          break;
        }
        traversal.advance();
      }
    }
    return transform;
  }

  // TOP transform must be the only child of _wrl
  public WrlTransform getTopTransform() {
    WrlTransform transform = null;
    if(_wrl!=null && _wrl.getNumberOfChildren()==1) {
      WrlNode child = _wrl.getChild(0);
      if(child instanceof WrlTransform &&
         child.getName() != null &&
         child.getName().equals("TOP")) {
        transform = (WrlTransform)child;
      }
    }
    return transform;
  }

  public boolean hasTopTransform() {
    return (getTopTransform()!=null);
  }

  public WrlTransform addTopTransform() {
    WrlTransform top = null;
    if(hasTopTransform()==false && _wrl!=null) {
      top = new WrlTransform();
      top.setName("TOP");
      _wrl.def("TOP",top);
      _wrl.insertParent(top);
      top.updateBBox();
    }
    return top;
  }

  public WrlShape getShape(String name) {
    WrlShape shape = null;
    if(_wrl!=null) {
      WrlSceneGraphTraversal traversal = new WrlSceneGraphTraversal(_wrl);
      WrlNode child = null;
      while((child=traversal.getCurrentNode())!=null) {
        if(child instanceof WrlShape &&
           child.getName() != null &&
           child.getName().equals(name)) {
          shape = (WrlShape)child;
          break;
        }
        traversal.advance();
      }
    }
    return shape;
  }

  public void showShape(String name, boolean value) {
    WrlShape shape = getShape(name);
    if(shape!=null) shape.setShow(value);
  }

  public boolean hasShape(String name) {
    return (getShape(name)!=null);
  }

  public WrlShape addShape(String name, WrlNode geometry) {
    WrlShape shape = null;

    if(hasShape(name)==false || geometry==null ||
       !((geometry instanceof WrlIndexedFaceSet) ||
        (geometry instanceof WrlIndexedLineSet))) {

      WrlTransform  top = getTopTransform();
      if(top==null) top = addTopTransform();
      top.updateBBox();

      // create a shape node
      shape = new WrlShape();
      shape.setName(name);
      _wrl.def(name,shape);

      // appearance
      WrlAppearance appearance = new WrlAppearance();
      WrlMaterial   material   = new WrlMaterial();
      appearance.setMaterial(material);
      material.setDiffuseColor(0.5f,0.25f,0.0f); // default color ???
      shape.setAppearance(appearance);

      // geometry
      shape.setGeometry(geometry);

      top.appendChild(shape);

      _wrl.makeSelection();
    }
    return shape;
  }

  public boolean updateBBox(float eps, boolean makeCube) {
    boolean success = false;
    if(hasShape("BOUNDING-BOX")==true && eps>-1.0f) {
      WrlShape          shape      = getShape("BOUNDING-BOX");
      WrlIndexedLineSet ils        = (WrlIndexedLineSet)shape.getGeometry();
      VecFloat          coord      = ils.getCoordValue();
      VecInt            coordIndex = ils.getCoordIndex();
      coord.erase();
      coordIndex.erase();

      // get bbox dimensions from the TOP transform
      WrlTransform  top = getTopTransform();
      top.updateBBox();
    
      // half side lengths of bbox
      float[] bbs  = top.getBboxSize();
      float hSide0 = (bbs[0]*(1.0f+eps))/2.0f;
      float hSide1 = (bbs[1]*(1.0f+eps))/2.0f;
      float hSide2 = (bbs[2]*(1.0f+eps))/2.0f;

      if(makeCube) {
        float max = hSide0;
        if(hSide1>max) max = hSide1;
        if(hSide2>max) max = hSide2;
        hSide0 = hSide1 = hSide2 = max;
      }
          
      float[] center = top.getBboxCenter();

      float   x0     = center[0]-hSide0;
      float   x1     = center[0]+hSide0;
      float   y0     = center[1]-hSide1;
      float   y1     = center[1]+hSide1;
      float   z0     = center[2]-hSide2;
      float   z1     = center[2]+hSide2;
    
      coord.pushBack(x0,y0,z0); // 0
      coord.pushBack(x0,y0,z1); // 1
      coord.pushBack(x0,y1,z0); // 2
      coord.pushBack(x0,y1,z1); // 3
      coord.pushBack(x1,y0,z0); // 4
      coord.pushBack(x1,y0,z1); // 5
      coord.pushBack(x1,y1,z0); // 6
      coord.pushBack(x1,y1,z1); // 7
      // z edges    
      coordIndex.pushBack(0,1,-1);
      coordIndex.pushBack(2,3,-1);
      coordIndex.pushBack(4,5,-1);
      coordIndex.pushBack(6,7,-1);
      // y edges
      coordIndex.pushBack(0,2,-1);
      coordIndex.pushBack(1,3,-1);
      coordIndex.pushBack(4,6,-1);
      coordIndex.pushBack(5,7,-1);
      // xedges
      coordIndex.pushBack(0,4,-1);
      coordIndex.pushBack(1,5,-1);
      coordIndex.pushBack(2,6,-1);
      coordIndex.pushBack(3,7,-1);
    
      _wrl.makeSelection();
      success = true;
    }
    return success;
  }

  // public WrlShape addBBox(float eps, boolean makeCube) {
  //   WrlShape shape = null;
  //   if(hasShape("BBOX")==false && eps>-1.0f) {
  //     shape = addShape("BOUNDING-BOX",new WrlIndexedLineSet());
  //     updateBBox(eps,makeCube);
  //   }
  //   return shape;
  // }
  //
  // public WrlShape addPoints() {
  //   WrlShape shape = null;
  //   if(hasShape("POINTS")==false)
  //     shape = addShape("POINTS",new WrlIndexedFaceSet());
  //   return shape;
  // }
  //
  // public void addSurface() {
  //   WrlShape shape = null;
  //   if(hasShape("SURFACE")==false)
  //     shape = addShape("SURFACE",new WrlIndexedFaceSet());
  // }
}
