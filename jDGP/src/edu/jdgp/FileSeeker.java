package edu.jdgp;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import edu.jdgp.DGP.VecInt;


public class FileSeeker {
	BufferedReader _f;
	String _fileName;
	int[] _lineNo;
	int _lineNoIndex;
	int _currentLine;
	
	public FileSeeker(int[] lineNo, String fileName) {
		_fileName = fileName;
		_lineNo = lineNo;
	}
	
	public boolean hasItem() {
		boolean hasItem = false;
		try {
			if (_f == null) {				
				_f = new BufferedReader(new FileReader(_fileName));
				_currentLine = 1;
			}
			
			if (_lineNoIndex < _lineNo.length) {
				hasItem = true;
			} else {
				_f.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return hasItem;
	}
	
	public Item nextItem() {
		Item item = null;
		try {
			while (_currentLine < _lineNo[_lineNoIndex]) {
					_f.readLine();
					_currentLine++;
			}
			String s = _f.readLine();
			if (s != null) 
				item = new Item(s);
			
			_currentLine++;
			_lineNoIndex++;			
			
			//System.out.println(item);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return item;
	}

}
