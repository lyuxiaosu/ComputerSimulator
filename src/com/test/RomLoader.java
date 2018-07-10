package com.test;

import java.util.*;
public class RomLoader {
	
	private static final String[] rom_program = new String[] {
			"LDR 3, 1, 8", //Load register 3 with the contents of the memory location 10, NO indexing
			"STR 2, 0, 19" //Store register 2 to memory location 9, NO indexing
	};
	
	/*private static final String[] rom_program = new String[] {
			"LDA 2, 3, 17", //Load register 3 with the contents of the memory location 10, NO indexing
			"LDX 1, 8", //Store register 2 to memory location 9, NO indexing
			"STX 1, 25",
			"TRAP 15"
	};*/
	private CentralProcessor cpu;
	private Memory memory;
	public RomLoader(CentralProcessor cpu, Memory memory) {
		this.cpu = cpu;
		this.memory = memory;
	}
	
	public boolean LoadProgram() {
		//Initialize the reserved memory
		String trap_instruction = "TRAP 15";
		String fault_instruction = "MFT 6";
		boolean result = memory.SetReservedMemory(0, trap_instruction);
		if (result == false) {
			return false;
		}
		result = memory.SetReservedMemory(1,  fault_instruction);
		if (result == false) {
			return false;
		}
		
		int len = rom_program.length;
		
		for (int i = 0; i < len; i++) {
			result = memory.LoadContent(8 + i, rom_program[i]);
			if (result == false) {
				return false;
			}
		}
		cpu.SetPC(8);
		return result;
	}
	
	public int Execute() {
		return cpu.Execute();
	}
}
