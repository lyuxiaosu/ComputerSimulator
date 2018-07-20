package com.test;

import java.util.*;
/*01	LDR r, x, address[,I]
  02	STR r, x, address[,I]
  03	LDA r, x, address[,I]
  41(33) 	LDX x, address[,I]
  42(34)	STX x, address[,I]
  36(30)    TRAP trapCode
  77(63)    MFT faultCode
  010(8)    JZ r,x,address[,I]
  011(9)	JNE r,x,address[,I]
  012(10)	JCC cc,x,address[,I]
  013(11)	JMA x, address[,I]
  014(12)	JSR x, address[,I]
  015(13)	RFS Immed
  016(14)	SOB r,x,address[,I]
  017(15)	JGE r,x,address[,I]
 *
 */
public class InstructionCodec {
	private IUpdate subject;
	private boolean indirectAddressing; 
	public InstructionCodec(IUpdate subject) {
		this.subject = subject;
		this.indirectAddressing = false; //no indirect addressing
	}
	
	public BitSet Encode(String instruction) {
		System.out.println("Encode, instruction is " + instruction);
		this.subject.updatePhase("Encoding Instruction");
		String[] segments = instruction.split(" ");
		String part1 = segments[0];
		if (segments.length == 1) { // For now, only HLT has no operand. If it is not HLT, it must be a invalid instruction
			if (!part1.equals("HLT")) {
				System.out.println("invalid instruction " + part1);
				return null;
			} else {
				return this.GetBitSet(0);
			}
		}
		
		String part2 = segments[1];
		if (part1.equals("LDR")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // LDR has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 			
			int opcode = 1;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); //Get operand address
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
			
		} else if (part1.equals("STR")) { 
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { //STR has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 			
			int opcode = 2;
			int r = Integer.parseInt(parts[0].trim()); //Get operand r
			int ix = Integer.parseInt(parts[1].trim()); //Get operand ix
			int address = Integer.parseInt(parts[2].trim());//Get operand address
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		} else if (part1.equals("LDA")) { 
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // LDA has 3 operands, if there is less than 3, must be invalid instruction
				System.out.println("LDA parts is not 3, is " + parts.length);
				return null;
			} 			
			int opcode = 3;
			int r = Integer.parseInt(parts[0].trim()); // r index is invalid, should be [0-3]
			int ix = Integer.parseInt(parts[1].trim());// ix index is invalid, should be [1-3]
			int address = Integer.parseInt(parts[2].trim());// address is invalid, should be [0-31]
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) {// ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) {// address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		} else if (part1.equals("LDX")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // LDX has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			} 			
			int opcode = 33;
			int ix = Integer.parseInt(parts[0].trim());
			int address = Integer.parseInt(parts[1].trim());
						
			if (ix > 3 || ix < 0) {// ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) {// address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, 0, ix, address);
			return bitset;
		} else if (part1.equals("STX")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) {// STX has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			} 			
			int opcode = 34;
			int ix = Integer.parseInt(parts[0].trim());
			int address = Integer.parseInt(parts[1].trim());
						
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateMFR(5);
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) {// address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, 0, ix, address);
			return bitset;
		} else if (part1.equals("TRAP")) { // TRAP instruction
			int trap_code = Integer.parseInt(part2.trim());			
			int opcode = 30;
			BitSet bitset = GetBitSet(opcode, trap_code);
			return bitset;
		} else if (part1.equals("MFT")) { // self-defined machine fault instruction. It opcode is 63, with one fault code operand
			int fault_code = Integer.parseInt(part2.trim());
			int opcode = 63;
			BitSet bitset = GetBitSet(opcode, fault_code);
			return bitset;
		} else if (part1.equals("JZ")) { //JZ instruction
			String sub = instruction.substring(2);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JZ has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 			
			int opcode = 8;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); //Get operand address
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
			
		} else if (part1.equals("JNE")) { //JNE instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JNE has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 			
			int opcode = 9;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); //Get operand address
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		}  else if (part1.equals("JCC")) { //JCC instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JNE has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 	
			
			int opcode = 10;
			int cc = Integer.parseInt(parts[0].trim()); // Get operand cc
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); //Get operand address
			
			if (cc > 3 || cc < 0) { // cc index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal CC value " + cc + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, cc, ix, address);
			return bitset;
		} else if (part1.equals("JMA")) { // JMA instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // JMA has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			} 			
			int opcode = 11;
			
			int ix = Integer.parseInt(parts[0].trim()); // Get operand ix
			int address = Integer.parseInt(parts[1].trim()); //Get operand address
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, 0, ix, address);
			return bitset;
		} else if (part1.equals("JSR")) { // JSR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // JSR has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			} 
			
			int opcode = 12;	
			int ix = Integer.parseInt(parts[0].trim()); // Get operand ix
			int address = Integer.parseInt(parts[1].trim()); //Get operand address
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, 0, ix, address);
			return bitset;
		} else if (part1.equals("RFS")) {// RFS instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 1) { // RFS has 1 operands, if there is less than 1, must be invalid instruction
				return null;
			} 
			
			int opcode = 13;
			int immed = Integer.parseInt(parts[0].trim()); // Get immediate parameter
			// there are 5 bits to hold immed, so do not exceed it. For signed and unsigned integer, they have different value range
			if (immed >= 32 || immed < -15) {
				this.subject.updateUserConsole("Illegal Immed " + immed + ", it should be [-15-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, 0, 0, immed);
			return bitset;
		} else if (part1.equals("SOB")) {// SOB instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // SOB has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 
			
			int opcode = 14;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); //Get operand address
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		} else if (part1.equals("JGE")) { //JGE instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JGE has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			} 
			
			int opcode = 15;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); //Get operand address
			
			if (r > 3 || r < 0) { // r index is invalid, should be [0-3]
				this.subject.updateUserConsole("Illegal GPR index " + r + "\n");
				return null;
			}
			
			if (ix > 3 || ix < 0) { // ix index is invalid, should be [1-3]
				this.subject.updateUserConsole("Illegal IX index " + ix + "\n");
				return null;
			}
			
			if (address > 31 || address < 0) { // address is invalid, should be [0-31]
				this.subject.updateUserConsole("Illegal address " + address + ", it should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		}
		return null;
	}
	
	//Decode the instruction with int type. Return a int array. [0] is Opcode, [1] is register, [2] is ix, [3] is address
	public int[] Decode(int instruction) {
		this.subject.updatePhase("Decoding Instruction");
		String bs = Integer.toBinaryString(instruction);
		StringBuilder sb = new StringBuilder();
		// if binary string is less than 16 bit, padding 0 
		while (sb.length() + bs.length() < 16) {
			sb.append('0');
		}
		sb.append(bs);
		String dstr = sb.reverse().toString(); //reverse the string so we can get the right sequence value
		
		int address = Integer.parseInt(dstr.substring(0, 5), 2); //Get first five bits as the address operand
		int indirectAddressing = Integer.parseInt(dstr.substring(5, 6), 2);// Get next 1 bits as the indirect addressing flag
		System.out.println("!!!! indirectAddressing is " + indirectAddressing); 
		int ix = Integer.parseInt(dstr.substring(6, 8), 2); // Get next 2 bits as the ix operand
		int r = Integer.parseInt(dstr.substring(8, 10), 2); //Get next 2 bits as the r operand
		int opcode = Integer.parseInt(dstr.substring(10), 2); // Get last 6 bits as the opcode 
		System.out.println("Decode instruction I:" + indirectAddressing + " address:"+ address + " ix:" + ix + " r:" + r + " opcode:" + opcode); 
		if (opcode == 30) {//this is a trap instruction, re-decode it			
			int trapcode = Integer.parseInt(dstr.substring(0, 4), 2);
			int[] parameters = new int[2];
			parameters[0] = opcode;
			parameters[1] = trapcode;
			return parameters;
		} else if (opcode == 63) {// this is a machine fault instruction
			int fault_code = Integer.parseInt(dstr.substring(0, 4), 2);
			int[] parameters = new int[2];
			parameters[0] = opcode;
			parameters[1] = fault_code;
			return parameters;
		} else if (opcode == 0) { // HLT instruction
			int[] parameters = new int[1];
			parameters[0] = opcode;
			return parameters;
		}
		
		int[] parameters = new int[5];
		parameters[0] = opcode;
		parameters[1] = r;
		parameters[2] = ix;
		parameters[3] = indirectAddressing;
		parameters[4] = address;
		return parameters;
	}
	
	/**
	 * Encode a instruction with 3 operands into binary code. Return the result as the BitSet object
	 * @param opcode 
	 * @param r
	 * @param ix
	 * @param address
	 * @return
	 */
	public BitSet GetBitSet(int opcode, int r, int ix, int address) { 
		BitSet bitset = new BitSet(16);
		String binary_addr = Integer.toBinaryString(address);
		
		// set address for the instruction [0-4]
		StringBuilder sb_addr = new StringBuilder();
		// if binary string is less than 5 bit, padding 0 
		while (sb_addr.length() + binary_addr.length() < 5) {
			sb_addr.append('0');
		}
		sb_addr.append(binary_addr);
		binary_addr = sb_addr.toString();
		for (int i = 0; i < binary_addr.length(); i++) {
			if (binary_addr.charAt(i) == '1') {
				bitset.set(i);
			}
		}

		// set indirect addressing
		if (this.indirectAddressing) {
			System.out.println("set indirect address is " + this.indirectAddressing);
		}
		bitset.set(5, this.indirectAddressing);
		// set IX
		String binary_ix = Integer.toBinaryString(ix);
		StringBuilder sb_ix = new StringBuilder();
		// if binary string is less than 2 bit, padding 0 
		while (sb_ix.length() + binary_ix.length() < 2) {
			sb_ix.append('0');
		}
		sb_ix.append(binary_ix);
		binary_ix = sb_ix.toString();
		for (int i = 0; i < binary_ix.length(); i++) {
			if (binary_ix.charAt(i) == '1') {
				bitset.set(i + 6);
			}
		}

		// set GPR
		String binary_r = Integer.toBinaryString(r);
		StringBuilder sb_r = new StringBuilder();
		// if binary string is less than 2 bit, padding 0 
		while (sb_r.length() + binary_r.length() < 2) {
			sb_r.append('0');
		}
		sb_r.append(binary_r);
		binary_r = sb_r.toString();
		for (int i = 0; i < binary_r.length(); i++) {
			if (binary_r.charAt(i) == '1') {
				bitset.set(i + 8);
			}
		}

		// set opcode
		String binary_opcode = Integer.toBinaryString(opcode);
		StringBuilder sb_opcode = new StringBuilder();
		// if binary string is less than 6 bit, padding 0 
		while (sb_opcode.length() + binary_opcode.length() < 6) {
			sb_opcode.append('0');
		}
		sb_opcode.append(binary_opcode);
		binary_opcode = sb_opcode.toString();
		for (int i = 0; i < binary_opcode.length(); i++) {
			if (binary_opcode.charAt(i) == '1') {
				bitset.set(i + 10);
			}
		}
		return bitset;
	}
	/**
	 * Encode a instruction with 1 operands into binary code. Return the result as the BitSet object 
	 * @param opcode
	 * @param trapCode
	 * @return
	 */
	public BitSet GetBitSet(int opcode, int trapCode) {
		BitSet bitset = new BitSet(16);
		String binary_trapcode = Integer.toBinaryString(trapCode);
		
		// set value for the instruction [0-3]
		StringBuilder sb_trapcode = new StringBuilder();
		// if binary string is less than 4 bit, padding 0 
		while (sb_trapcode.length() + binary_trapcode.length() < 4) {
			sb_trapcode.append('0');
		}
		sb_trapcode.append(binary_trapcode);
		binary_trapcode = sb_trapcode.toString();
		for (int i = 0; i < binary_trapcode.length(); i++) {
			if (binary_trapcode.charAt(i) == '1') {
				bitset.set(i);
			}
		}
		String binary_opcode = Integer.toBinaryString(opcode);
		StringBuilder sb_opcode = new StringBuilder();
		// if binary string is less than 6 bit, padding 0 
		while (sb_opcode.length() + binary_opcode.length() < 6) {
			sb_opcode.append('0');
		}
		sb_opcode.append(binary_opcode);
		binary_opcode = sb_opcode.toString();
		for (int i = 0; i < binary_opcode.length(); i++) {
			if (binary_opcode.charAt(i) == '1') {
				bitset.set(i + 10);
			}
		}
		
		return bitset;
	}
	/**
	 * Encode a instruction with no operands into binary code. Return the result as the BitSet object
	 * @param opcode
	 * @return
	 */
	public BitSet GetBitSet(int opcode) {
		BitSet bitset = new BitSet(16);
		String binary_opcode = Integer.toBinaryString(opcode);
		StringBuilder sb_opcode = new StringBuilder();
		// if binary string is less than 6 bit, padding 0 
		while (sb_opcode.length() + binary_opcode.length() < 6) {
			sb_opcode.append('0');
		}
		sb_opcode.append(binary_opcode);
		binary_opcode = sb_opcode.toString();
		for (int i = 0; i < binary_opcode.length(); i++) {
			if (binary_opcode.charAt(i) == '1') {
				bitset.set(i + 10);
			}
		}
		
		return bitset;
	}
	
	public void SetIndirectAddress(boolean indirectAddress) {
		if (indirectAddress) {
			System.out.println("indirect address is true");
		} else {
			System.out.println("indirect address is false");
		}
		this.indirectAddressing = indirectAddress;
	}
	/**
	 * Get the BitSet's value with Integer format
	 * @param bitset
	 * @return
	 */
	public static int GetValueWithInt(BitSet bitset) {	
		int bitInteger = 0;
	    for(int i = 0 ; i < 16; i++)
	        if(bitset.get(i))
	            bitInteger |= (1 << i);
	    return bitInteger;
	}
}
