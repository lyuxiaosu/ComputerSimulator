package com.test;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.SwingUtilities;

public class SingalController extends AbstrctProcessor {
	private Memory memory;
	private CentralProcessor cpu;
	private IStop simulator;
	private boolean isWaitingKeyboard = false;
	private final List<String> current_keyboard_value = new LinkedList<String>();

	private CurrentState current_state = new CurrentState();

	public SingalController(CentralProcessor cpu, Memory memory, IUpdate subject, IStop simulator, IGetInput input) {
		this.cpu = cpu;
		this.memory = memory;
		this.subject = subject;
		this.simulator = simulator;
	}

	@Override
	protected Object doProcess(Object data) {
		this.subject.updatePhase("Executing Instruction");
		if (data == null) {
			return null;
		}

		int result = 0;
		if (data instanceof ArrayList<?>) {
			ArrayList<Integer> array = (ArrayList<Integer>) (data);
			int opcode = array.get(0).intValue();

			if (opcode == 1) { // LDR
				// load register from memory
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleLDR(r, ix, i, address);
			} else if (opcode == 2) { // STR
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleSTR(r, ix, i, address);
			} else if (opcode == 3) { // LDA
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleLDA(r, ix, i, address);
			} else if (opcode == 33) {// LDX
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleLDX(ix, i, address);
			} else if (opcode == 34) {// STX
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleSTX(ix, i, address);
			} else if (opcode == 30) {// TRAP
				int trapcode = array.get(1);
				return HandleTRAP(trapcode);
			} else if (opcode == 63) {
				int fault_code = array.get(1);
				return HandleMFT(fault_code);
			} else if (opcode == 0) {
				return HandleHLT();
			} else if (opcode == 8) {// JZ
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJZ(r, ix, i, address);
			} else if (opcode == 9) { // JNE
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJNE(r, ix, i, address);
			} else if (opcode == 10) { // JCC
				int cc = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJCC(cc, ix, i, address);
			} else if (opcode == 11) {// JMA
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJMA(ix, i, address);
			} else if (opcode == 12) {// JSR
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJSR(ix, i, address);
			} else if (opcode == 13) {// RFS
				int immed = array.get(4).intValue();
				return HandleRFS(immed);
			} else if (opcode == 14) { // SOB
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleSOB(r, ix, i, address);
			} else if (opcode == 15) { // JGE
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJGE(r, ix, i, address);
			} else if (opcode == 4) { // AMR
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleAMR(r, ix, i, address);
			} else if (opcode == 5) { // SMR
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleSMR(r, ix, i, address);
			} else if (opcode == 6) { // AIR
				int r = array.get(1).intValue();
				int immed = array.get(4).intValue();
				return HandleAIR(r, immed);
			} else if (opcode == 7) { // SIR
				int r = array.get(1).intValue();
				int immed = array.get(4).intValue();
				return HandleSIR(r, immed);
			} else if (opcode == 16) { // MLT
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleMLT(rx, ry);
			} else if (opcode == 17) { // DVD
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleDVD(rx, ry);
			} else if (opcode == 18) { // TRR
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleTRR(rx, ry);
			} else if (opcode == 19) { // AND
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleAND(rx, ry);
			} else if (opcode == 20) { // ORR
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleORR(rx, ry);
			} else if (opcode == 21) { // NOT
				int rx = array.get(1).intValue();
				return HandleNOT(rx);
			} else if (opcode == 25) {// SRC
				int r = array.get(1).intValue();
				int AL = array.get(2).intValue();
				int LR = array.get(3).intValue();
				int count = array.get(4).intValue();
				return HandleSRC(r, AL, LR, count);
			} else if (opcode == 26) { // RRC
				int r = array.get(1).intValue();
				int AL = array.get(2).intValue();
				int LR = array.get(3).intValue();
				int count = array.get(4).intValue();
				return HandleRRC(r, AL, LR, count);
			} else if (opcode == 49) { // IN
				int r = array.get(1).intValue();
				int devid = array.get(4).intValue();
				return HandleIN(r, devid);
			} else if (opcode == 50) { //OUT
				int r = array.get(1).intValue();
				int devid = array.get(4).intValue();
				return HandleOUT(r, devid);
			} else if (opcode == 51) { //CHK
				int r = array.get(1).intValue();
				int devid = array.get(4).intValue();
				return HandleCHK(r, devid);
			}
			else {
				this.subject.updateUserConsole("Illegal Operation Code:" + opcode + "\n");
				this.subject.updateMFR(2);
				return new Integer(-2);
			} 
		} else {
			this.subject.updateUserConsole("Unkown Error!!!!\n");
			return new Integer(-2);
		}
	}

