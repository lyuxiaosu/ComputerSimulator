package com.test;

import java.util.*;
public class PC extends AbstrctProcessor {
	public PC(IUpdate subject) {
		this.bitset = new BitSet(12);
		this.subject = subject;
		// PC's first value shoule be 6 as 0-5 for reserved
		this.value = 8;
		this.nbits = 12;
	}
	
	@Override
	protected Object doProcess(Object data) {
		this.subject.updatePhase("Fetching Instruction");
		if (data == null) {
			return null;
		}
		
		if (value == 12) { // Because boot program only in address 8 and 9. If the PC at 10, it means the boot program finished.
			value = 8;
			SetValue(8);
			return new Integer(-1);
		}
		
		Integer obj = new Integer(value);	
		value++;
		
		System.out.println(value);
		if (value >= 2048) {
			// The first address of your program at octal address = octal 10 + length of boot program + octal 10.
			value = 8 + 2 + 8;
		}
		SetValue(value);
		System.out.println(this.GetValueWithInt());
		return obj;
	}
}