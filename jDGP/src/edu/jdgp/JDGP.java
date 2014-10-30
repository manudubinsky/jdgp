package edu.jdgp;

//------------------------------------------------------------------------
//  Copyright (C) 1993-2007 Gabriel Taubin
//  Time-stamp: <2013-12-10 18:06:50 taubin>
//------------------------------------------------------------------------

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import gui.*;
import wrl.*;

public class JDGP
  extends    J3DApp
  implements WindowListener
{
  private static boolean _debug = true;
  private static void _log(String s) {
    if(_debug) System.err.println("JDGP | "+s);
  }

  // defined in J3DApp
  // protected static String   _wrlDir        = null;
  // protected static String   _imgDir        = null;
  // protected static String   _filename      = null;

  //////////////////////////////////////////////////////////////////////
  public JDGP() {
    super();
    addWindowListener(this);

    setFont(new Font("Tahoma",Font.PLAIN,10));

    // menuBar = new J3DPGPMenu(this);
    menuBar = new J3DAppMenu(this);
    setMenuBar(menuBar);
      
    J3DCanvas  c1 =  getJ3DCanvas1();
    J3DCanvas  c2 =  getJ3DCanvas2();
    J3DCanvas  c3 =  getJ3DCanvas3();

    DropTarget dt1 = new DropTarget(c1,this);
    DropTarget dt2 = new DropTarget(c2,this);
    DropTarget dt3 = new DropTarget(c3,this);
  }

  // command line messages
  static String[] _usage = {
    "usage : JDGP [flags]",
    "            [-help]",
    "            [-d|-debug]",
    "            [-w|-width ] <width> ",
    "            [-h|-height] <height>",
  };

  public static void usage() {
    for(int i=0;i<_usage.length;i++)
      System.out.println(_usage[i]);
  }  

  //////////////////////////////////////////////////////////////////////
  public static void main(String[] args) {

    // default dimensions defined in J3DDesktop
    _frameWidth  = 900;
    _frameHeight = 610;

    // process command line 

    for(int i=0;i<args.length;i++) {
      if(args[i].equals("-help")) {
        usage();
        System.exit(0);
      } else if(args[i].equals("-d") ||
                args[i].equals("-debug")) {

      } else if(args[i].equals("-w") ||
                args[i].equals("-width")) {
        i++;
        if(i<args.length)
          _frameWidth = Integer.parseInt(args[i]);
        else
          error("no value after " + args[i-1]);
        if(_frameWidth<=0)
          error("_frameWidth = " + _frameWidth);
      } else if(args[i].equals("-h") ||
                args[i].equals("-height")) {
        i++;
        if(i<args.length)
          _frameHeight = Integer.parseInt(args[i]);
        else
          error("no value after " + args[i-1]);
        if(_frameHeight<=0)
          error("_frameHeight = " + _frameHeight);
      } else if(args[i].charAt(0)=='-') {
        error("unknown command line option"); 
      } else
        _filename= args[i];
    }

    String os = System.getProperty("os.name");
    _log("os = \""+os+"\"");

    // create the interactive application

    JDGP jdgp = new JDGP();
    jdgp.setTitle("JDGP | "+Version.getValue());
    jdgp.setVisible(true);
    jdgp.setSize(_frameWidth,_frameHeight);

    // add new panels
    
    jdgp.addDesktopPanel(new J3DPanelNodes(jdgp),"NODES");
    jdgp.addDesktopPanel(new J3DPanelDgp(jdgp),"DGP");
    jdgp.addDesktopPanel(new J3DPanelIsoCurves(jdgp),"ISOCURVES");

    // TODO Mon Sep 23 13:18:38 2013
    // white two seconds until the gui is created
    // otherwise it hungs; why?
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    // if a filename was specified in the command line, try to load it
    if(_filename!=null) {

      File file = new File(_filename);
      if(file.exists()==false)
        error("filename \""+_filename+"\" does not exist");
      else if(file.isFile()==false)
        error("filename \""+_filename+"\" exists but it is not a file");

      // remember the full pathname of the last file loaded
      _filename = file.getAbsolutePath();
      _log("filename = \""+_filename);
      _wrlDir = file.getParentFile().getAbsolutePath();
      _log("wrl_dir  = \""+_wrlDir+"\"");

      jdgp.load(_filename);
    }
  }

  //////////////////////////////////////////////////////////////////////
  // implements WindowListener

  public void windowActivated(WindowEvent e)   { }  
  public void windowClosed(WindowEvent e)      { }  
  public void windowClosing(WindowEvent e)     { quit(); }  
  public void windowDeactivated(WindowEvent e) { }  
  public void windowDeiconified(WindowEvent e) { }  
  public void windowIconified(WindowEvent e)   { }  
  public void windowOpened(WindowEvent e)      { }

}
