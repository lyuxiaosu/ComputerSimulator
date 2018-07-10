package com.test;

import java.util.*;
public class CCR extends AbstrctProcessor {
	
	public CCR(IUpdate subject) {
		this.bitset = new BitSet(4);
		this.subject = subject;
		this.value = 0;
		this.nbits = 4;
	}
}
