package com.test;

import java.util.BitSet;
import java.util.Observable;
import java.util.Observer;

public class IndexRegister extends AbstrctProcessor {
	public IndexRegister(IUpdate subject) {
		this.subject = subject;
		this.bitset = new BitSet(16);
		this.value = 0;
		this.nbits = 16;
	}	
}
