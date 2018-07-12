package com.test;

import java.util.ArrayList;

public class SingalController extends AbstrctProcessor {
	private Memory memory;
	private CentralProcessor cpu;
	private IStop simulator;
	public SingalController (CentralProcessor cpu, Memory memory, IUpdate subject, IStop simulator) {
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
			ArrayList<Integer> array = (ArrayList<Integer>)(data);	
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
			} else {
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
		this.subject.updateUserConsole("Excute trap instruction: " + "TRAP " + trapcode +  " sucess\n");
		//Get memory content from 2
		Integer instruction_address = memory.GetValueWithInt(2);
		if (instruction_address == null) { 
			return -2;
		}
		cpu.SetPC(instruction_address.intValue());
		return 0;
	}
	
	private int HandleMFT(int faultCode) {
		this.subject.updateUserConsole("Excute machine fault instruction: " + "MFT " + faultCode + " sucess\n");
		//Get memory content from 4
		Integer instruction_address = memory.GetValueWithInt(4);
		if (instruction_address == null) { 
			return -2;
		}
		
		System.out.println("instruction_address is " + instruction_address.intValue());
		cpu.SetPC(instruction_address.intValue());
		return 0;
	}
	private int HandleHLT() {
		//
		this.simulator.stop();
		return 0;
	}
	private int HandleLDR(int r, int ix, int i, int address) {
		// Load Register From Memory, r = 0..3
		// r <- c(EA)
		// r <- c(c(EA)), if I bit set
				
		int ea = this.CalculateEA(ix, i, address);
		if (ea == -2) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		int value = 0;
		if (i == 0) {				
			Integer memory_content = memory.GetValueWithInt(ea);
			if (memory_content == null) {
				this.subject.updateUserConsole(
						"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
				this.subject.updateMFR(4);
				return -2;
			}
			value = memory_content.intValue();
			System.out.println("address is " + ea + " memory content is " + value);
		} else {
			System.out.println("indirect address\n");
			Integer memory_content = memory.GetValueWithInt(ea);
			if (memory_content == null) {
				this.subject.updateUserConsole(
						"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
				this.subject.updateMFR(4);
				return -2;
			}
			
			this.subject.updateUserConsole("c(EA) = " + memory_content.intValue() + "\n");
			memory_content = memory.GetValueWithInt(memory_content.intValue());
			if (memory_content == null) {
				this.subject.updateUserConsole(
						"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
				this.subject.updateMFR(4);
				return -2;
			}		
			value = memory_content.intValue();	
			this.subject.updateUserConsole("c(c(EA)) = " + memory_content.intValue() + "\n");
			
		}
		
		int result = cpu.SetGPR(r, value);
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: LDR " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleSTR(int r, int ix, int i, int address) {
		// Store Register To Memory, r = 0..3
		// Memory(EA) <- c(r)	
		
		int ea = this.CalculateEA(ix, i, address);
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: STR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer GPR_content = cpu.GetGPR(r);		
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: STR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		int result = memory.Set(ea, GPR_content);
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: STR " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: STR " + r + ", " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleLDA(int r, int ix, int i, int address) {
		// Load Register with Address, r = 0..3
		// r <- EA
		
		int ea = this.CalculateEA(ix, i, address);	
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}			
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		int result = cpu.SetGPR(r, ea);
		
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: LDA " + r + ", " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + r + ", " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleLDX(int ix, int i, int address) {
		//Load Index Register from Memory, x = 1..3. 
		//Xx <- c(EA)
		
		int ea = this.CalculateEA(0, i, address);
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer memory_content = memory.GetValueWithInt(ea);		
		if (memory_content == null) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: LDX " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}	
		
		int result = cpu.SetIX(ix, memory_content.intValue());
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: LDX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleSTX(int ix, int i, int address) {
		//Store Index Register to Memory. X = 1..3
		//Memory(EA) <- c(Xx)
		
		int ea = this.CalculateEA(0, i, address);
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer ix_content = cpu.GetIX(ix);
		if (ix_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			return -2;
		}

		int result = memory.Set(ea, ix_content.intValue());
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: STX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int CalculateEA(int ix, int i, int address) {
		Integer ix_content = cpu.GetIX(ix);
		if (ix_content == null) {
			return -2;
		}
				
		if (i == 0) { // no indirect addressing
			return (address + ix_content.intValue());
		} else { // indirect address
			Integer memory_content = memory.GetValueWithInt(address + ix_content.intValue());
			if (memory_content == null) {
				return -2;
			} 
			return memory_content.intValue();
		}
	}	

}
