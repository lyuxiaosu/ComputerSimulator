package com.test;

import java.util.*;
public class MAR extends AbstrctProcessor{
	public MAR(IUpdate subject) {
		this.bitset = new BitSet(16);
		this.subject = subject;
		this.value = 0;
		this.nbits = 16;
	}
	
	
	@Override
	protected Object doProcess(Object data) {
		if (data == null) {
			return null;
		}
		int i = (Integer) data;
		
		if (i == -1 || i == -2 || i == -3) {
			return i;
		}
		SetValue(i);
		return new Integer(i);
	}
}
