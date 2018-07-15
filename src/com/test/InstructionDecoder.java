package com.test;

import java.util.ArrayList;

public class InstructionDecoder extends AbstrctProcessor {
	private InstructionCodec decoder;
	public InstructionDecoder(IUpdate subject) {
		this.subject = subject;
		decoder = new InstructionCodec(subject);
	}
	
	protected Object doProcess(Object data) {
		if (data == null) {
			return null;
		}
		
		int i = (Integer) data;
		if (i == -1 || i == -2 || i == -3) {
			return -i;
		}
		
		int[] instruction = decoder.Decode(i);
		
		//Convert int[] to a ArrayList and return		
		ArrayList<Integer> list = new ArrayList<Integer>(instruction.length);
		for (int j = 0; j < instruction.length; j++)
			list.add(Integer.valueOf(instruction[j]));
		this.subject.updateUserConsole("Decode instruction success\n");
		
		return list;
	}
	
	public void SetIndirectAddress(boolean indirectAddress) {
		decoder.SetIndirectAddress(indirectAddress);
	}
}
