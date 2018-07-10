package com.test;

import java.util.*;
public class IR extends AbstrctProcessor {
	public IR(IUpdate subject) {
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
		SetValue(i);
		String message = "Fetch instruction " + GetBinaryString() + " sucess\n";
		this.subject.updateUserConsole(message);
		return new Integer(i);
		/*
		if(this.next == null) {
			if(true) {
				this.AddNext(null).Process(null);
			}
			else {
				this.AddNext(null).Process(null);
			}
		}
		return null;*/

	}
}
