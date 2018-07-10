package com.test;

import java.util.*;
/*01	LDR r, x, address[,I]
  02	STR r, x, address[,I]
  03	LDA r, x, address[,I]
  41(33) 	LDX x, address[,I]
  42(34)	STX x, address[,I]
  36(30)    TRAP trapCode
  77(63)    MFT faultCode
 *
 */
public class InstructionCodec {
	private IUpdate subject;
	public InstructionCodec(IUpdate subject) {
		this.subject = subject;
	}
	
	public BitSet Encode(String instruction) {
		System.out.println("Encode, instruction is " + instruction);
		this.subject.updatePhase("Encoding Instruction");
		String[] segments = instruction.split(" ");
		String part1 = segments[0];
		if (segments.length == 1) {
			if (!part1.equals("HLT")) {
				System.out.println("invalid instruction " + part1);
				return null;
			} else {
				//TODO process HLT instruction
				return null;
			}
		}
		
		String part2 = segments[1];
		if (part1.equals("LDR")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) {
				return null;
			} 			
			int opcode = 1;
			int r = Integer.parseInt(parts[0].trim());
			int ix = Integer.parseInt(parts[1].trim());
			int address = Integer.parseInt(parts[2].trim());
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
			
		} else if (part1.equals("STR")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) {
				return null;
			} 			
			int opcode = 2;
			int r = Integer.parseInt(parts[0].trim());
			int ix = Integer.parseInt(parts[1].trim());
			int address = Integer.parseInt(parts[2].trim());
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		} else if (part1.equals("LDA")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) {
				System.out.println("LDA parts is not 3, is " + parts.length);
				return null;
			} 			
			int opcode = 3;
			int r = Integer.parseInt(parts[0].trim());
			int ix = Integer.parseInt(parts[1].trim());
			int address = Integer.parseInt(parts[2].trim());
			
			BitSet bitset = GetBitSet(opcode, r, ix, address);
			return bitset;
		} else if (part1.equals("LDX")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) {
				return null;
			} 			
			int opcode = 33;
			int ix = Integer.parseInt(parts[0].trim());
			int address = Integer.parseInt(parts[1].trim());
			
			BitSet bitset = GetBitSet(opcode, 0, ix, address);
			return bitset;
		} else if (part1.equals("STX")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) {
				return null;
			} 			
			int opcode = 34;
			int ix = Integer.parseInt(parts[0].trim());
			int address = Integer.parseInt(parts[1].trim());
			
			BitSet bitset = GetBitSet(opcode, 0, ix, address);
			return bitset;
		} else if (part1.equals("TRAP")) {
			int trap_code = Integer.parseInt(part2.trim());			
			int opcode = 30;
			BitSet bitset = GetBitSet(opcode, trap_code);
			return bitset;
		} else if (part1.equals("MFT")) {
			int fault_code = Integer.parseInt(part2.trim());
			int opcode = 63;
			BitSet bitset = GetBitSet(opcode, fault_code);
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
		String dstr = sb.reverse().toString();
		
		int address = Integer.parseInt(dstr.substring(0, 5), 2);
		int ix = Integer.parseInt(dstr.substring(6, 8), 2);
		int r = Integer.parseInt(dstr.substring(8, 10), 2);
		int opcode = Integer.parseInt(dstr.substring(10), 2);
		
		if (opcode == 30) {//this is a trap instruction, re-decode it			
			int trapcode = Integer.parseInt(dstr.substring(0, 4), 2);
			int[] parameters = new int[2];
			parameters[0] = opcode;
			parameters[1] = trapcode;
			return parameters;
		} else if (opcode == 63) {// this is a machine fault instruciton
			int fault_code = Integer.parseInt(dstr.substring(0, 4), 2);
			int[] parameters = new int[2];
			parameters[0] = opcode;
			parameters[1] = fault_code;
			return parameters;
		}
		
		int[] parameters = new int[4];
		parameters[0] = opcode;
		parameters[1] = r;
		parameters[2] = ix;
		parameters[3] = address;
		return parameters;
	}
	
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

		// we always set it to 1, which means indirect addressing
		//bitset.set(5);
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
}
