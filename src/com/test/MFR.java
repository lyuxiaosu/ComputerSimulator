package com.test;

import java.util.BitSet;

public class MFR extends AbstrctProcessor {
/*	
 *	ID	Fault
 *	0	Illegal Memory Address to Reserved Locations
 *	1	Illegal TRAP code
 *	2	Illegal Operation Code
 *	3	Illegal Memory Address beyond 2048 (memory installed)
 *  4   Illegal Access Memory address. No allocation
 *  5   Illegal IX index (1-3)
 *  6   Illegal GPR index (0-3)
 *  7   Encode instruction error
 */
	public MFR(IUpdate subject) {
		this.bitset = new BitSet(4);
		this.subject = subject;
		this.value = 0;
		this.nbits = 4;
	}
	
	@Override
	protected Object doProcess(Object data) {
		return null;
	}
}
