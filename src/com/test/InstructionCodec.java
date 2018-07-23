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
  004		AMR r,x, address[,I]
  005		SMR r,x, address[,I]
  006		AIR r, immed
  007		SIR r, immed
  020(16)	MLT rx, ry
  021(17)	DVD rx, ry
  022(18)	TRR rx, ry
  023(19)	AND rx, ry
  024(20)	ORR rx, ry
  025(21)	NOT rx
  031(25)	SRC r, count, L/R, A/L
  032(26)	RRC r, count, L/R, A/L
  061(49)	IN r, devid
  062(50)	OUT r, devid
  063(51)	CHK, r, devid
 *
 */
public class InstructionCodec {
	private IUpdate subject;
	private boolean indirectAddressing;

	public InstructionCodec(IUpdate subject) {
		this.subject = subject;
		this.indirectAddressing = false; // no indirect addressing
	}

	public BitSet Encode(String instruction) {
		System.out.println("Encode, instruction is " + instruction);
		this.subject.updatePhase("Encoding Instruction");
		String[] segments = instruction.split(" ");
		String part1 = segments[0];
		if (segments.length == 1) { // For now, only HLT has no operand. If it is not HLT, it must be a invalid
									// instruction
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
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
			if (parts.length != 3) { // STR has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}
			int opcode = 2;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim());// Get operand address

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
		} else if (part1.equals("MFT")) { // self-defined machine fault instruction. It opcode is 63, with one fault
											// code operand
			int fault_code = Integer.parseInt(part2.trim());
			int opcode = 63;
			BitSet bitset = GetBitSet(opcode, fault_code);
			return bitset;
		} else if (part1.equals("JZ")) { // JZ instruction
			String sub = instruction.substring(2);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JZ has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}
			int opcode = 8;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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

		} else if (part1.equals("JNE")) { // JNE instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JNE has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}
			int opcode = 9;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
		} else if (part1.equals("JCC")) { // JCC instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JNE has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}

			int opcode = 10;
			int cc = Integer.parseInt(parts[0].trim()); // Get operand cc
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
			int address = Integer.parseInt(parts[1].trim()); // Get operand address

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
			int address = Integer.parseInt(parts[1].trim()); // Get operand address

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
			// there are 5 bits to hold immed, so do not exceed it. For signed and unsigned
			// integer, they have different value range
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
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
		} else if (part1.equals("JGE")) { // JGE instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // JGE has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}

			int opcode = 15;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
		} else if (part1.equals("AMR")) { // AMR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // AMR has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}

			int opcode = 4;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
		} else if (part1.equals("SMR")) { // SMR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 3) { // AMR has 3 operands, if there is less than 3, must be invalid instruction
				return null;
			}

