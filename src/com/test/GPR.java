package com.test;

import java.util.*;
public class GPR extends AbstrctProcessor {
	public GPR(IUpdate subject) {
		this.bitset = new BitSet(16);
		this.subject = subject;
		this.value = 0;
		this.nbits = 16;
	}	
}
