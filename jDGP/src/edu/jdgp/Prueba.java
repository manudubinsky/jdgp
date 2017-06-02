package edu.jdgp;

public class Prueba {
	private int i;
	
	public int nextVal() {
		return i++;
	}

	public static void main(String[] args) {
		try {
			Prueba p = new Prueba();
			System.out.println(p.nextVal());
			System.out.println(p.nextVal());
		} catch (Exception e) {
			System.out.println(e);
		}
	}

}