			int opcode = 5;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int ix = Integer.parseInt(parts[1].trim()); // Get operand ix
			int address = Integer.parseInt(parts[2].trim()); // Get operand address

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
		} else if (part1.equals("AIR")) { // AIR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // AIR has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 6;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int immed = Integer.parseInt(parts[1].trim()); // Get operand immed

			if (immed > 15 || immed < -15) {
				this.subject.updateUserConsole("Invalid immed: " + immed + ", range should be [-15, 15]\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, r, immed);
			return bitset;
		} else if (part1.equals("SIR")) { // SIR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // SIR has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 7;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int immed = Integer.parseInt(parts[1].trim()); // Get operand immed

			if (immed > 15 || immed < -15) {
				this.subject.updateUserConsole("Invalid immed: " + immed + ", range should be [-15, 15]\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, r, immed);
			return bitset;
		} else if (part1.equals("MLT")) { // MLT instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // MLT has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 16;
			int rx = Integer.parseInt(parts[0].trim()); // Get operand rx
			int ry = Integer.parseInt(parts[1].trim()); // Get operand ry

			if ((rx != 0 && rx != 2) || (ry != 0 && ry != 2)) {
				this.subject.updateUserConsole(
						"Invalid GPR index: rx=" + rx + " ry=" + ry + ". rx and ry must be 0 or 2\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, rx, ry);
			return bitset;
		} else if (part1.equals("DVD")) { // DVD instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // DVD has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 17;
			int rx = Integer.parseInt(parts[0].trim()); // Get operand rx
			int ry = Integer.parseInt(parts[1].trim()); // Get operand ry

			if ((rx != 0 && rx != 2) || (ry != 0 && ry != 2)) {
				this.subject.updateUserConsole(
						"Invalid GPR index: rx=" + rx + " ry=" + ry + ". rx and ry must be 0 or 2\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, rx, ry);
			return bitset;
		} else if (part1.equals("TRR")) { // TRR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // TRR has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 18;
			int rx = Integer.parseInt(parts[0].trim()); // Get operand rx
			int ry = Integer.parseInt(parts[1].trim()); // Get operand ry

			if (rx > 3 || rx < 0 || ry > 3 || ry < 0) {
				this.subject
						.updateUserConsole("Invalid GPR index: rx=" + rx + " ry=" + ry + ". rx and ry must be [0-3]\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, rx, ry);
			return bitset;
		} else if (part1.equals("AND")) {// AND instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // AND has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 19;
			int rx = Integer.parseInt(parts[0].trim()); // Get operand rx
			int ry = Integer.parseInt(parts[1].trim()); // Get operand ry

			if (rx > 3 || rx < 0 || ry > 3 || ry < 0) {
				this.subject
						.updateUserConsole("Invalid GPR index: rx=" + rx + " ry=" + ry + ". rx and ry must be [0-3]\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, rx, ry);
			return bitset;
		} else if (part1.equals("ORR")) { // ORR instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // ORR has 2 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 20;
			int rx = Integer.parseInt(parts[0].trim()); // Get operand rx
			int ry = Integer.parseInt(parts[1].trim()); // Get operand ry

			if (rx > 3 || rx < 0 || ry > 3 || ry < 0) {
				this.subject
						.updateUserConsole("Invalid GPR index: rx=" + rx + " ry=" + ry + ". rx and ry must be [0-3]\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, rx, ry);
			return bitset;
		} else if (part1.equals("NOT")) {
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 1) { // NOT has 1 operands, if there is less than 2, must be invalid instruction
				return null;
			}

			int opcode = 21;
			int rx = Integer.parseInt(parts[0].trim()); // Get operand rx

			if (rx > 3 || rx < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + rx + "\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, rx);
			return bitset;
		} else if (part1.equals("SRC")) { // SRC instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 4) { // SRC has 4 operands, if there is less than 4, must be invalid instruction
				return null;
			}

			int opcode = 25;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int count = Integer.parseInt(parts[1].trim()); // Get operand count
			int LR = Integer.parseInt(parts[2].trim()); // Get operand L/R
			int AL = Integer.parseInt(parts[3].trim()); // Get operand A/L
			
			if (count > 15 || count < -15) {
				this.subject.updateUserConsole("Invalid count value: " + count + ". The range should be [-15-15]\n");
				return null;
			}
			
			if (r > 3 || r < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + r + "\n");
				return null;
			}

			if (count < 0 || count > 15) {
				this.subject.updateUserConsole("Invalid count value: " + count + "\n");
				return null;
			}

			if (LR < 0 || LR > 1 || AL < 0 || AL > 1) {
				this.subject.updateUserConsole("Invalid LR:" + LR + " or invalid AL:" + AL + "\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, r, count, LR, AL);
			return bitset;
		} else if (part1.equals("RRC")) { // RRC instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 4) { // RRC has 4 operands, if there is less than 4, must be invalid instruction
				return null;
			}

			int opcode = 26;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int count = Integer.parseInt(parts[1].trim()); // Get operand count
			int LR = Integer.parseInt(parts[2].trim()); // Get operand L/R
			int AL = Integer.parseInt(parts[3].trim()); // Get operand A/L

			if (count > 15 || count < -15) {
				this.subject.updateUserConsole("Invalid count value: " + count + ". The range should be [-15-15]\n");
				return null;
			}
			if (r > 3 || r < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + r + "\n");
				return null;
			}

			if (LR < 0 || LR > 1 || AL < 0 || AL > 1) {
				this.subject.updateUserConsole("Invalid LR:" + LR + " or invalid AL:" + AL + "\n");
				return null;
			}

			BitSet bitset = GetBitSet(opcode, r, count, LR, AL);
			return bitset;
		} else if (part1.equals("IN")) { //IN instruction
			String sub = instruction.substring(2);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // IN has 2 operands, if there is less than 4, must be invalid instruction
				return null;
			}

			int opcode = 49;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int devid = Integer.parseInt(parts[1].trim()); // Get operand devid
			
			if (r > 3 || r < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + r + "\n");
				return null;
			}

			if (devid > 31 || devid < 0) {
				this.subject.updateUserConsole("Invalid Device id: " + devid + ". It should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, devid);
			return bitset;
		} else if (part1.equals("OUT")) { //OUT instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // OUT has 2 operands, if there is less than 4, must be invalid instruction
				return null;
			}

			int opcode = 50;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int devid = Integer.parseInt(parts[1].trim()); // Get operand devid
			
			if (r > 3 || r < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + r + "\n");
				return null;
			}

			if (devid > 31 || devid < 0) {
				this.subject.updateUserConsole("Invalid Device id: " + devid + ". It should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, devid);
			return bitset;
		} else if (part1.equals("CHK")) { //CHK instruction
			String sub = instruction.substring(3);
			String[] parts = sub.split(",");
			if (parts.length != 2) { // CHK has 2 operands, if there is less than 4, must be invalid instruction
				return null;
			}

			int opcode = 51;
			int r = Integer.parseInt(parts[0].trim()); // Get operand r
			int devid = Integer.parseInt(parts[1].trim()); // Get operand devid
			
			if (r > 3 || r < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + r + "\n");
				return null;
			}

			if (devid > 31 || devid < 0) {
				this.subject.updateUserConsole("Invalid Device id: " + devid + ". It should be [0-31]\n");
				return null;
			}
			
			BitSet bitset = GetBitSet(opcode, r, devid);
			return bitset;
		}

		return null;
	}

	// Decode the instruction with int type. Return a int array. [0] is Opcode, [1]
	// is register, [2] is ix, [3] is address
	public int[] Decode(int instruction) {
		this.subject.updatePhase("Decoding Instruction");
		String bs = Integer.toBinaryString(instruction);
		StringBuilder sb = new StringBuilder();
		// if binary string is less than 16 bit, padding 0
		while (sb.length() + bs.length() < 16) {
			sb.append('0');
		}
		sb.append(bs);
		String dstr = sb.reverse().toString(); // reverse the string so we can get the right sequence value

		int address = Integer.parseInt(dstr.substring(0, 5), 2); // Get first five bits as the address operand
		int indirectAddressing = Integer.parseInt(dstr.substring(5, 6), 2);// Get next 1 bits as the indirect addressing
																			// flag
		System.out.println("!!!! indirectAddressing is " + indirectAddressing);
		int ix = Integer.parseInt(dstr.substring(6, 8), 2); // Get next 2 bits as the ix operand
		int r = Integer.parseInt(dstr.substring(8, 10), 2); // Get next 2 bits as the r operand
		int opcode = Integer.parseInt(dstr.substring(10), 2); // Get last 6 bits as the opcode
		System.out.println("Decode instruction I:" + indirectAddressing + " address:" + address + " ix:" + ix + " r:"
				+ r + " opcode:" + opcode);
		if (opcode == 30) {// this is a trap instruction, re-decode it
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
		} else if (opcode == 7 || opcode == 6) { // AIR or SIR
			int signed = Integer.parseInt(dstr.substring(0, 1), 2);
			int immed = Integer.parseInt(dstr.substring(1, 5), 2);
			if (signed == 1) {
				immed = -immed;
			}

			r = Integer.parseInt(dstr.substring(8, 10), 2); // Get next 2 bits as the r operand
			opcode = Integer.parseInt(dstr.substring(10), 2); // Get last 6 bits as the opcode

			System.out.println("Decode instruction I:" + " r:" + r + " opcode:" + opcode + " immed:" + immed);
			int[] parameters = new int[5];
			parameters[0] = opcode;
			parameters[1] = r;
			parameters[2] = 0;
			parameters[3] = 0;
			parameters[4] = immed;
			return parameters;
		} else if (opcode == 25 || opcode == 26) { //SRC or RRC
			int count = Integer.parseInt(dstr.substring(0, 4), 2);
			int LR = Integer.parseInt(dstr.substring(6, 7), 2);
			int AL = Integer.parseInt(dstr.substring(7, 8), 2);
			r = Integer.parseInt(dstr.substring(8, 10), 2);
			
			int [] parameters = new int[5];
			parameters[0] = opcode;
			parameters[1] = r;
			parameters[2] = AL;
			parameters[3] = LR;
			parameters[4] = count;
			System.out.println("Decode instruction: r:" + r + " AL:" + AL + " LR:" + LR + " count:" + count);
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

	public BitSet GetBitSet(int opcode, int r, int immed) {
		BitSet bitset = new BitSet(16);
		if (opcode == 7 || opcode == 6) {
			String binary_immed = Integer.toBinaryString(java.lang.Math.abs(immed));
			// set address for the instruction [0-4]
			StringBuilder sb_immed = new StringBuilder();
			// if binary string is less than 5 bit, padding 0
			while (sb_immed.length() + binary_immed.length() < 4) {
				sb_immed.append('0');
			}
			if (immed < 0) { // if immed is negative
				sb_immed.append('1');
			} else { // if immed is positive
				sb_immed.append('0');
			}
			sb_immed.append(binary_immed);
			binary_immed = sb_immed.toString();

			for (int i = 0; i < binary_immed.length(); i++) {
				if (binary_immed.charAt(i) == '1') {
					bitset.set(i);
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

		} else if (opcode == 16 || opcode == 17 || opcode == 18 || opcode == 19 || opcode == 20) {
			// set ry
			String binary_ry = Integer.toBinaryString(immed);
			StringBuilder sb_ry = new StringBuilder();
			// if binary string is less than 2 bit, padding 0
			while (sb_ry.length() + binary_ry.length() < 2) {
				sb_ry.append('0');
			}
			sb_ry.append(binary_ry);
			binary_ry = sb_ry.toString();
			for (int i = 0; i < binary_ry.length(); i++) {
				if (binary_ry.charAt(i) == '1') {
					bitset.set(i + 6);
				}
			}

			// set rx
			String binary_rx = Integer.toBinaryString(r);
			StringBuilder sb_rx = new StringBuilder();
			// if binary string is less than 2 bit, padding 0
			while (sb_rx.length() + binary_rx.length() < 2) {
				sb_rx.append('0');
			}
			sb_rx.append(binary_rx);
			binary_rx = sb_rx.toString();
			for (int i = 0; i < binary_rx.length(); i++) {
				if (binary_rx.charAt(i) == '1') {
					bitset.set(i + 8);
				}
			}
		} else if (opcode == 49 || opcode == 50 || opcode == 51) { //IN and OUT
			String binary_devid = Integer.toBinaryString(immed);
			StringBuilder sb_devid = new StringBuilder();
			// if binary string is less than 5 bit, padding 0
			while (sb_devid.length() + binary_devid.length() < 5) {
				sb_devid.append('0');
			}
			
			sb_devid.append(binary_devid);
			binary_devid = sb_devid.toString();
			for (int i = 0; i < binary_devid.length(); i++) {
				if (binary_devid.charAt(i) == '1') {
					bitset.set(i);
				}
			}
			
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
	 * Encode a instruction with 3 operands into binary code. Return the result as
	 * the BitSet object
	 * 
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

	public BitSet GetBitSet(int opcode, int r, int count, int LR, int AL) { // for instruction SRC and RRC
		BitSet bitset = new BitSet(16);
		// Set count
		String binary_count = Integer.toBinaryString(count);

		// set count for the instruction [0-3]
		StringBuilder sb_count = new StringBuilder();
		// if binary string is less than 4 bit, padding 0
		while (sb_count.length() + binary_count.length() < 4) {
			sb_count.append('0');
		}
		sb_count.append(binary_count);
		binary_count = sb_count.toString();
		for (int i = 0; i < binary_count.length(); i++) {
			if (binary_count.charAt(i) == '1') {
				bitset.set(i);
			}
		}

		// Set LR
		if (LR == 1) {
			bitset.set(6);
		}

		// Set AL
		if (AL == 1) {
			bitset.set(7);
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
	 * Encode a instruction with 1 operands into binary code. Return the result as
	 * the BitSet object
	 * 
	 * @param opcode
	 * @param trapCode
	 * @return
	 */
	public BitSet GetBitSet(int opcode, int trapCode) {
		BitSet bitset = new BitSet(16);
		if (opcode == 30 || opcode == 63) { // TRAP and MFT
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
		} else if (opcode == 21) { // NOT
			// set GPR
			int r = trapCode;
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
	 * Encode a instruction with no operands into binary code. Return the result as
	 * the BitSet object
	 * 
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
	 * 
	 * @param bitset
	 * @return
	 */
	public static int GetValueWithInt(BitSet bitset) {
		int bitInteger = 0;
		for (int i = 0; i < 16; i++)
			if (bitset.get(i))
				bitInteger |= (1 << i);
		return bitInteger;
	}
}