	private int HandleTRAP(int trapcode) {
		this.subject.updateUserConsole("Excute trap instruction: " + "TRAP " + trapcode + " sucess\n");
		// Get memory content from 2
		Integer instruction_address = memory.GetValueWithInt(2, false);
		if (instruction_address == null) {
			return -2;
		}
		cpu.SetPC(instruction_address.intValue()); // After executing the TRAP instruction, recover the PC to the value
													// of memory location 2
													// Because location 2 records the next program's address
		return 0;
	}

	private int HandleMFT(int faultCode) {
		this.subject.updateUserConsole("Excute machine fault instruction: " + "MFT " + faultCode + " sucess\n");
		// Get memory content from 4
		Integer instruction_address = memory.GetValueWithInt(4, false);
		if (instruction_address == null) {
			return -2;
		}

		System.out.println("instruction_address is " + instruction_address.intValue());
		cpu.SetPC(instruction_address.intValue()); // After executing the MFT instruction, recover the PC to the value
													// of memory location 4
													// Because location 4 records the next program's address
		return 0;
	}

	private int HandleHLT() {
		this.simulator.stop();
		return 0;
	}

	private int HandleLDR(int r, int ix, int i, int address) {
		// Load Register From Memory, r = 0..3
		// r <- c(EA)
		// r <- c(c(EA)), if I bit set

		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		this.subject.updateUserConsole("EA is " + ea + "\n");
		System.out.println("EA is " + ea + "\n");

		int value = 0;
		if (i == 0) {// in direct addressing mode
			Integer memory_content = memory.GetValueWithInt(ea, false); // fetch the content from the effective address
			if (memory_content == null) {
				this.subject.updateUserConsole(
						"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
				this.subject.updateMFR(4);
				return -2;
			}
			value = memory_content.intValue();
			System.out.println("address is " + ea + " memory content is " + value);
		} else { // in indirect addressing mode
			System.out.println("indirect address\n");
			Integer memory_content = memory.GetValueWithInt(ea, false); // fetch the content from the effective address
			if (memory_content == null) {
				this.subject.updateUserConsole(
						"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
				this.subject.updateMFR(4);
				return -2;
			}

			this.subject.updateUserConsole("c(EA) = " + memory_content.intValue() + "\n");
			memory_content = memory.GetValueWithInt(memory_content.intValue(), false); // fetch the content from the
																						// content of the effective
																						// address
			if (memory_content == null) {
				this.subject.updateUserConsole(
						"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
				this.subject.updateMFR(4);
				return -2;
			}
			value = memory_content.intValue();
			this.subject.updateUserConsole("c(c(EA)) = " + memory_content.intValue() + "\n");

			System.out.println("c(c(EA)) = " + memory_content.intValue() + "\n");
		}

		int result = cpu.SetGPR(r, value);
		if (result == 0) {
			this.subject.updateUserConsole(
					"Excute instruction success. Instruction: LDR " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject
					.updateUserConsole("Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
		}

		return result;
	}

	private int HandleSTR(int r, int ix, int i, int address) {
		// Store Register To Memory, r = 0..3
		// Memory(EA) <- c(r)

		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: STR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: STR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		int result = memory.Set(ea, GPR_content, false); // set GPR's content to M[ea]
		if (result == 0) {
			this.subject.updateUserConsole(
					"Excute instruction success. Instruction: STR " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject
					.updateUserConsole("Failed to execute instruction: STR " + r + ", " + ix + ", " + address + "\n");
		}

		return result;
	}

	private int HandleLDA(int r, int ix, int i, int address) {
		// Load Register with Address, r = 0..3
		// r <- EA

		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: LDA " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		int result = cpu.SetGPR(r, ea); // set GPR with ea

		if (result == 0) {
			this.subject.updateUserConsole(
					"Excute instruction success. Instruction: LDA " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject
					.updateUserConsole("Failed to execute instruction: LDA " + r + ", " + ix + ", " + address + "\n");
		}

		return result;
	}

	private int HandleLDX(int ix, int i, int address) {
		// Load Index Register from Memory, x = 1..3.
		// Xx <- c(EA)

		int ea = this.CalculateEA(0, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		Integer memory_content = memory.GetValueWithInt(ea, false); // fetch the content from the content of the
																	// effective address
		if (memory_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: LDX " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}

		int result = cpu.SetIX(ix, memory_content.intValue()); // set IX with M[ea]
		if (result == 0) {
			this.subject
					.updateUserConsole("Excute instruction success. Instruction: LDX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + ix + ", " + address + "\n");
		}

		return result;
	}

	private int HandleSTX(int ix, int i, int address) {
		// Store Index Register to Memory. X = 1..3
		// Memory(EA) <- c(Xx)

		int ea = this.CalculateEA(0, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		System.out.println("EA is " + ea + "\n");

		Integer ix_content = cpu.GetIX(ix); // Get IX's content
		if (ix_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			System.out.println("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			return -2;
		}

		int result = memory.Set(ea, ix_content.intValue(), false); // Set IX's content to location ea of the memory
		if (result == 0) {
			this.subject
					.updateUserConsole("Excute instruction success. Instruction: STX " + ix + ", " + address + "\n");
			System.out.println("Excute instruction success. Instruction: STX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			System.out.println("Failed to execute instruction: STX " + ix + ", " + address + "\n");
		}
		return result;
	}

	private int HandleJZ(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: JZ " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: JZ " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		if (GPR_content.intValue() == 0) {// if c(r) == 0, set PC to ea
			cpu.SetPC(ea);
			this.subject.updateUserConsole(
					"Execute instruction success. Instruction: JZ " + r + ", " + ix + ", " + address + "\n");

		} else { // if c(r) != 0, PC will increase 1 by itself
			this.subject.updateUserConsole(
					"Execute instruction success. Instruction: JZ " + r + ", " + ix + ", " + address + "\n");
		}
		return 0;
	}

	private int HandleJNE(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: JNE " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: JNE " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		if (GPR_content.intValue() != 0) {// if c(r) != 0, set PC to ea
			cpu.SetPC(ea);
		} else { // if c(r) == 0, PC will increase 1 by itself
		}
		this.subject.updateUserConsole(
				"Execute instruction success. Instruction: JNE " + r + ", " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleJCC(int cc, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: JCC " + cc + ", " + ix + ", " + address + "\n");
			return -2;
		}

		this.subject.updateUserConsole("EA is " + ea + "\n");
		boolean bit = cpu.GetCCRBit(cc);
		if (bit == true) { // if bit == true, set PC to ea
			cpu.SetPC(ea);
		} else { // if bit == false, PC will increase 1 by itself
		}

		this.subject.updateUserConsole(
				"Execute instruction success. Instruction: JCC " + cc + ", " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleJMA(int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: JMA " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		cpu.SetPC(ea);
		this.subject.updateUserConsole("Execute instruction success. Instruction: JMA " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleJSR(int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: JMA " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		// Since the instruction flows from PC to this object, the PC's value has
		// already increased 1 in PC object,
		// here we don't need to increase PC's value by 1, we just need to save the
		// current PC's value to R3
		int pc = Integer.parseInt(cpu.GetPC());
		cpu.SetGPR(3, pc);
		cpu.SetPC(ea);

		this.subject.updateUserConsole("Execute instruction success. Instruction: JSR " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleRFS(int immed) {
		// set immed to GPR-0
		cpu.SetGPR(0, immed);
		// set GPR-3's content to PC
		cpu.SetPC(cpu.GetGPR(3));
		return 0;
	}

	private int HandleSOB(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		cpu.SetGPR(r, GPR_content.intValue() - 1);

		if ((GPR_content.intValue() - 1) > 0) {
			cpu.SetPC(ea);
		} else {
			// PC will increase 1 automatically
		}
		return 0;
	}

	private int HandleJGE(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		if (GPR_content.intValue() >= 0) {
			cpu.SetPC(ea);
		} else {
			// PC will increase 1 automatically
		}

		this.subject.updateUserConsole("Execute instruction success: JGE " + r + ", " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleAMR(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: AMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: AMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		Integer memory_content = memory.GetValueWithInt(ea, false); // fetch the content from the content of the
																	// effective address
		if (memory_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: AMR " + r + ", " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}

		int result = GPR_content.intValue() + memory_content.intValue();
		if (result > 32767) {
			this.subject
					.updateUserConsole("OverFlow when Executing AMR " + r + ", " + ix + ", " + address + "\n");
			// set CC overflow
			cpu.SetCCRBit(0);
			return -2;
		}
		if (result < -32767) {
			this.subject
					.updateUserConsole("UnderFlow when Executing AMR " + r + ", " + ix + ", " +  address +  "\n");
			// set CC underflow
			cpu.SetCCRBit(1);
			return -2;
		}
		
		cpu.ResetCCR();
		cpu.SetGPR(r, result);
		this.subject.updateUserConsole("Execute instruction success: AMR " +  r + ", " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleSMR(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");

		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}

		Integer memory_content = memory.GetValueWithInt(ea, false); // fetch the content from the content of the
																	// effective address
		if (memory_content == null) {
			this.subject
					.updateUserConsole("Failed to execute instruction: SMR " + r + ", " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}

		int result = GPR_content.intValue() - memory_content.intValue();
		if (result > 32767) {
			this.subject
					.updateUserConsole("OverFlow when Executing SMR " + r + ", " + ix + ", " + address + "\n");
			// set CC overflow
			cpu.SetCCRBit(0);
			return -2;
		}
		if (result < -32767) {
			this.subject
					.updateUserConsole("UnderFlow when Executing SMR " + r + ", " + ix + ", " +  address +  "\n");
			// set CC underflow
			cpu.SetCCRBit(1);
			return -2;
		}
		cpu.ResetCCR();
		cpu.SetGPR(r, result);
		this.subject.updateUserConsole("Execute instruction success: SMR " +  r + ", " + ix + ", " + address + "\n");
		return 0;
	}

	private int HandleAIR(int r, int immed) {
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: AIR " + r + ", " + immed + "\n");
			return -2;
		}

		if (immed > 127 || immed < -127) {
			this.subject.updateUserConsole("Invalid immed: " + immed + ", range should be [-127, 127]\n");
			return -2;
		}

		cpu.SetGPR(r, immed + GPR_content.intValue());
		
		this.subject.updateUserConsole("Execute instruction success. Instruction: AIR " + r + ", " + immed + "\n");
		return 0;
	}

	private int HandleSIR(int r, int immed) {
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: SIR " + r + ", " + immed + "\n");
			return -2;
		}

		if (immed > 127 || immed < -127) {
			this.subject.updateUserConsole("Invalid immed: " + immed + ", range should be [-127, 127]\n");
			return -2;
		}

		cpu.SetGPR(r, GPR_content.intValue() - immed);
		
		this.subject.updateUserConsole("Execute instruction success. Instruction: SIR " + r + ", " + immed + "\n");
		return 0;
	}

	private int HandleMLT(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: MLT " + rx + ", " + ry + "\n");
			return -2;
		}

		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: MLT " + rx + ", " + ry + "\n");
			return -2;
		}

		int result = rx_content.intValue() * ry_content.intValue();
		if (result > 32767) {
			this.subject
					.updateUserConsole("OverFlow when Executing MLT " + rx + ", " + ry + ". Result=" + result + "\n");
			// set CC overflow
			cpu.SetCCRBit(0);
			return -2;
		}
		if (result < -32767) {
			this.subject
					.updateUserConsole("UnderFlow when Executing MLT " + rx + ", " + ry + ". Result=" + result + "\n");
			// set CC underflow
			cpu.SetCCRBit(1);
			return -2;
		}
		// reset CCR
		cpu.ResetCCR();
		int high_bits = (result >> 8) & 0XFF;
		int low_bits = result & 0xFF;
		this.cpu.SetGPR(rx, high_bits);
		this.cpu.SetGPR(rx + 1, low_bits);
		this.subject
				.updateUserConsole("Execute Instruction success: MLT " + rx + ", " + ry + ". result=" + result + "(" +InstructionCodec.GetBinaryString(result) + ")\n");
		return 0;
	}

	private int HandleDVD(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: DVD " + rx + ", " + ry + "\n");
			return -2;
		}

		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: DVD " + rx + ", " + ry + "\n");
			return -2;
		}

		if (ry_content.intValue() == 0) {
			this.subject.updateUserConsole("ry is 0. Failed to execute instruction: DVD " + rx + ", " + ry + "\n");
			this.cpu.SetCCRBit(2);
			//Let PC to 0 where is the TRAP instruction
			this.cpu.SetPC(0);
			return -2;
		}
		
		// reset CCR
		cpu.ResetCCR();
		int quotient = rx_content.intValue() / ry_content.intValue();
		int remainder = rx_content.intValue() % ry_content.intValue();

		this.cpu.SetGPR(rx, quotient);
		this.cpu.SetGPR(rx + 1, remainder);
		this.subject.updateUserConsole("Execute Instruction success: DVD " + rx + ", " + ry + ". quotient=" + quotient
				+ ", remainder=" + remainder + "\n");
		return 0;
	}

	private int HandleTRR(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: TRR " + rx + ", " + ry + "\n");
			return -2;
		}

		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: TRR " + rx + ", " + ry + "\n");
			return -2;
		}

		if (ry_content.intValue() == rx_content.intValue()) {
			this.subject.updateUserConsole("Execute Instruction success: TRR " + rx + ", " + ry + ". c(rx)="
					+ rx_content.intValue() + " equal to c(ry)=" + ry_content.intValue() + "\n");
			this.cpu.SetCCRBit(3);
		} else {
			this.subject.updateUserConsole("Execute Instruction success: TRR " + rx + ", " + ry + ". c(rx)="
					+ rx_content.intValue() + " not equal to c(ry)=" + ry_content.intValue() + "\n");
			this.cpu.ResetCCR();
		}

		return 0;
	}

	private int HandleAND(int rx, int ry) {
		BitSet rx_bitset = cpu.GetGPRWithBitSet(rx);		
		if (rx_bitset == null) {
			this.subject.updateUserConsole("Failed to execute instruction: NOT " + rx + "\n");
			return -2;
		}
		
		
		BitSet ry_bitset = cpu.GetGPRWithBitSet(ry);		
		if (ry_bitset == null) {
			this.subject.updateUserConsole("Failed to execute instruction: NOT " + rx + "\n");
			return -2;
		}
		
		BitSet new_bitset = new BitSet(16);
		for (int i = 0; i < 16; i++) {
			if (rx_bitset.get(i) && ry_bitset.get(i)) {
				new_bitset.set(i);
			}
		}
		cpu.SetGPRWithBitSet(rx, new_bitset);
		this.subject.updateUserConsole("Execute Instruction success: AND " + rx + ", " + ry + "\n");
		this.subject.updateUserConsole("rx(" + InstructionCodec.GetBinaryString(rx_bitset) + ") AND ry(" + 
						InstructionCodec.GetBinaryString(ry_bitset) + ")=" + InstructionCodec.GetBinaryString(new_bitset) + "\n");

		return 0;
	}

	private int HandleORR(int rx, int ry) {
		BitSet rx_bitset = cpu.GetGPRWithBitSet(rx);		
		if (rx_bitset == null) {
			this.subject.updateUserConsole("Failed to execute instruction: NOT " + rx + "\n");
			return -2;
		}
		
		
		BitSet ry_bitset = cpu.GetGPRWithBitSet(ry);		
		if (ry_bitset == null) {
			this.subject.updateUserConsole("Failed to execute instruction: NOT " + rx + "\n");
			return -2;
		}
		
		BitSet new_bitset = new BitSet(16);
		for (int i = 0; i < 16; i++) {
			if (rx_bitset.get(i) || ry_bitset.get(i)) {
				new_bitset.set(i);
			}
		}
		cpu.SetGPRWithBitSet(rx, new_bitset);
		this.subject.updateUserConsole("Execute Instruction success: ORR " + rx + ", " + ry + "\n");
		this.subject.updateUserConsole("rx(" + InstructionCodec.GetBinaryString(rx_bitset) + ") ORR ry(" + 
						InstructionCodec.GetBinaryString(ry_bitset) + ")=" + InstructionCodec.GetBinaryString(new_bitset) + "\n");

		return 0;
	}

	private int HandleNOT(int rx) {
		BitSet bitset = cpu.GetGPRWithBitSet(rx);		
		if (bitset == null) {
			this.subject.updateUserConsole("Failed to execute instruction: NOT " + rx + "\n");
			return -2;
		}
		
		BitSet new_bitset = (BitSet)bitset.clone();
		
		for (int i = 0; i < 16; i++) {
			if (new_bitset.get(i)) {
				new_bitset.set(i, false);
			} else {
				new_bitset.set(i, true);
			}
		}
		
		
		this.subject.updateUserConsole("Execute instruction success. NOT " + rx + "\n");	
		cpu.SetGPRWithBitSet(rx, new_bitset);;
		
		return 0;
	}

	private int HandleSRC(int r, int AL, int LR, int count) {
		BitSet bitset = cpu.GetGPRWithBitSet(r);
		cpu.ResetCCR();
		if (bitset == null) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: SRC " + r + ", " + count + ", " + LR + ", " + AL + "\n");
			return -2;
		}

		// this.subject.updateUserConsole("SRC " + r + ", " + count + ", " + LR + ", " +
		// AL + "\n");
		if (AL == 0) { // Arithmetic shift
			if (LR == 1) {
				// letf shift
				BitSet bs = ArithmeticShift(bitset, true, count);
				cpu.SetGPRWithBitSet(r, bs);
			} else {
				// right shift
				BitSet bs = ArithmeticShift(bitset, false, count);
				cpu.SetGPRWithBitSet(r, bs);
			}
		} else { // logical shift
			if (LR == 1) { // left shift
				BitSet bs = LogicalShift(bitset, true, count);
				cpu.SetGPRWithBitSet(r, bs);
			} else {
				// right shift
				BitSet bs = LogicalShift(bitset, false, count);
				cpu.SetGPRWithBitSet(r, bs);
			}
		}

		this.subject.updateUserConsole(
				"Execute instruction success: SRC " + r + ", " + count + ", " + LR + ", " + AL + "\n");
		return 0;
	}

	private int HandleRRC(int r, int AL, int LR, int count) {
		cpu.ResetCCR();
		BitSet bitset = cpu.GetGPRWithBitSet(r);
		if (bitset == null) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: RRC " + r + ", " + count + ", " + LR + ", " + AL + "\n");
			return -2;
		}

		if (AL == 0) { // Arithmetic rotate
			this.subject.updateUserConsole("Not support arithmetic rotate. Failed to execute instruction: RRC " + r
					+ ", " + count + ", " + LR + ", " + AL + "\n");
			return -2;
		} else { // logical rotate
			if (LR == 1) { // left rotate
				BitSet bs = LogicalRotate(bitset, true, count);
				cpu.SetGPRWithBitSet(r, bs);
			} else {
				// right rotate
				BitSet bs = LogicalRotate(bitset, false, count);
				cpu.SetGPRWithBitSet(r, bs);
			}
		}

		this.subject.updateUserConsole(
				"Execute instruction success: SRC " + r + ", " + count + ", " + LR + ", " + AL + "\n");
		return 0;
	}

	/**
	 * Handle logic shifting
	 * 
	 * @param bitset
	 * @param leftShifting
	 * @param count
	 */
	private BitSet LogicalShift(BitSet bitset, boolean leftShifting, int count) {
		BitSet return_bitset = new BitSet(16);
		int[] reverse_bits = new int[16];
		int value = InstructionCodec.GetValueWithInt(bitset);
		for (int i = 0; i < 16; i++) {
			if (bitset.get(i) == true) {
				reverse_bits[15 - i] = 1;
			} else {
				reverse_bits[15 - i] = 0;
			}
		}

		for (int i = 0; i < 16; i++) {
			if (reverse_bits[i] == 1) {
				System.out.println("[" + i + "]=" + 1);
			} else {
				System.out.println("[" + i + "]=" + 0);
			}
		}

		int[] tmp_bits = new int[16];
		if (leftShifting == false) {
			// compensate 0 to the left
			for (int i = 0; i < count; i++) {
				tmp_bits[i] = 0;
			}

			for (int i = 0; i < 16 - count; i++) {
				tmp_bits[i + count] = reverse_bits[i];
			}

			// set new value to return_bitset
			for (int i = 0; i < 16; i++) {
				if (tmp_bits[15 - i] == 1) {
					return_bitset.set(i);
				}
			}			
					
		} else {
			// copy position count to 16 to the tmp_bits
			for (int i = count; i < 16; i++) {
				tmp_bits[i - count] = reverse_bits[i];
			}

			// compensate 0 to the left count bits
			for (int i = 16 - count; i < 16; i++) {
				tmp_bits[i] = 0;
			}

			// set new value to return_bitset
			for (int i = 0; i < 16; i++) {
				if (tmp_bits[15 - i] == 1) {
					return_bitset.set(i);
				}
			}
						
			value = value * (int)Math.pow(2, count);
			if (value > 32767) {
				cpu.SetCCRBit(0);
				this.subject.updateUserConsole("Overflow\n");
			}
			
			if (value < -32767) {
				cpu.SetCCRBit(1);
				this.subject.updateUserConsole("Underflow\n");
			}
		}

		return return_bitset;
	}

	public BitSet ArithmeticShift(BitSet bitset, boolean leftShifting, int count) {
		BitSet return_bitset = new BitSet(16);
		int[] reverse_bits = new int[16];
		int value = InstructionCodec.GetValueWithInt(bitset);
		
		for (int i = 0; i < 16; i++) {
			if (bitset.get(i) == true) {
				reverse_bits[15 - i] = 1;
			} else {
				reverse_bits[15 - i] = 0;
			}
		}

		for (int i = 0; i < 16; i++) {
			if (reverse_bits[i] == 1) {
				System.out.println("[" + i + "]=" + 1);
			} else {
				System.out.println("[" + i + "]=" + 0);
			}
		}

		int[] tmp_bits = new int[16];
		if (leftShifting == false) { // right shifting

			if (reverse_bits[0] == 1) {
				// compensate 1 to the left
				for (int i = 0; i < count; i++) {
					tmp_bits[i] = 1;
				}
			} else {
				// compensate 0 to the left
				for (int i = 0; i < count; i++) {
					tmp_bits[i] = 0;
				}
			}

			for (int i = 0; i < 16 - count; i++) {
				tmp_bits[i + count] = reverse_bits[i];
			}

			// set new value to return_bitset
			for (int i = 0; i < 16; i++) {
				if (tmp_bits[15 - i] == 1) {
					return_bitset.set(i);
				}
			}
			
		} else { // left shifting
			// keep the sign bit unchanged
			tmp_bits[0] = reverse_bits[0];
			// copy position count to 16 to the tmp_bits
			for (int i = count + 1; i < 16; i++) {
				tmp_bits[i - count] = reverse_bits[i];
			}

			// compensate 0 to the left count bits
			for (int i = 16 - count; i < 16; i++) {
				tmp_bits[i] = 0;
			}

			// set new value to return_bitset
			for (int i = 0; i < 16; i++) {
				if (tmp_bits[15 - i] == 1) {
					return_bitset.set(i);
				}
			}
			
			value = value * (int)Math.pow(2, count);
			if (value > 32767) {
				cpu.SetCCRBit(0);
				this.subject.updateUserConsole("Overflow\n");
			}
			
			if (value < -32767) {
				cpu.SetCCRBit(1);
				this.subject.updateUserConsole("Underflow\n");
			}
		}

		System.out.println("After shifting");
		for (int i = 0; i < 16; i++) {
			if (tmp_bits[i] == 1) {
				System.out.println("[" + i + "]=" + 1);
			} else {
				System.out.println("[" + i + "]=" + 0);
			}
		}
		return return_bitset;
	}

	private BitSet LogicalRotate(BitSet bitset, boolean leftShifting, int count) {
		BitSet return_bitset = new BitSet(16);
		int[] reverse_bits = new int[16];
		for (int i = 0; i < 16; i++) {
			if (bitset.get(i) == true) {
				reverse_bits[15 - i] = 1;
			} else {
				reverse_bits[15 - i] = 0;
			}
		}

		for (int i = 0; i < 16; i++) {
			if (reverse_bits[i] == 1) {
				System.out.println("[" + i + "]=" + 1);
			} else {
				System.out.println("[" + i + "]=" + 0);
			}
		}

		int[] tmp_bits = new int[16];
		if (leftShifting == false) { // right rotate
			for (int i = 0; i < 16; i++) {
				tmp_bits[(i + count) % 16] = reverse_bits[i];
			}
			// set new value to return_bitset
			for (int i = 0; i < 16; i++) {
				if (tmp_bits[15 - i] == 1) {
					return_bitset.set(i);
				}
			}
			
		} else { // left rotate
			for (int i = 0; i < 16; i++) {
				tmp_bits[(i + (16 - count)) % 16] = reverse_bits[i];
			}

			// set new value to return_bitset
			for (int i = 0; i < 16; i++) {
				if (tmp_bits[15 - i] == 1) {
					return_bitset.set(i);
				}
			}
			
		}

		System.out.println("After shifting");
		for (int i = 0; i < 16; i++) {
			if (tmp_bits[i] == 1) {
				System.out.println("[" + i + "]=" + 1);
			} else {
				System.out.println("[" + i + "]=" + 0);
			}
		}
		return return_bitset;
	}

	private int HandleIN(int r, int devid) {
		current_state.pc = Integer.parseInt(cpu.GetPC());
		InputThread input_thread = new InputThread(r, devid);
		input_thread.start();
		return -4;
	}

	private int HandleOUT(int r, int devid) {
		this.subject.updateUserConsole("r is " + r + " The output device is " + devid + "\n");
		if (devid == 1) { // console printer
			Integer r_content = cpu.GetGPR(r);
			if (r_content == null) {
				this.subject.updateUserConsole("Failed to execute instruction: OUT " + r + ", " + devid + "\n");
				return -2;
			}
			
			this.subject.updateUserConsole("Output " + r_content.intValue() + " to console printer from GPR-" + r + "\n");
			return 0;
		} else {
			this.subject.updateUserConsole("The output device is " + devid + ". We haven't simulated this device\n");
			return 0;
		}
	}
	
	private int HandleCHK(int r, int devid) {
		this.subject.updateUserConsole("r is " + r + " The checking device is " + devid + "\n");
		
		
			if (r > 3 || r < 0) {
				this.subject.updateUserConsole("Invalid GPR index: " + r + ". Failed to execute instruction: CHK " + r + ", " + devid + "\n");
				return -2;
			}
			
			if (devid > 31 || devid < 0) {
				this.subject.updateUserConsole("Invalid device id: " + devid + ". Range should be [0-31]. Failed to execute instruction: CHK " + r + ", " + devid + "\n");
				return -2;
			}
			cpu.SetGPR(r, 0);
			this.subject.updateUserConsole("Excute instruction success: CHK " + r + ", " + devid + "\n");
			return 0;
		
	}
	/**
	 * Calculate the effective address
	 * 
	 * @param ix
	 * @param i
	 * @param address
	 * @return
	 */
	private int CalculateEA(int ix, int i, int address) {
		Integer ix_content = cpu.GetIX(ix);
		if (ix_content == null) {
			return -2;
		}

		if (i == 0) { // no indirect addressing
			return (address + ix_content.intValue());
		} else { // indirect address
			Integer memory_content = memory.GetValueWithInt(address + ix_content.intValue(), false);
			if (memory_content == null) {
				return -2;
			}
			return memory_content.intValue();
		}
	}

	public void InputNotify(int devid, String number) {
		if (devid == 0) {
			synchronized (current_keyboard_value) {
				current_keyboard_value.clear();
				if (isWaitingKeyboard) {
					current_keyboard_value.add(number);
					current_keyboard_value.notify();
				}
			}
		}

	}

	private class InputThread extends Thread {
		private int devid;
		private int r;
		
		public InputThread(int r, int devid) {
			this.devid = devid;
			this.r = r;
		}

		@Override
		public void run() {
			if (devid == 0) {
				subject.updateUserConsole("Waiting input...\n");
				isWaitingKeyboard = true;
				synchronized (current_keyboard_value) {
					while (current_keyboard_value.isEmpty()) {
						try {
							current_keyboard_value.wait();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					String number = current_keyboard_value.remove(0);
					isWaitingKeyboard = false;
					try {
						int input_number = Integer.parseInt(number);
						if (input_number > 32767 || input_number < -32767) {
							SingalController.this.subject.updateUserConsole("Invalid input number:" + number + ". The input number range should be [-32767-32767]\n");
							return;
						}
						SingalController.this.subject.updateUserConsole("Input number is " + number + "\n");
						int result = cpu.SetGPR(r, input_number);
						if (result == 0) {
							subject.updateUserConsole(
									"Excute instruction success. Instruction: IN " + r + ", " + devid + "\n");
						} else {
							subject.updateUserConsole("Failed to execute instruction: IN " + r + ", " + devid + "\n");
						}
					} catch (NumberFormatException e) {
						SingalController.this.subject.updateUserConsole("Invalid input number:" + number
								+ ". The input number range should be [-32767-32767]\n");
					}
				}
			} else {
				subject.updateUserConsole("Input device id is " + devid + ". We haven't simulated this device\n");
			}
		}
	}

	private class CurrentState {
		public int pc;
	}

}
