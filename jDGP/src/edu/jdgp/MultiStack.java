package edu.jdgp;
package edu.jdgp;

import edu.jdgp.DGP.VecInt;
import edu.jdgp.DGP.VecBool;

/*
 * Implementación de un conjunto de VecInt
 * (la necesidad surgió en la implementación de Matsui para generar 
 * todos los árboles generadores de un grafo)
 * El sentido de esta clase es dar soporte a una función recursiva para 
 * no perder el control de la información que no puede almacenarse 
 * en la pila del proceso.
 * 1) La idea es que cada llamada recursiva L se registre en esta clase 
 *    y se le asigne un slotID 
 * 2) La clase se crea con una cantidad fija de VecInt por slot. Por 
 *    ejemplo, Matsui necesita 2 VecInt: uno para ejes pivot y otro para 
 *    ejes que pueden sustituirse por cada eje pivot
 * Referencia:
 * 		"SpanningTreesMatsui.pdf"
 * */
public class MultiVecInt {
	int _numVecsPerSlot;
	int _nextSlot;
	private VecInt[] _vecs;

	/*
	 * int numVecsPerSlot: cantidad de vectores por slot (ej. Matsui 
	 * 					requiere por los menos dos: ejes pivot y ejes 
	 *                  que pueden sustituirse por cada eje pivot)
	 * int initalNumSlots: cantidad inicial de slots
	 */ 
	public MultiVecInt(int numVecsPerSlot, int initalNumSlots) {
		_nextSlot = 0;
		_numVecsPerSlot = numVecsPerSlot;
		_vecs = new VecInt[numVecsPerSlot * initalNumSlots];
	}

	public int newSlot() {
		if (_nextSlot == _vecs.length) { // si se pasa del tamaño hay que redimensionar
			VecInt[] newVecs = new VecInt[_vecs.length * 2];
			for (int i = 0; i < _vecs.length; i++) {
				newVecs[i] = _vecs[i];
			}
			_vecs = newVecs;
		}
		return _nextSlot++;
	}
	
	
	public void setVec(int slotID, int vecID, VecInt vec) {
		_vecs[slotID * _numVecsPerSlot + vecID] = vec;
	}
	
	public static void main(String[] args) throws Exception {
	}

}
