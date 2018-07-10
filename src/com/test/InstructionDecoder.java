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
		int[] instruction = decoder.Decode(i);
		
		//System.out.println("instruction is " + instruction[0] + " " + instruction[1] + " " + instruction[2] + " " + instruction[3]);
		
		ArrayList<Integer> list = new ArrayList<Integer>(instruction.length);
		for (int j = 0; j < instruction.length; j++)
			list.add(Integer.valueOf(instruction[j]));
		this.subject.updateUserConsole("Decode instruction success\n");
		
		return list;
	}
}
