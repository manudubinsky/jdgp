package edu.jdgp;

import gui.J3DApp;
import gui.J3DApp.J3DAppMenu;

public class WrlTest extends J3DApp {
	
	public WrlTest(String file) {
		super();
	    menuBar = new J3DAppMenu(this);
	    setMenuBar(menuBar);
		load(file);
	}
	
	public static void main(String[] args) {
		WrlTest app = new WrlTest("D:\\workspace\\jDGP\\img\\bunny1r.wrl");
		app.setVisible(true);
	    try {
	        Thread.sleep(2000);
	    } catch (InterruptedException e) {
	        e.printStackTrace();
	    }

	}
}
