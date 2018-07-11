package com.test;

import java.util.BitSet;
public class MBR extends AbstrctProcessor {
	private Memory memory;
	public MBR(IUpdate subject, Memory memory) {
		this.bitset = new BitSet(16);
		this.subject = subject;
		this.value = 0;
		this.nbits = 16;
		this.memory = memory;
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
		
		Integer content = memory.GetValueWithInt(i);
		if (content != null) {
			SetValue(content.intValue());
		} else {
			return new Integer(-3);
		}
		return content;
	}
}
