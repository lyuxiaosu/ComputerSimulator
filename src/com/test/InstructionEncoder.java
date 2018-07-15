package com.test;

import java.util.BitSet;

public class InstructionEncoder {
	private InstructionCodec encoder;
	private IUpdate subject;
	public InstructionEncoder(IUpdate subject) {
		this.subject = subject;
		encoder = new InstructionCodec(subject);
	}
	
	BitSet Encode(String instruction) {
		return encoder.Encode(instruction);
	}
	
	public void SetIndirectAddress(boolean indirectAddress) {
		encoder.SetIndirectAddress(indirectAddress);
	}
}
