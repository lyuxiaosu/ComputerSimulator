package com.test;

import java.util.BitSet;

// index 0 value is 1, it means the computer is normal, otherwise, the computer is not normal 
public class MSR extends AbstrctProcessor {
	
	public MSR(IUpdate subject) {
		this.bitset = new BitSet(16);
		this.subject = subject;
		this.value = 0;
		this.nbits = 16;
	}
	
	public boolean GetStatus() {
		return this.bitset.get(0);
	}
	
	// we define the index 0 store the status: 0 is false, 1 is true
	public void SetStatus(boolean status) {
		this.bitset.set(0, status);
	}
	@Override
	protected Object doProcess(Object data) {
		return null;
	}
}
