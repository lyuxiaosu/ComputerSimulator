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
			} else if (opcode == 8) {//JZ
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJZ(r, ix, i, address);
			} else if (opcode == 9) { //JNE
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleJNE(r, ix, i, address);
			} else if (opcode == 10) { //JCC
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
			} else if (opcode == 14) { //SOB
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
			} else if (opcode == 4) { //AMR
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleAMR(r, ix, i, address);
			} else if (opcode == 5) { //SMR
				int r = array.get(1).intValue();
				int ix = array.get(2).intValue();
				int i = array.get(3).intValue();
				int address = array.get(4).intValue();
				return HandleSMR(r, ix, i, address);
			} else if (opcode == 6) { //AIR
				int r = array.get(1).intValue();
				int immed = array.get(4).intValue();
				return HandleAIR(r, immed);
			} else if (opcode == 7) { //SIR
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
			} else if (opcode == 18) { //TRR
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleTRR(rx, ry);
			} else if (opcode == 19) { //AND
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleAND(rx, ry);
			} else if (opcode == 20) { // ORR
				int rx = array.get(1).intValue();
				int ry = array.get(2).intValue();
				return HandleORR(rx, ry);
			} else if (opcode == 21) { //NOT
				int rx = array.get(1).intValue(); 
				return HandleNOT(rx);
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
		this.subject.updateUserConsole("Excute trap instruction: " + "TRAP " + trapcode +  " sucess\n");
		//Get memory content from 2
		Integer instruction_address = memory.GetValueWithInt(2, false);
		if (instruction_address == null) { 
			return -2;
		}
		cpu.SetPC(instruction_address.intValue()); // After executing the TRAP instruction, recover the PC to the value of memory location 2
												   // Because location 2 records the next program's address
		return 0;
	}
	
	private int HandleMFT(int faultCode) {
		this.subject.updateUserConsole("Excute machine fault instruction: " + "MFT " + faultCode + " sucess\n");
		//Get memory content from 4
		Integer instruction_address = memory.GetValueWithInt(4, false);
		if (instruction_address == null) { 
			return -2;
		}
		
		System.out.println("instruction_address is " + instruction_address.intValue());
		cpu.SetPC(instruction_address.intValue()); // After executing the MFT instruction, recover the PC to the value of memory location 4
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
			this.subject.updateUserConsole(
					"Failed to execute instruction: LDR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
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
			memory_content = memory.GetValueWithInt(memory_content.intValue(), false); // fetch the content from the content of the effective address
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
		
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
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
		
		int result = memory.Set(ea, GPR_content, false); // set GPR's content to M[ea]
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
		
		int ea = this.CalculateEA(ix, i, address);	// Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}			
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		int result = cpu.SetGPR(r, ea); // set GPR with ea
		
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
		
		int ea = this.CalculateEA(0, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: LDA " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer memory_content = memory.GetValueWithInt(ea, false); //fetch the content from the content of the effective address 	
		if (memory_content == null) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: LDX " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}	
		
		int result = cpu.SetIX(ix, memory_content.intValue()); // set IX with M[ea]
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
		
		int ea = this.CalculateEA(0, i, address);  // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer ix_content = cpu.GetIX(ix); // Get IX's content
		if (ix_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
			return -2;
		}

		int result = memory.Set(ea, ix_content.intValue(), false); //Set IX's content to location ea of the memory
		if (result == 0) {
			this.subject.updateUserConsole("Excute instruction success. Instruction: STX " + ix + ", " + address + "\n");
		} else {
			this.subject.updateUserConsole("Failed to execute instruction: STX " + ix + ", " + address + "\n");
		}
		
		return result;
	}
	
	private int HandleJZ(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: JZ " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		Integer GPR_content = cpu.GetGPR(r);		
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: JZ " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		if (GPR_content.intValue() == 0) {// if c(r) == 0, set PC to ea		
			cpu.SetPC(ea);
			this.subject.updateUserConsole("Execute instruction success. Instruction: JZ " + r + ", " + ix + ", " + address + "\n");
			 
		} else { // if c(r) != 0, PC will increase 1 by itself		
			this.subject.updateUserConsole("Execute instruction success. Instruction: JZ " + r + ", " + ix + ", " + address + "\n");		
		}
		return 0;
	}
	
	private int HandleJNE(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: JNE " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		Integer GPR_content = cpu.GetGPR(r);		
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: JNE " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		if (GPR_content.intValue() != 0) {// if c(r) != 0, set PC to ea		
			cpu.SetPC(ea);			 
		} else { // if c(r) == 0, PC will increase 1 by itself							
		}
		this.subject.updateUserConsole("Execute instruction success. Instruction: JNE " + r + ", " + ix + ", " + address + "\n");
		return 0;
	}
	
	private int HandleJCC(int cc, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: JCC " + cc + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		this.subject.updateUserConsole("EA is " + ea + "\n");
		boolean bit = cpu.GetCCRBit(cc);
		if (bit == true) { // if bit == true, set PC to ea
			cpu.SetPC(ea);
		} else { // if bit == false, PC will increase 1 by itself			
		}
		
		this.subject.updateUserConsole("Execute instruction success. Instruction: JCC " + cc + ", " + ix + ", " + address + "\n");
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
		//Since the instruction flows from PC to this object, the PC's value has already increased 1 in PC object,
		// here we don't need to increase PC's value by 1, we just need to save the current PC's value to R3
		int pc = Integer.parseInt(cpu.GetPC());
		cpu.SetGPR(3, pc);
		cpu.SetPC(ea);
		
		this.subject.updateUserConsole("Execute instruction success. Instruction: JSR " + ix + ", " + address + "\n");
		return 0;
	}
	private int HandleRFS(int immed) {
		//set immed to GPR-0
		cpu.SetGPR(0, immed);
		//set GPR-3's content to PC
		cpu.SetPC(cpu.GetGPR(3));
		return 0;
	}
	
	private int HandleSOB(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
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
			this.subject.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: SOB " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		if (GPR_content.intValue() >= 0) {
			cpu.SetPC(ea);
		} else {
			// PC will increase 1 automatically
		}
		
		return 0;
	}
	
	private int HandleAMR(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: AMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: AMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		Integer memory_content = memory.GetValueWithInt(ea, false); //fetch the content from the content of the effective address 	
		if (memory_content == null) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: AMR " + r + ", " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}	
		
		cpu.SetGPR(r, GPR_content.intValue() + memory_content.intValue());
		return 0;
	}
	
	private int HandleSMR(int r, int ix, int i, int address) {
		int ea = this.CalculateEA(ix, i, address); // Calculate the effective address
		if (ea == -2) {
			this.subject.updateUserConsole("Failed to execute instruction: SMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		this.subject.updateUserConsole("EA is " + ea + "\n");
		
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: SMR " + r + ", " + ix + ", " + address + "\n");
			return -2;
		}
		
		Integer memory_content = memory.GetValueWithInt(ea, false); //fetch the content from the content of the effective address 	
		if (memory_content == null) {
			this.subject.updateUserConsole(
					"Failed to execute instruction: SMR " + r + ", " + ix + ", " + address + "\n");
			this.subject.updateMFR(4);
			return -2;
		}	
		
		cpu.SetGPR(r, GPR_content.intValue() - memory_content.intValue());
		return 0;
	}
	
	private int HandleAIR(int r, int immed) {
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: AIR " + r + ", "+ immed + "\n");
			return -2;
		}
		
		if (immed > 15 || immed < -15) {
			this.subject.updateUserConsole("Invalid immed: " + immed + ", range should be [-15, 15]\n");
			return -2;
		}
		
		cpu.SetGPR(r, immed + GPR_content.intValue());
		return 0;
	}
	
	private int HandleSIR(int r, int immed) {
		Integer GPR_content = cpu.GetGPR(r);
		if (GPR_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: SIR " + r + ", "+ immed + "\n");
			return -2;
		}
		
		if (immed > 15 || immed < -15) {
			this.subject.updateUserConsole("Invalid immed: " + immed + ", range should be [-15, 15]\n");
			return -2;
		}
		
		cpu.SetGPR(r, GPR_content.intValue() - immed);
		return 0;
	}
	
	private int HandleMLT(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: MLT " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: MLT " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		int result = rx_content.intValue() * ry_content.intValue();
		if (result > 32767) {
			this.subject.updateUserConsole("OverFlow when Executing MLT " + rx + ", " + ry + ". Result=" + result + "\n");
			//set CC overflow
			cpu.SetCCRBit(0);
			return -2;
		} 
		if (result < -32767) {
			this.subject.updateUserConsole("OverFlow when Executing MLT " + rx + ", " + ry + ". Result=" + result + "\n");
			//set CC underflow
			cpu.SetCCRBit(1);
			return -2;
		}
		//reset CCR
		cpu.ResetCCR();
		int high_bits = (result>>8)&0XFF;
		int low_bits = result&0xFF;
		this.cpu.SetGPR(rx, high_bits);
		this.cpu.SetGPR(rx + 1, low_bits);
		this.subject.updateUserConsole("Execute Instruction success: MLT " + rx + ", " + ry + ". result=" + result+ "\n");
		return 0;
	}
	
	private int HandleDVD(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: DVD " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: DVD " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		if (ry_content.intValue() == 0) {
			this.subject.updateUserConsole("ry is 0. Failed to execute instruction: DVD " + rx + ", " + ry + "\n");
			this.cpu.SetCCRBit(2);
			return -2;
		}
		//reset CCR
		cpu.ResetCCR();
		int quotient = rx_content.intValue() / ry_content.intValue();
		int remainder = rx_content.intValue() % ry_content.intValue();
		
		this.cpu.SetGPR(rx, quotient);
		this.cpu.SetGPR(rx + 1, remainder);
		this.subject.updateUserConsole("Execute Instruction success: DVD " + rx + ", " + ry + ". quotient=" + quotient + ", remainder=" + remainder+ "\n");
		return 0;
	}
	
	private int HandleTRR(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: TRR " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: TRR " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		if (ry_content.intValue() == rx_content.intValue()) {
			this.subject.updateUserConsole("Execute Instruction success: TRR " + rx + ", " + ry + ". c(rx)=" + rx_content.intValue() + " equal to c(ry)=" + ry_content.intValue() + "\n");
			this.cpu.SetCCRBit(3);
		} else {
			this.subject.updateUserConsole("Execute Instruction success: TRR " + rx + ", " + ry + ". c(rx)=" + rx_content.intValue() + " not equal to c(ry)=" + ry_content.intValue() + "\n");
			this.cpu.ResetCCR();
		}
		
		return 0;
	}
	
	private int HandleAND(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: AND " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: AND " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		int and = rx_content.intValue() & ry_content.intValue();
		cpu.SetGPR(rx, and);
		
		this.subject.updateUserConsole("Execute Instruction success: AND " + rx + ", "+ ry + ". and result=" + and + "\n");
		
		return 0;
	}
	
	private int HandleORR(int rx, int ry) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: ORR " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		Integer ry_content = cpu.GetGPR(ry);
		if (ry_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: ORR " + rx + ", "+ ry + "\n");
			return -2;
		}
		
		int and = rx_content.intValue() | ry_content.intValue();
		cpu.SetGPR(rx, and);
		
		this.subject.updateUserConsole("Execute Instruction success: ORR " + rx + ", "+ ry + ". and result=" + and + "\n");
		
		return 0;
	}
	
	private int HandleNOT(int rx) {
		Integer rx_content = cpu.GetGPR(rx);
		if (rx_content == null) {
			this.subject.updateUserConsole("Failed to execute instruction: NOT " + rx + "\n");
			return -2;
		}
		
		short content = (short)rx_content.intValue();
		short not = (short)~(content);
		cpu.SetGPR(rx, not);
		this.subject.updateUserConsole("Execute Instruction success: NOT " + rx + "NOT " + content + " is " + not + "\n");
		
		return 0;
	}
	/**
	 * Calculate the effective address
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

}
