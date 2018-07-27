package com.test;

import java.util.*;
public class PC extends AbstrctProcessor {
	private int bootEndLocation;
	public PC(IUpdate subject) {
		this.bitset = new BitSet(12);
		this.subject = subject;
		// PC's first value shouled be 6 as 0-5 for reserved
		this.value = 8;
		this.nbits = 12;
	}
	
	@Override
	protected Object doProcess(Object data) {
		this.subject.updatePhase("Fetching Instruction");
	
		if (value == bootEndLocation) { // boot program finished.
			value = 8;
			SetValue(8);
			return new Integer(-1);
		}
		
		Integer obj = new Integer(value);	
		value++;
		
		if (value >= 2048) { // If PC is out of memory location, reset it to user's program location 
			// The first address of your program at octal address = octal 10 + length of boot program + octal 10 = 18.
			value = 8 + 13 + 8;
		}
		
		SetValue(value);
		return obj;
	}
	
	/**
	 * Set the boot program's end location 
	 * @param location
	 * @return
	 */
	public boolean SetBootEndLocation(int location) {
		if (location >= 2048 || location < 0) {
			return false;
		}
		this.bootEndLocation = location;
		return true;
	}
	@Override
	protected void postSetValue(int old_value, int new_value) {
		if (old_value != new_value) {
			this.subject.updateUserConsole("PC from " + old_value + " to " + new_value + "\n");
		} else {
			this.subject.updateUserConsole("PC from " + (old_value - 1) + " to " + new_value + "\n");
		}
	}
		
}
