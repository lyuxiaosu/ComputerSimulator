package com.test;

import java.util.ArrayList;

public class SingalController extends AbstrctProcessor {
	private Memory memory;
	private CentralProcessor cpu;
	public SingalController (CentralProcessor cpu, Memory memory, IUpdate subject) {
		this.cpu = cpu;
		this.memory = memory;
		this.subject = subject;
	}
	
	@Override
	protected Object doProcess(Object data) {
		this.subject.updatePhase("Executing Instruction");
		if (data == null) {
			return null;
		}
		
		int result = 0;
		if (data instanceof ArrayList<?>) {
			ArrayList<Integer> array = (ArrayList<Integer>)(data);	
			int opcode = array.get(0).intValue();
			
			if (opcode == 1) { // LDR
				// load register from memory
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int address = array.get(3).intValue();
				return HandleLDR(r, ix, address);			
			} else if (opcode == 2) { // STR
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int address = array.get(3).intValue();
				return HandleSTR(r, ix, address);
			} else if (opcode == 3) { // LDA
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int address = array.get(3).intValue();
				return HandleLDA(r, ix, address);
			} else if (opcode == 33) {// LDX	
				int ix = array.get(2).intValue();
				int address = array.get(3).intValue();
				return HandleLDX(ix, address);
			} else if (opcode == 34) {// STX
				int ix = array.get(2).intValue();
				int address = array.get(3).intValue();
				return HandleSTX(ix, address);
			} else if (opcode == 30) {// TRAP 
				int trapcode = array.get(1);
				return HandleTRAP(trapcode);
			} else if (opcode == 63) {
				int fault_code = array.get(1);
				return HandleMFT(fault_code);
			} else {
				this.subject.updateUserConsole("Unkown instruction. Opcode:" + opcode + "\n");
				return new Integer(-2);
			}
		} else {
			this.subject.updateUserConsole("Unkown Error!!!!\n");
			return new Integer(-2);
		}
	}
	
	private int HandleTRAP(int trapcode) {
		this.subject.updateUserConsole("Excute trap instruction sucess, trap code:" + trapcode);
		//Get memory content from 2
		Integer instruction_address = memory.GetValueWithInt(2);
		if (instruction_address == null) { 
			return -2;
		}
		cpu.SetPC(instruction_address.intValue());
		return 0;
	}
	
	private int HandleMFT(int faultCode) {
		this.subject.updateUserConsole("Excute machine fault instruction sucess, fault code:" + faultCode + "\n");
		//Get memory content from 4
		Integer instruction_address = memory.GetValueWithInt(4);
		if (instruction_address == null) { 
			return -2;
		}
		
		System.out.println("instruction_address is " + instruction_address.intValue());
		cpu.SetPC(instruction_address.intValue());
		return 0;
	}
	
	private int HandleLDR(int r, int ix, int address) {
		// Load Register From Memory, r = 0..3
		// r <- c(EA)
		// r <- c(c(EA)), if I bit set

		if (ix < 0 || ix >= 4) {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + ix + ". The range of IX is 0-3\n");
			this.subject.updateMFR(5);
			return -2;
		}
		
		if (r < 0 || r >= 4) {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + r + ". The range of GPR is 0-3\n");
			this.subject.updateMFR(6);
			return -2;
		}
		
		int ix_content = cpu.GetIX(ix);
		int addr = ix_content + address;
				
		Integer memory_content = memory.GetValueWithInt(addr);
		if (memory_content == null) { 
			this.subject.updateUserConsole("Failed to excute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}
		
		System.out.println("address is " + addr + " memory content is " + memory_content);
		int result = cpu.SetGPR(r, memory_content.intValue());
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: LDR " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to excute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleSTR(int r, int ix, int address) {
		// Store Register To Memory, r = 0..3
		// Memory(EA) <- c(r)

		if (ix < 0 || ix >= 4) {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + ix + ". The range of IX is 0-3\n");
			return -2;
		}
		
		if (r < 0 || r >= 4) {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + r + ". The range of GPR is 0-3\n");
			return -2;
		}
		
		int ix_content = cpu.GetIX(ix);			
		int addr = ix_content + address;
		if (addr <= 5) {
			this.subject.updateUserConsole("Illegal Memory Address to Reserved Locations, Error !!!!\n");
			cpu.SetMFR(0);
			return -2;
		}
		
		if (addr >= 2048) {
			this.subject.updateUserConsole("Out of memory (0-2047) Error !!!!\n");
			cpu.SetMFR(3);
			return -2;
		}
		
		int GPR_content = cpu.GetGPR(r);
		int result = memory.Set(addr, GPR_content);
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: STR " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to excute instruction: STR " + r + ", " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleLDA(int r, int ix, int address) {
		// Load Register with Address, r = 0..3
		// r <- EA

		if (ix < 0 || ix >= 4) {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + ix + ". The range of IX is 0-3\n");
			return -2;
		}
		
		if (r < 0 || r >= 4) {
			this.subject.updateUserConsole("Access GPR fail. Invalid GPR index:" + r + ". The range of GPR is 0-3\n");
			return -2;
		}
		
		int ix_content = cpu.GetIX(ix);
		int addr = ix_content + address;
		int result = cpu.SetGPR(r, addr);
		
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: LDA " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to excute instruction: LDA " + r + ", " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleLDX(int ix, int address) {
		//Load Index Register from Memory, x = 1..3. 
		//Xx <- c(EA)
		
		if (ix < 0 || ix >= 4) {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + ix + ". The range of IX is 0-3\n");
			return -2;
		}
				
		Integer memory_content = memory.GetValueWithInt(address);
		if (memory_content == null) { 
			return -2;
		}
	
		int result = cpu.SetIX(ix, memory_content.intValue());
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: LDX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to excute instruction: LDA " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleSTX(int ix, int address) {
		//Store Index Register to Memory. X = 1..3
		//Memory(EA) <- c(Xx)
		if (ix < 0 || ix >= 4) {
			this.subject.updateUserConsole("Access IX fail. Invalid IX index:" + ix + ". The range of IX is 0-3\n");
			return -2;
		}
		
		int ix_content = cpu.GetIX(ix);
		int result = memory.Set(address, ix_content);
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: STX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to excute instruction: STX " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
}
